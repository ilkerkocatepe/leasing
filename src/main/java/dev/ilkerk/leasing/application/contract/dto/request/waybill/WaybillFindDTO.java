package dev.ilkerk.leasing.application.contract.dto.request.waybill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.matching;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaybillFindDTO {
	private UUID id;
	private String serialNumber;
	private String description;
	private UUID customerId;
	private UUID receiverCustomerId;
	private UUID contractId;
	private UUID receiverAddressId;
	private UUID senderAddressId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("serialNumber", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("description", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("senderCustomerId", exact())
				.withMatcher("receiverCustomerId", exact())
				.withMatcher("contractId", exact())
				.withMatcher("receiverAddressId", exact())
				.withMatcher("senderAddressId", exact());
	}
}
