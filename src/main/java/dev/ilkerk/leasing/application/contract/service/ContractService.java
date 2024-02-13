package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.contract.ContractCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.ContractFindDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.TransactionWithContractCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.TransactionWithContractUpdateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.note.NoteCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.response.ContractResponse;
import dev.ilkerk.leasing.application.contract.dto.response.TransactionResponse;
import dev.ilkerk.leasing.application.customer.service.CustomerPreferenceService;
import dev.ilkerk.leasing.application.customer.service.CustomerService;
import dev.ilkerk.leasing.application.user.service.UserService;
import dev.ilkerk.leasing.domain.contract.entity.Contract;
import dev.ilkerk.leasing.domain.contract.entity.ContractStatus;
import dev.ilkerk.leasing.domain.contract.entity.TransportType;
import dev.ilkerk.leasing.domain.contract.repository.ContractRepository;
import dev.ilkerk.leasing.domain.customer.preference.PaymentCalculationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;
    private final NoteService noteService;
    private final CustomerService customerService;
    private final CustomerPreferenceService customerPreferenceService;
    private final UserService userService;

    public Mono<ContractResponse> get(UUID id) {
        return contractRepository.findById(id).flatMap(contract -> {
            ContractResponse contractResponse = modelMapper.map(contract, ContractResponse.class);

            return Mono.just(contractResponse)
                    .flatMap(contractResponse1 -> customerService.get(contract.getSellerCustomerId()).flatMap(sellerCustomer -> {
                        contractResponse1.setSellerCustomer(sellerCustomer);

                        return Mono.just(contractResponse1);
                    }))
                    .flatMap(contractResponse1 -> customerService.get(contract.getTakerCustomerId()).flatMap(takerCustomer -> {
                        contractResponse1.setTakerCustomer(takerCustomer);

                        return Mono.just(contractResponse1);
                    }))
                    .flatMap(contractResponse1 -> transactionService.getAllByContractId(contract.getId()).collectList().flatMap(transactions -> {
                        contractResponse1.setTransactionList(transactions);

                        return Mono.just(contractResponse1);
                    }))
                    .flatMap(contractResponse1 -> noteService.getAllByContractId(contract.getId()).collectList().flatMap(notes -> {
                        contractResponse1.setNoteList(notes);

                        return Mono.just(contractResponse1);
                    }));
        });
    }

    private Mono<Contract> getObject(UUID id) {
        return contractRepository.findById(id);
    }

    public Flux<ContractResponse> getAllByCriteria(ContractFindDTO contractFindDTO) {
        Contract contract = modelMapper.map(contractFindDTO, Contract.class);

        Example<Contract> contractExample = Example.of(contract, ContractFindDTO.getExampleMatcher());

        return contractRepository.findAll(contractExample)
                .flatMap(contract0 -> {
                    ContractResponse contractResponse = modelMapper.map(contract0, ContractResponse.class);

                    return Mono.just(contractResponse)
                            .flatMap(contractResponse1 -> customerService.get(contract0.getSellerCustomerId()).flatMap(sellerCustomer -> {
                                contractResponse1.setSellerCustomer(sellerCustomer);

                                return Mono.just(contractResponse1);
                            }))
                            .flatMap(contractResponse1 -> customerService.get(contract0.getTakerCustomerId()).flatMap(takerCustomer -> {
                                contractResponse1.setTakerCustomer(takerCustomer);

                                return Mono.just(contractResponse1);
                            }));
                })
                .flatMap(contractResponse -> {
                    Flux<TransactionResponse> transactionList = transactionService.getAllByContractId(contractResponse.getId())
                            .defaultIfEmpty(new TransactionResponse());

                    return Flux.combineLatest(transactionList.collectList(), Flux.just(contractResponse), (transactions, contractResponse1) -> {
                        contractResponse1.setTransactionList(transactions);

                        return contractResponse1;
                    });
                })
                .flatMap(contractResponse -> noteService.getAllByContractId(contractResponse.getId()).collectList().flatMap(notes -> {
                    contractResponse.setNoteList(notes);

                    return Mono.just(contractResponse);
                }));
    }

    public Mono<ContractResponse> create(ContractCreateDTO contractCreateDTO) {
        log.info("Contract creating: " + contractCreateDTO.toString());

        Contract contract = modelMapper.map(contractCreateDTO, Contract.class);

        log.debug("Created contract object: " + contract);

        return contractRepository.save(contract)
                .flatMap(contract1 -> Mono.just(modelMapper.map(contract1, ContractResponse.class)));
    }

    public Mono<ContractResponse> createWithTransaction(TransactionWithContractCreateDTO transactionWithContractCreateDTO, Authentication authentication) {
        log.info("Contract creating: " + transactionWithContractCreateDTO.toString());

        Map<String, String> userIdAndCustomerId = userService.getUserIdAndCustomerId(authentication);
        transactionWithContractCreateDTO.setSellerCustomerId(UUID.fromString(userIdAndCustomerId.get("customerId")));
        transactionWithContractCreateDTO.setUserId(UUID.fromString(userIdAndCustomerId.get("userId")));
        transactionWithContractCreateDTO.setStatus(ContractStatus.ACTIVE);

        ContractCreateDTO contractCreateDTO = modelMapper.map(transactionWithContractCreateDTO, ContractCreateDTO.class);

        return this.create(contractCreateDTO)
                .flatMap(contractResponse -> Mono.just(contractResponse)
                        .flatMap(contractResponse1 -> customerService.get(transactionWithContractCreateDTO.getSellerCustomerId()).flatMap(sellerCustomer -> {
                            contractResponse1.setSellerCustomer(sellerCustomer);

                            return Mono.just(contractResponse1);
                        })))
                .flatMap(contractResponse -> Mono.just(contractResponse)
                        .flatMap(contractResponse1 -> customerService.get(transactionWithContractCreateDTO.getTakerCustomerId()).flatMap(takerCustomer -> {
                            contractResponse1.setTakerCustomer(takerCustomer);

                            return Mono.just(contractResponse1);
                        })))
                .flatMap(contractResponse -> {
                    transactionWithContractCreateDTO.getTransactionList().forEach(transactionCreateDTO -> {
                        this.isTransactionValid(contractResponse.getTransactionList(), transactionCreateDTO.getType(), transactionCreateDTO.getProductId(), transactionCreateDTO.getAmount());

                        transactionCreateDTO.setContractId(contractResponse.getId());
                    });

                    return transactionService.createAll(transactionWithContractCreateDTO.getTransactionList())
                            .collectList()
                            .flatMap(transactionList -> {
                                contractResponse.setTransactionList(transactionList);

                                return Mono.just(contractResponse);
                            });
                })
                .flatMap(contractResponse -> noteService.createAll(transactionWithContractCreateDTO.getNoteList(), transactionWithContractCreateDTO.getUserId(), contractResponse)
                        .collectList()
                        .flatMap(noteList -> {
                            contractResponse.setNoteList(noteList);

                            return Mono.just(contractResponse);
                        }));
    }

    public Mono<ContractResponse> update(UUID id, ContractCreateDTO contractCreateDTO) {
        log.info("Contract updating: " + contractCreateDTO.toString());

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new Exception("Contract not found")))
                .flatMap(optionalContract -> {
                    if (optionalContract.isPresent()) {
                        Contract contract = this.getUpdatedContract(optionalContract.get(), contractCreateDTO);

                        return contractRepository.save(contract);
                    }
                    return Mono.empty();
                })
                .flatMap(contract0 -> {
                    ContractResponse contractResponse = modelMapper.map(contract0, ContractResponse.class);

                    return Mono.just(contractResponse)
                            .flatMap(contractResponse1 -> customerService.get(contract0.getSellerCustomerId()).flatMap(sellerCustomer -> {
                                contractResponse1.setSellerCustomer(sellerCustomer);

                                return Mono.just(contractResponse1);
                            }))
                            .flatMap(contractResponse1 -> customerService.get(contract0.getTakerCustomerId()).flatMap(takerCustomer -> {
                                contractResponse1.setTakerCustomer(takerCustomer);

                                return Mono.just(contractResponse1);
                            }));
                })
                .flatMap(contractResponse -> transactionService.getAllByContractId(contractResponse.getId()).collectList().flatMap(transactions -> {
                    contractResponse.setTransactionList(transactions);

                    return Mono.just(contractResponse);
                }))
                .flatMap(contractResponse -> noteService.getAllByContractId(contractResponse.getId()).collectList().flatMap(notes -> {
                    contractResponse.setNoteList(notes);

                    return Mono.just(contractResponse);
                }));
    }

    private Contract getUpdatedContract(Contract contract, ContractCreateDTO contractCreateDTO) {
        if (contractCreateDTO.getContractNumber() != null) {
            contract.setContractNumber(contractCreateDTO.getContractNumber());
        }

        if (contractCreateDTO.getStatus() != null) {
            contract.setStatus(contractCreateDTO.getStatus());
        }

        if (contractCreateDTO.getStartAt() != null) {
            contract.setStartAt(contractCreateDTO.getStartAt());
        }

        if (contractCreateDTO.getEndAt() != null) {
            contract.setEndAt(contractCreateDTO.getEndAt());
        }

        if (contractCreateDTO.getSpecialAreaPrice() != null) {
            contract.setSpecialAreaPrice(contractCreateDTO.getSpecialAreaPrice());
        }

        if (contractCreateDTO.getSellerCustomerId() != null) {
            contract.setSellerCustomerId(contractCreateDTO.getSellerCustomerId());
        }

        if (contractCreateDTO.getTakerCustomerId() != null) {
            contract.setTakerCustomerId(contractCreateDTO.getTakerCustomerId());
        }

        if (contractCreateDTO.getUserId() != null) {
            contract.setUserId(contractCreateDTO.getUserId());
        }

        log.info("Updated contract object: " + contract);

        return contract;
    }

    public Mono<ContractResponse> updateWithTransaction(UUID id, TransactionWithContractUpdateDTO transactionWithContractUpdateDTO) {
        log.info("Contract updating: " + transactionWithContractUpdateDTO.toString());

        ContractCreateDTO contractCreateDTO = modelMapper.map(transactionWithContractUpdateDTO, ContractCreateDTO.class);

        return this.update(id, contractCreateDTO)
                .flatMap(contractResponse -> {
                    return Flux.fromIterable(transactionWithContractUpdateDTO.getTransactionList())
                            .flatMap(transactionUpdateDTO -> {
                                this.isTransactionValid(contractResponse.getTransactionList(), transactionUpdateDTO.getType(), transactionUpdateDTO.getProductId(), transactionUpdateDTO.getAmount());

                                if (transactionUpdateDTO.getId() == null) {
                                    transactionUpdateDTO.setContractId(contractResponse.getId());
                                    return transactionService.create(modelMapper.map(transactionUpdateDTO, TransactionCreateDTO.class));
                                }

                                return transactionService.update(transactionUpdateDTO.getId(), modelMapper.map(transactionUpdateDTO, TransactionCreateDTO.class));
                            })
                            .collectList()
                            .flatMap(transactionList -> {
                                contractResponse.setTransactionList(transactionList);

                                return Mono.just(contractResponse);
                            });
                });
    }

    public Mono<Void> deleteById(UUID id) {
        log.info("Contract deleting: " + id);

        return contractRepository.deleteById(id);
    }

    public Mono<ContractResponse> addNoteToContract(UUID id, String note, Authentication authentication) {
        log.info("Note adding to contract: " + id);

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new Exception("Contract not found")))
                .flatMap(optionalContract -> {
                    if (optionalContract.isPresent()) {
                        Contract contract = optionalContract.get();

                        return Mono.just(modelMapper.map(contract, ContractResponse.class))
                                .flatMap(contractResponse -> noteService.create(NoteCreateDTO.builder().text(note).build(), authentication, contractResponse).flatMap(noteResponse -> {
                                    contractResponse.getNoteList().add(noteResponse);

                                    return Mono.just(contractResponse);
                                }))
                                .flatMap(contractResponse -> transactionService.getAllByContractId(contract.getId()).collectList().flatMap(transactions -> {
                                    contractResponse.setTransactionList(transactions);

                                    return Mono.just(contractResponse);
                                }))
                                .flatMap(contractResponse -> noteService.getAllByContractId(contract.getId()).collectList().flatMap(notes -> {
                                    contractResponse.setNoteList(notes);

                                    return Mono.just(contractResponse);
                                }));
                    }
                    return Mono.empty();
                });
    }

    public Mono<ContractResponse> preview(TransactionWithContractCreateDTO transactionWithContractCreateDTO, Authentication authentication) {
        Map<String, String> userIdAndCustomerId = userService.getUserIdAndCustomerId(authentication);
        transactionWithContractCreateDTO.setSellerCustomerId(UUID.fromString(userIdAndCustomerId.get("customerId")));
        transactionWithContractCreateDTO.setUserId(UUID.fromString(userIdAndCustomerId.get("userId")));
        transactionWithContractCreateDTO.setStatus(ContractStatus.PREPARING);

        ContractResponse contractResponse = modelMapper.map(transactionWithContractCreateDTO, ContractResponse.class);

        return Mono.just(contractResponse)
                .flatMap(contractResponse1 -> customerService.get(transactionWithContractCreateDTO.getSellerCustomerId()).flatMap(sellerCustomer -> {
                    contractResponse1.setSellerCustomer(sellerCustomer);
                    return Mono.just(contractResponse1);
                }))
                .flatMap(contractResponse1 -> customerPreferenceService.getPaymentCalculationType(transactionWithContractCreateDTO.getSellerCustomerId()).flatMap(paymentCalculationType -> {
                    contractResponse1.setPaymentCalculationType(paymentCalculationType);
                    return Mono.just(contractResponse1);
                }))
                .flatMap(contractResponse1 -> {
                    if (transactionWithContractCreateDTO.getSpecialAreaPrice() != null) {
                        contractResponse1.setSpecialAreaPrice(transactionWithContractCreateDTO.getSpecialAreaPrice());
                        return Mono.just(contractResponse1);
                    }

                    return customerPreferenceService.getSpecialAreaPrice(transactionWithContractCreateDTO.getSellerCustomerId()).flatMap(specialAreaPrice -> {
                        contractResponse1.setSpecialAreaPrice(specialAreaPrice);
                        return Mono.just(contractResponse1);
                    });
                })
                .flatMap(contractResponse1 -> customerService.get(transactionWithContractCreateDTO.getTakerCustomerId()).flatMap(takerCustomer -> {
                    contractResponse1.setTakerCustomer(takerCustomer);
                    return Mono.just(contractResponse1);
                }))
                .flatMap(contractResponse1 -> {
                    transactionWithContractCreateDTO.getTransactionList().forEach(transactionCreateDTO -> {
                        this.isTransactionValid(contractResponse.getTransactionList(), transactionCreateDTO.getType(), transactionCreateDTO.getProductId(), transactionCreateDTO.getAmount());

                        transactionCreateDTO.setContractId(contractResponse.getId());
                    });

                    return transactionService.preview(transactionWithContractCreateDTO.getTransactionList())
                            .collectList()
                            .flatMap(transactionList -> {
                                contractResponse.setTransactionList(transactionList);

                                return this.calculateTotalArea(contractResponse);
                            })
                            .flatMap(this::calculateTotalPrice);
                });
    }

    private Mono<ContractResponse> calculateTotalArea(ContractResponse contractResponse) {
        AtomicReference<Double> totalArea = new AtomicReference<>(0.0);

        contractResponse.getTransactionList().forEach(transactionResponse -> {
            totalArea.updateAndGet(v -> v + (transactionResponse.getProduct().getFactor() * transactionResponse.getAmount()));
        });

        contractResponse.setTotalArea(totalArea.get());

        return Mono.just(contractResponse);
    }

    private Mono<ContractResponse> calculateTotalPrice(ContractResponse contractResponse) {
        if (contractResponse.getPaymentCalculationType().equals(PaymentCalculationType.AREA)) {
            contractResponse.setTotalPrice(contractResponse.getTotalArea() * contractResponse.getSpecialAreaPrice());

            return Mono.just(contractResponse);
        } else {
            AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);

            contractResponse.getTransactionList().forEach(transactionResponse -> {
                totalPrice.updateAndGet(v -> v + (transactionResponse.getProduct().getPrice() * transactionResponse.getAmount()));
            });

            contractResponse.setTotalPrice(totalPrice.get());

            return Mono.just(contractResponse);
        }
    }

    private void isTransactionValid(List<TransactionResponse> transactionResponseList, TransportType type, UUID productId, Double amount) {
        transactionService.isTransactionValid(transactionResponseList, type, productId, amount);
    }
}
