package dev.ilkerk.leasing.domain.user.repository;

import dev.ilkerk.leasing.domain.user.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    Mono<User> findByUsername(String username);
}
