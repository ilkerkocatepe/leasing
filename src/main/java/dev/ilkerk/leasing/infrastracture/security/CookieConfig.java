package dev.ilkerk.leasing.infrastracture.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
public class CookieConfig {
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("JSESSIONID");
        resolver.addCookieInitializer((builder) -> builder.path("/"));
        resolver.addCookieInitializer((builder) -> builder.sameSite("Strict"));
        resolver.addCookieInitializer((builder) -> builder.secure(true));
        resolver.addCookieInitializer((builder) -> builder.maxAge(7 * 24 * 60 * 60)); // expires in 7 days
        resolver.addCookieInitializer((builder) -> builder.httpOnly(true));
        return resolver;
    }
}
