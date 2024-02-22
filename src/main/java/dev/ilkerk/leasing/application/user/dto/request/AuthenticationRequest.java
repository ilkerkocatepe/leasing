package dev.ilkerk.leasing.application.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
