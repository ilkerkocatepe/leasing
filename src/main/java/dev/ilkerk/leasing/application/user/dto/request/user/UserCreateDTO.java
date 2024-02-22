package dev.ilkerk.leasing.application.user.dto.request.user;

import dev.ilkerk.leasing.domain.user.entity.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
	private String name;
	@Email
	private String email;
	private String password;
	private Boolean active;
	private UUID customerId;
	private List<Role> roles;
}
