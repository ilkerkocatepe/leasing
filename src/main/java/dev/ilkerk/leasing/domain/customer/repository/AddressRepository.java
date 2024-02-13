package dev.ilkerk.leasing.domain.customer.repository;

import dev.ilkerk.leasing.domain.customer.entity.Address;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<Address, UUID> {
    Flux<Address> findAllByCustomerId(UUID customerId);
}
