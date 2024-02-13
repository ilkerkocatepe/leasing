package dev.ilkerk.leasing.application.customer.dto.request.customerPreference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreferenceCreateDTO {
	private String name;
	private String value;
	private String description;
	private UUID customerId;
}
