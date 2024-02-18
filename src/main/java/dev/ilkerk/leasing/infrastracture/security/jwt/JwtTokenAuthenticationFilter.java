package dev.ilkerk.leasing.infrastracture.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession().flatMap(session -> {
            if (session.getAttributes().get("token") == null) {
                return chain.filter(exchange);
            }

            String token = session.getAttributes().get("token").toString();
            log.debug("Session token: {}", token);

            if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
                return Mono.fromCallable(() -> this.tokenProvider.getAuthentication(token))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(authentication -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
            }

            return chain.filter(exchange);
        });
    }
}
