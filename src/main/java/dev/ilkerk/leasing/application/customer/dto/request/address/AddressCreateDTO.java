package dev.ilkerk.leasing.application.customer.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateDTO {
	@NotBlank
	private String name;
	@NotBlank
	private String details;
	@NotBlank
	private String district;
	@NotBlank
	private String city;
	@NotBlank
	private String country;
	private String zipcode;
	private String description;
	private UUID customerId;
	private Boolean isMain;
}
