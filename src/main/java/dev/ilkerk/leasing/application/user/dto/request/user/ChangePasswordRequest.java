package dev.ilkerk.leasing.application.user.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @Size(min = 8, message = "Old password must be at least 8 characters long")
    @Size(max = 30, message = "Old password must be at most 30 characters long")
    private String oldPassword;
    @Size(min = 8, message = "New password must be at least 8 characters long")
    @Size(max = 30, message = "New password must be at most 30 characters long")
    private String newPassword;
}
