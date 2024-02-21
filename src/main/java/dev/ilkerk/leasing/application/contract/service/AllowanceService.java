package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceCreate;
import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceFind;
import dev.ilkerk.leasing.application.contract.dto.response.AllowanceResponse;
import dev.ilkerk.leasing.application.product.service.ProductService;
import dev.ilkerk.leasing.domain.contract.entity.Allowance;
import dev.ilkerk.leasing.domain.contract.repository.AllowanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllowanceService {
    private final AllowanceRepository allowanceRepository;
    private final ModelMapper modelMapper;
    private final DiscountService discountService;
    private final ContractService contractService;
    private final TransactionService transactionService;
    private final ProductService productService;

    public Mono<AllowanceResponse> get(UUID id) {
        return allowanceRepository.findById(id)
                .flatMap(allowance -> Mono.just(modelMapper.map(allowance, AllowanceResponse.class)));
    }

    public Mono<Allowance> getObject(UUID id) {
        return allowanceRepository.findById(id);
    }

    public Flux<AllowanceResponse> getAllByCriteria(AllowanceFind allowanceFind) {
        Allowance allowance = modelMapper.map(allowanceFind, Allowance.class);

        Example<Allowance> allowanceExample = Example.of(allowance, AllowanceFind.getExampleMatcher());

        return allowanceRepository.findAll(allowanceExample)
                .flatMap(allowance1 -> Mono.just(modelMapper.map(allowance1, AllowanceResponse.class)));
    }

    public Mono<LocalDateTime> getLastEndTimeByContractId(UUID contractId) {
        return allowanceRepository.findFirstByContractIdOrderByEndTimeDesc(contractId)
                .switchIfEmpty(Mono.empty())
                .flatMap(allowance -> Mono.just(allowance.getEndTime()));
    }

    public Mono<AllowanceResponse> create(AllowanceCreate allowanceCreate, String modifiedBy) {
        log.info("Allowance creating: " + allowanceCreate.toString());

        return this.preview(allowanceCreate)
                .flatMap(allowanceResponse -> {
                    log.info("Allowance preview: " + allowanceResponse.toString());
                    Allowance allowance = modelMapper.map(allowanceCreate, Allowance.class);
                    allowance.setModifiedBy(modifiedBy);
                    allowance.setAmount(allowanceResponse.getAmount());

                    log.info("Allowance mapped: " + allowance);

                    return allowanceRepository.save(allowance)
                            .flatMap(allowance1 -> {
                                log.info("Allowance saved: " + allowance1.toString());

                                allowanceResponse.setId(allowance1.getId());
                                allowanceResponse.setCreatedAt(allowance1.getCreatedAt());
                                allowanceResponse.setUpdatedAt(allowance1.getUpdatedAt());
                                allowanceResponse.setModifiedBy(allowance1.getModifiedBy());

                                return Mono.just(allowanceResponse);
                            });
                });
    }

    public Mono<AllowanceResponse> preview(AllowanceCreate allowanceCreate) {
        AllowanceResponse allowanceResponse = modelMapper.map(allowanceCreate, AllowanceResponse.class);

        return Mono.just(allowanceResponse)
                .flatMap(allowanceResponse1 -> {
                    return this.getLastEndTimeByContractId(allowanceCreate.getContractId()).map(lastAllowanceEndTime -> {
                        log.info("Last allowance: " + lastAllowanceEndTime);
                        allowanceResponse1.setStartTime(lastAllowanceEndTime);

                        return allowanceResponse1;
                    }).thenReturn(allowanceResponse1);
                })
                .flatMap(allowanceResponse1 -> contractService.get(allowanceCreate.getContractId()).flatMap(contractResponse -> {
                    log.info("Contract: " + contractResponse);
                    allowanceResponse1.setContract(contractResponse);
                    allowanceResponse1.setTransactions(contractResponse.getTransactionList());

                    if (allowanceResponse1.getSpecialAreaPrice() == null) {
                        allowanceResponse1.setSpecialAreaPrice(contractResponse.getSpecialAreaPrice());
                    }

                    return Mono.just(allowanceResponse1);
                }))
                .flatMap(allowanceResponse1 -> {
                    log.info("Allowance response: " + allowanceResponse1);
                    if (allowanceResponse1.getStartTime() == null) {
                        allowanceResponse1.setStartTime(allowanceResponse.getContract().getStartAt());
                    }

                    return transactionService.calculateTransactions(allowanceCreate.getContractId(), allowanceResponse.getStartTime(), allowanceCreate.getEndTime()).flatMap(transactions -> {
                        allowanceResponse1.setAmount(transactions.values().stream().reduce(0.0, Double::sum) * allowanceResponse1.getSpecialAreaPrice());

                        log.info("Transactions: " + transactions);
                        return Flux.fromIterable(transactions.entrySet())
                                .flatMap(transaction -> productService.get(transaction.getKey()).flatMap(productResponse -> {
                                    allowanceResponse1.getProducts().put(productResponse.getName(), transaction.getValue());
                                    return Mono.just(allowanceResponse1.getProducts());
                                }))
                                .flatMap(productResponses -> {
                                    allowanceResponse1.setProducts(productResponses);
                                    return Flux.just(allowanceResponse1);
                                }).next();
                    }).thenReturn(allowanceResponse1);
                })
                .flatMap(allowanceResponse1 -> {
                    log.info("Allowance response2: " + allowanceResponse1);
                    if (allowanceCreate.getDiscount() == null) {
                        return Mono.just(allowanceResponse1);
                    }

                    return discountService.create(allowanceCreate.getDiscount()).flatMap(discountResponse -> {
                        allowanceResponse1.setDiscount(discountResponse);
                        allowanceResponse1.setDiscountId(discountResponse.getId());
                        return Mono.just(allowanceResponse1);
                    });
                });
    }

    public Mono<AllowanceResponse> update(UUID id, AllowanceCreate allowanceCreate) {
        log.info("Allowance updating: " + allowanceCreate.toString());

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new RuntimeException("Allowance not found.")))
                .flatMap(optionalAllowance -> {
                    if (optionalAllowance.isPresent()) {
                        Allowance updatedAllowance = this.getUpdatedAllowance(optionalAllowance.get(), allowanceCreate);

                        return allowanceRepository.save(updatedAllowance);
                    }
                    return Mono.empty();
                })
                .flatMap(allowance -> Mono.just(modelMapper.map(allowance, AllowanceResponse.class)));
    }

    private Allowance getUpdatedAllowance(Allowance allowance, AllowanceCreate allowanceCreate) {

        log.info("Updated allowance object: " + allowance);

        return allowance;
    }

    public Mono<Void> deleteById(UUID id) {
        log.info("Allowance deleting: " + id);

        return allowanceRepository.deleteById(id);
    }
}
