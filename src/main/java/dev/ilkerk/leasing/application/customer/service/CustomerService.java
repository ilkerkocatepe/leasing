package dev.ilkerk.leasing.application.customer.service;

import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerFindDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerWithAddressCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.response.AddressResponse;
import dev.ilkerk.leasing.application.customer.dto.response.CustomerResponse;
import dev.ilkerk.leasing.application.user.dto.request.user.UserCreateDTO;
import dev.ilkerk.leasing.application.user.service.UserService;
import dev.ilkerk.leasing.domain.customer.entity.Customer;
import dev.ilkerk.leasing.domain.customer.repository.CustomerRepository;
import dev.ilkerk.leasing.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final CustomerPreferenceService customerPreferenceService;
    private final UserService userService;

    @Value("${aws.customer-image-folder}")
    private String bucketImageFolder;

    public Mono<CustomerResponse> get(UUID id) {
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    CustomerResponse customerResponse = modelMapper.map(customer, CustomerResponse.class);

                    if (customerResponse.getLogo() != null) {
                        customerResponse.setLogoUrl(bucketImageFolder + customerResponse.getLogo());
                    }

                    return Mono.just(customerResponse)
                            .flatMap(customerResponse1 -> addressService.getAllByCustomerId(customer.getId()).collectList()
                                    .flatMap(addressResponses -> {
                                        customerResponse1.setAddressList(addressResponses);

                                        return Mono.just(customerResponse1);
                                    }))
                            .flatMap(customerResponse1 -> customerPreferenceService.getAllByCustomerId(customer.getId()).collectList()
                                    .flatMap(customerPreferenceResponses -> {
                                        customerPreferenceResponses.forEach(customerPreferenceResponse -> {
                                            customerResponse1.getCustomerPreferences().put(customerPreferenceResponse.getName(), customerPreferenceResponse.getValue());
                                        });

                                        return Mono.just(customerResponse1);
                                    }));
                });
    }

    public Mono<Customer> getObject(UUID id) {
        return customerRepository.findById(id);
    }

    public Flux<CustomerResponse> getAllByCriteria(CustomerFindDTO customerFindDTO) {
        Customer customer = modelMapper.map(customerFindDTO, Customer.class);

        Example<Customer> customerExample = Example.of(customer, CustomerFindDTO.getExampleMatcher());

        return customerRepository.findAll(customerExample)
                .flatMap(customer1 -> {
                    CustomerResponse customerResponse = modelMapper.map(customer1, CustomerResponse.class);

                    if (customerResponse.getLogo() != null) {
                        customerResponse.setLogoUrl(bucketImageFolder + customerResponse.getLogo());
                    }

                    Flux<AddressResponse> addressList = addressService.getAllByCustomerId(customer1.getId())
                            .defaultIfEmpty(new AddressResponse());

                    return Flux.combineLatest(addressList.collectList(), Flux.just(customerResponse), (addressResponses, customerResponse1) -> {
                        customerResponse1.setAddressList(addressResponses);

                        return customerResponse1;
                    });
                })
                .flatMap(customerResponse -> customerPreferenceService.getAllByCustomerId(customerResponse.getId()).collectList()
                        .flatMap(customerPreferenceResponses -> {
                            customerPreferenceResponses.forEach(customerPreferenceResponse -> {
                                customerResponse.getCustomerPreferences().put(customerPreferenceResponse.getName(), customerPreferenceResponse.getValue());
                            });

                            return Mono.just(customerResponse);
                        }));
    }

    public Mono<CustomerResponse> create(CustomerCreateDTO customerCreateDTO) {
        log.info("Customer creating: " + customerCreateDTO.toString());

        Customer customer = modelMapper.map(customerCreateDTO, Customer.class);

        log.debug("Created customer object: " + customer);

        return customerRepository.save(customer)
                .flatMap(customer1 -> {
                    CustomerResponse customerResponse = modelMapper.map(customer1, CustomerResponse.class);

                    if (customerResponse.getLogo() != null) {
                        customerResponse.setLogoUrl(bucketImageFolder + customerResponse.getLogo());
                    }

                    return Mono.just(customerResponse);
                })
                .flatMap(customerResponse -> addressService.getAllByCustomerId(customerResponse.getId()).collectList().flatMap(addressResponses -> {
                    customerResponse.setAddressList(addressResponses);

                    return Mono.just(customerResponse);
                }))
                .flatMap(customerResponse -> customerPreferenceService.getAllByCustomerId(customerResponse.getId()).collectList().flatMap(customerPreferenceResponses -> {
                    customerPreferenceResponses.forEach(customerPreferenceResponse -> {
                        customerResponse.getCustomerPreferences().put(customerPreferenceResponse.getName(), customerPreferenceResponse.getValue());
                    });

                    return Mono.just(customerResponse);
                }))
                .flatMap(customerResponse -> {
                    UserCreateDTO userCreateDTO = modelMapper.map(customerCreateDTO.getUser(), UserCreateDTO.class);
                    userCreateDTO.setCustomerId(customerResponse.getId());
                    userCreateDTO.setActive(Boolean.TRUE);
                    userCreateDTO.getRoles().add(Role.CUSTOMER);

                    return userService.create(userCreateDTO)
                            .flatMap(userResponse -> {
                                customerResponse.setUser(userResponse);

                                return Mono.just(customerResponse);
                            });
                });
    }

    public Mono<CustomerResponse> createWithAddress(CustomerWithAddressCreateDTO customerWithAddressCreateDTO) {
        log.info("Customer with address creating: " + customerWithAddressCreateDTO.toString());

        CustomerCreateDTO customerCreateDTO = modelMapper.map(customerWithAddressCreateDTO, CustomerCreateDTO.class);

        return this.create(customerCreateDTO)
                .flatMap(customerResponse -> {
                    if (customerResponse.getLogo() != null) {
                        customerResponse.setLogoUrl(bucketImageFolder + customerResponse.getLogo());
                    }

                    customerWithAddressCreateDTO.getAddress().setCustomerId(customerResponse.getId());

                    return addressService.create(customerWithAddressCreateDTO.getAddress())
                            .flatMap(addressResponse -> {
                                customerResponse.getAddressList().add(addressResponse);

                                return Mono.just(customerResponse);
                            });
                });
    }

    public Mono<CustomerResponse> update(UUID id, CustomerCreateDTO customerCreateDTO) {
        log.info("Customer updating: " + customerCreateDTO.toString());

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new Exception("Customer not found")))
                .flatMap(optionalCustomer -> {
                    if (optionalCustomer.isPresent()) {
                        Customer customer = this.getUpdatedCustomer(optionalCustomer.get(), customerCreateDTO);

                        return customerRepository.save(customer);
                    }
                    return Mono.empty();
                })
                .flatMap(customer1 -> {
                    CustomerResponse customerResponse = modelMapper.map(customer1, CustomerResponse.class);

                    if (customerResponse.getLogo() != null) {
                        customerResponse.setLogoUrl(bucketImageFolder + customerResponse.getLogo());
                    }

                    return Mono.just(customerResponse).flatMap(customerResponse1 -> addressService.getAllByCustomerId(customer1.getId()).collectList().flatMap(addressResponses -> {
                                customerResponse1.setAddressList(addressResponses);

                                return Mono.just(customerResponse1);
                            }));
                })
                .flatMap(customerResponse -> customerPreferenceService.getAllByCustomerId(customerResponse.getId()).collectList().flatMap(customerPreferenceResponses -> {
                    customerPreferenceResponses.forEach(customerPreferenceResponse -> {
                        customerResponse.getCustomerPreferences().put(customerPreferenceResponse.getName(), customerPreferenceResponse.getValue());
                    });

                    return Mono.just(customerResponse);
                }));
    }

    private Customer getUpdatedCustomer(Customer customer, CustomerCreateDTO customerCreateDTO) {
        if (customerCreateDTO.getTitle() != null) {
            customer.setTitle(customerCreateDTO.getTitle());
        }

        if (customerCreateDTO.getLogo() != null) {
            customer.setLogo(customerCreateDTO.getLogo());
        }

        if (customerCreateDTO.getTaxNumber() != null) {
            customer.setTaxNumber(customerCreateDTO.getTaxNumber());
        }

        if (customerCreateDTO.getTaxAdministration() != null) {
            customer.setTaxAdministration(customerCreateDTO.getTaxAdministration());
        }

        if (customerCreateDTO.getMersisNumber() != null) {
            customer.setMersisNumber(customerCreateDTO.getMersisNumber());
        }

        if (customerCreateDTO.getPhoneNumber() != null) {
            customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());
        }

        log.info("Updated customer object: " + customer);

        return customer;
    }

    public Mono<Void> deleteById(UUID id) {
        log.info("Customer deleting: " + id);

        return customerRepository.deleteById(id);
    }
}
