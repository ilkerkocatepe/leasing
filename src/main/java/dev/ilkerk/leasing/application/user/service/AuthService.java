package dev.ilkerk.leasing.application.user.service;

import dev.ilkerk.leasing.application.customer.service.CustomerService;
import dev.ilkerk.leasing.application.user.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomerService customerService;

    public Mono<AuthResponse> getAuthResponse(Authentication authentication) {
        log.info("Authenticating user: {}", authentication);
        return userService.get(UUID.fromString(((Map<String, String>) authentication.getDetails()).get("userId")))
                .flatMap(user -> customerService.get(UUID.fromString(((Map<String, String>) authentication.getDetails()).get("customerId")))
                        .map(customer -> AuthResponse.builder()
                                .user(user)
                                .customer(customer)
                                .build()));
    }
}
