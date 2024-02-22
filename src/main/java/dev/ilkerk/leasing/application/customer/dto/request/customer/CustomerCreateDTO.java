package dev.ilkerk.leasing.application.customer.dto.request.customer;

import dev.ilkerk.leasing.application.user.dto.request.user.UserCreateDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDTO {
	@NotBlank
	private String title;
	private String logo;
	@NotBlank
	private String taxNumber;
	private String taxAdministration;
	private String mersisNumber;
	private String phoneNumber;
	private UserCreateDTO user;
}
