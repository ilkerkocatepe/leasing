package dev.ilkerk.leasing.domain.user.repository;

import dev.ilkerk.leasing.domain.user.entity.CustomerUsers;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerUsersRepository extends R2dbcRepository<CustomerUsers, UUID> {
    Mono<CustomerUsers> findByUserId(UUID userId);
}
