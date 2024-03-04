package dev.ilkerk.leasing.application.contract.service;

import com.itextpdf.html2pdf.HtmlConverter;
import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceCreate;
import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceFind;
import dev.ilkerk.leasing.application.contract.dto.response.AllowanceResponse;
import dev.ilkerk.leasing.application.contract.dto.response.DiscountResponse;
import dev.ilkerk.leasing.application.contract.dto.response.html.AllowanceHtml;
import dev.ilkerk.leasing.application.contract.dto.response.html.ConditionsHtml;
import dev.ilkerk.leasing.application.contract.dto.response.html.ProductsHtml;
import dev.ilkerk.leasing.application.contract.dto.response.html.TransactionsHtml;
import dev.ilkerk.leasing.application.customer.dto.response.AddressResponse;
import dev.ilkerk.leasing.application.product.service.ProductService;
import dev.ilkerk.leasing.domain.contract.entity.Allowance;
import dev.ilkerk.leasing.domain.contract.repository.AllowanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
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
    private final SpringWebFluxTemplateEngine templateEngine;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public Mono<AllowanceResponse> get(UUID id) {
        return allowanceRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Allowance not found.")))
                .flatMap(allowance -> Mono.just(modelMapper.map(allowance, AllowanceResponse.class)))
                .flatMap(allowanceResponse -> {
                    return contractService.get(allowanceResponse.getContractId()).flatMap(contractResponse -> {
                        allowanceResponse.setContract(contractResponse);
                        allowanceResponse.setTransactions(contractResponse.getTransactionList());

                        if (allowanceResponse.getSpecialAreaPrice() == null) {
                            allowanceResponse.setSpecialAreaPrice(contractResponse.getSpecialAreaPrice());
                        }

                        return Mono.just(allowanceResponse);
                    });
                })
                .flatMap(allowanceResponse -> {
                    if (allowanceResponse.getDiscountId() == null) {
                        return Mono.just(allowanceResponse);
                    }

                    return discountService.get(allowanceResponse.getDiscountId()).flatMap(discountResponse -> {
                        allowanceResponse.setDiscount(discountResponse);
                        return Mono.just(allowanceResponse);
                    });
                })
                .flatMap(allowanceResponse -> {
                    if (allowanceResponse.getStartTime() == null) {
                        allowanceResponse.setStartTime(allowanceResponse.getContract().getStartAt());
                    }

                    return transactionService.calculateTransactions(allowanceResponse.getContractId(), allowanceResponse.getStartTime(), allowanceResponse.getEndTime()).flatMap(transactions -> {
//                        Double specialAreaPrice = allowanceResponse.getSpecialAreaPrice() == null ? allowanceResponse.getContract().getSpecialAreaPrice() : allowanceResponse.getSpecialAreaPrice();
//
//                        allowanceResponse.setAmount(transactions.values().stream().reduce(0.0, Double::sum) * specialAreaPrice);

                        log.info("Transactions: " + transactions);
                        return Flux.fromIterable(transactions.entrySet())
                                .flatMap(transaction -> productService.get(transaction.getKey()).flatMap(productResponse -> {
                                    allowanceResponse.getProducts().put(productResponse.getName(), transaction.getValue());
                                    return Mono.just(allowanceResponse.getProducts());
                                }))
                                .flatMap(productResponses -> {
                                    allowanceResponse.setProducts(productResponses);
                                    return Flux.just(allowanceResponse);
                                }).next();
                    }).thenReturn(allowanceResponse);
                });
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

    public Mono<Long> countAllByCustomerId(UUID customerId) {
        return allowanceRepository.countAllByCustomerId(customerId);
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
                    Allowance allowance = modelMapper.map(allowanceResponse, Allowance.class);
                    allowance.setModifiedBy(modifiedBy);
                    allowance.setAmount(allowanceResponse.getAmount());
                    allowance.setCustomerId(allowanceResponse.getContract().getSellerCustomer().getId());

                    log.info("Allowance mapped: " + allowance);

                    return allowanceRepository.save(allowance)
                            .flatMap(allowance1 -> {
                                log.info("Allowance saved: " + allowance1.toString());

                                allowanceResponse.setId(allowance1.getId());
                                allowanceResponse.setCreatedAt(allowance1.getCreatedAt());
                                allowanceResponse.setUpdatedAt(allowance1.getUpdatedAt());
                                allowanceResponse.setModifiedBy(allowance1.getModifiedBy());

                                return Mono.just(allowanceResponse);
                            })
                            .flatMap(allowanceResponse1 -> {
                                log.info("Allowance response2: " + allowanceResponse1);
                                if (allowanceCreate.getDiscount() == null) {
                                    return Mono.just(allowanceResponse1);
                                }

                                return discountService.create(allowanceCreate.getDiscount()).flatMap(discountResponse -> {
                                    allowanceResponse1.setModifiedBy(modifiedBy);
                                    allowanceResponse1.setDiscount(discountResponse);
                                    allowanceResponse1.setDiscountId(discountResponse.getId());
                                    return Mono.just(allowanceResponse1);
                                });
                            });
                });
    }

    public Mono<String> generateSerialNumber(String sellerName, UUID customerId) {
        return this.countAllByCustomerId(customerId)
                .map(count -> WordUtils.initials(sellerName) + "-" + StringUtils.leftPad(String.valueOf(count + 1), 6, "0"));
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
                        double amount = (transactions.values().stream().reduce(0.0, Double::sum) * allowanceResponse1.getSpecialAreaPrice());

                        if (amount <= 0) {
                            return Mono.error(new RuntimeException("Allowance amount must be greater than 0"));
                        }

                        allowanceResponse1.setAmount(amount);

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
                    if (allowanceCreate.getDiscount() == null ||
                            allowanceCreate.getDiscount().getAmount() == null ||
                            allowanceCreate.getDiscount().getAmount() <= 0) {
                        return Mono.just(allowanceResponse1);
                    }

                    DiscountResponse discountResponse = modelMapper.map(allowanceCreate.getDiscount(), DiscountResponse.class);
                    allowanceResponse1.setDiscount(discountResponse);
                    allowanceResponse1.setAmount(allowanceResponse1.getAmount() - discountResponse.getAmount());

                    return Mono.just(allowanceResponse1);
                })
                .flatMap(allowanceResponse1 -> {
                    return this.generateSerialNumber(allowanceResponse1.getContract().getSellerCustomer().getTitle(), allowanceResponse1.getContract().getSellerCustomer().getId()).flatMap(serialNumber -> {
                        allowanceResponse1.setSerialNumber(serialNumber);
                        return Mono.just(allowanceResponse1);
                    });
                });
    }

    private Mono<AllowanceHtml> getForHtml(AllowanceResponse allowanceResponse) {
        AllowanceHtml allowanceHtml = new AllowanceHtml();

        if (allowanceResponse.getDiscount() != null && allowanceResponse.getDiscount().getAmount() > 0) {
            allowanceHtml.setDiscount(df.format(allowanceResponse.getDiscount().getAmount()));
        }

        allowanceHtml.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        allowanceHtml.setLogo(allowanceResponse.getContract().getSellerCustomer().getLogoUrl());
        allowanceHtml.setDealerTitle(allowanceResponse.getContract().getSellerCustomer().getTitle());
        allowanceHtml.setSerialNumber(allowanceResponse.getSerialNumber());
        allowanceHtml.setStartTime(allowanceResponse.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        allowanceHtml.setEndTime(allowanceResponse.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        allowanceHtml.setDealerAddress(allowanceResponse.getContract().getSellerCustomer().getAddressList().stream().filter(AddressResponse::getIsMain).findFirst().get().getCity());
        allowanceHtml.setDealerPhoneNumber(allowanceResponse.getContract().getSellerCustomer().getPhoneNumber());
        allowanceHtml.setDealerEmail("");
        allowanceHtml.setCustomerTitle(allowanceResponse.getContract().getTakerCustomer().getTitle());
        allowanceHtml.setCustomerAddress(allowanceResponse.getContract().getTakerCustomer().getAddressList().stream().filter(AddressResponse::getIsMain).findFirst().get().getCity());
        allowanceHtml.setCustomerPhoneNumber(allowanceResponse.getContract().getTakerCustomer().getPhoneNumber());
        allowanceHtml.setCustomerEmail("");
        allowanceHtml.setConditions(ConditionsHtml.from(Map.of("Günlük m2 Fiyatı", String.valueOf(allowanceResponse.getSpecialAreaPrice()))));
        allowanceHtml.setTransactions(TransactionsHtml.from(allowanceResponse.getTransactions()));
        allowanceHtml.setProducts(ProductsHtml.from(allowanceResponse.getProducts()));
        allowanceHtml.setTotalAmount(df.format(allowanceResponse.getAmount()));

        log.info("Allowance html: " + allowanceHtml);

        return Mono.just(allowanceHtml);
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

    public Mono<String> previewHtml(AllowanceCreate allowanceCreate) {
        return this.preview(allowanceCreate)
                .flatMap(this::getForHtml)
                .flatMap(
                        allowanceHtml -> {
                            Context context = new Context(Locale.of("tr_TR"), Map.of("allowance", allowanceHtml));
                            String html = templateEngine.process("allowance", context);
                            return Mono.just(html);
                        }
                );
    }

    public Mono<String> html(UUID allowanceId) {
        return this.get(allowanceId)
                .flatMap(this::getForHtml)
                .flatMap(
                        allowanceHtml -> {
                            Context context = new Context(Locale.of("tr_TR"), Map.of("allowance", allowanceHtml));
                            String html = templateEngine.process("allowance", context);
                            return Mono.just(html);
                        }
                );
    }

    public Mono<ResponseEntity<byte[]>> download(UUID allowanceId) {
        return this.get(allowanceId)
                .flatMap(this::getForHtml)
                .flatMap(
                        allowanceHtml -> {
                            Context context = new Context(Locale.of("tr_TR"), Map.of("allowance", allowanceHtml));
                            String html = templateEngine.process("allowance", context);
                            ByteArrayOutputStream pdf = new ByteArrayOutputStream();

                            String fileName = "allowance_" + allowanceHtml.getSerialNumber() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";

                            HtmlConverter.convertToPdf(html, pdf);

                            return Mono.just(ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                                    .contentType(MediaType.APPLICATION_PDF)
                                    .body(pdf.toByteArray()));
                        }
                );
    }
}
