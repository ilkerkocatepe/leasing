package dev.ilkerk.leasing.application.contract.dto.request.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.matching;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractFindDTO {
	private UUID id;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private UUID sellerCustomerId;
	private UUID takerCustomerId;
	private UUID userId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("startAt", exact())
				.withMatcher("endAt", exact())
				.withMatcher("sellerCustomerId", exact())
				.withMatcher("takerCustomerId", exact())
				.withMatcher("userId", exact());
	}
}
