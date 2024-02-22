package dev.ilkerk.leasing.presentation.user;

import dev.ilkerk.leasing.application.user.dto.request.AuthenticationRequest;
import dev.ilkerk.leasing.application.user.dto.response.AuthResponse;
import dev.ilkerk.leasing.application.user.service.AuthService;
import dev.ilkerk.leasing.infrastracture.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("login")
    public Mono<AuthResponse> login(
            @Valid @RequestBody Mono<AuthenticationRequest> authRequest, WebSession session) {
        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.email(), login.password()))
                        .map(tokenProvider::createToken)
                        .flatMap(jwt -> {
                            session.getAttributes().put("token", jwt);
                            return authService.getAuthResponse(this.tokenProvider.getAuthentication(jwt));
                        }));
    }

    @GetMapping("me")
    public Mono<AuthResponse> current(@AuthenticationPrincipal Authentication authentication) {
        return authService.getAuthResponse(authentication);
    }
}
