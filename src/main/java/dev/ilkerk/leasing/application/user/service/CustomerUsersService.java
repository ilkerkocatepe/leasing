package dev.ilkerk.leasing.application.user.service;

import dev.ilkerk.leasing.domain.user.repository.CustomerUsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerUsersService {
    private final CustomerUsersRepository customerUsersRepository;

    public Mono<UUID> findCustomerIdByUserId(UUID userId) {
        return customerUsersRepository.findByUserId(userId).flatMap(customerUsers -> Mono.just(customerUsers.getCustomerId()));
    }
}
