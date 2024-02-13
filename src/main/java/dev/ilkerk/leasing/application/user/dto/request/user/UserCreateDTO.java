package dev.ilkerk.leasing.application.user.dto.request.user;

import dev.ilkerk.leasing.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
	private String name;
	private String username;
	private String email;
	private String password;
	private Boolean active;
	private List<Role> roles;
}
