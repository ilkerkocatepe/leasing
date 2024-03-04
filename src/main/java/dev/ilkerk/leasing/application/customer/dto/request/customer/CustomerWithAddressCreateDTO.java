package dev.ilkerk.leasing.application.customer.dto.request.customer;

import dev.ilkerk.leasing.application.customer.dto.request.address.AddressCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWithAddressCreateDTO {
	private String title;
	private String logoUrl;
	private String taxNumber;
	private String taxAdministration;
	private String mersisNumber;
	private String phoneNumber;
	private AddressCreateDTO address;
	private String email;
//	private UserCreateDTO user;
}
