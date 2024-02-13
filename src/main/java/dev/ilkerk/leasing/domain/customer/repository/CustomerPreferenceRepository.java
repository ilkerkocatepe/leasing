package dev.ilkerk.leasing.domain.customer.repository;

import dev.ilkerk.leasing.domain.customer.entity.CustomerPreference;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CustomerPreferenceRepository extends R2dbcRepository<CustomerPreference, UUID> {
    Flux<CustomerPreference> findByCustomerIdAndNameOrderByIdDesc(UUID customerId, String key);
    Flux<CustomerPreference> findAllByCustomerId(UUID customerId);
}
