package dev.ilkerk.leasing.application.user.dto.response;

import dev.ilkerk.leasing.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String name;
    private String email;
    private boolean active;
    private List<Role> roles;
}
