package dev.ilkerk.leasing.infrastracture.security.jwt;

import dev.ilkerk.leasing.application.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String CUSTOMER_ID_KEY = "customerId";
    private static final String USER_ID_KEY = "userId";

    private final JwtProperties jwtProperties;
    private final UserService userService;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        var secret = Base64.getEncoder()
                .encodeToString(this.jwtProperties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        var claimsBuilder = Jwts.claims().subject(username);
        if (!authorities.isEmpty()) {
            claimsBuilder.add(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        claimsBuilder.add(CUSTOMER_ID_KEY, userService.findCustomerIdByUsername(username).toFuture().join().toString());
        claimsBuilder.add(USER_ID_KEY, userService.findUserIdByUsername(username).toFuture().join().toString());

        var claims = claimsBuilder.build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + this.jwtProperties.getValidityInMs());

        return Jwts.builder().claims(claims).issuedAt(now).expiration(validity)
                .signWith(this.secretKey, Jwts.SIG.HS256).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith(this.secretKey).build()
                .parseSignedClaims(token).getPayload();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        String customerId = claims.get(CUSTOMER_ID_KEY, String.class);
        String userId = claims.get(USER_ID_KEY, String.class);

        User principal = new User(claims.getSubject(), "", authorities);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, token, authorities);
        usernamePasswordAuthenticationToken.setDetails(Map.of(CUSTOMER_ID_KEY, customerId, USER_ID_KEY, userId));

        return usernamePasswordAuthenticationToken;
    }

    public boolean validateToken(String token) {
        try {
            log.info("validateToken: {}", token);
            Jws<Claims> claims = Jwts.parser().verifyWith(this.secretKey)
                    .build().parseSignedClaims(token);
            // parseClaimsJws will check expiration date. No need do here.
            log.info("expiration date: {}", claims.getPayload().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }

}
