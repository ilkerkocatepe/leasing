package dev.ilkerk.leasing.domain.user.exception;

import org.springframework.security.access.AccessDeniedException;

public class UnauthorizedException extends AccessDeniedException {
    public UnauthorizedException() {
        super("Yetkisiz Erişim!");
    }
}
