package dev.ilkerk.leasing.application.contract.dto.request.transport;

import dev.ilkerk.leasing.domain.contract.entity.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.matching;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportFindDTO {
	private UUID id;
	private String description;
	private TransportType transportType;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private UUID senderCustomerId;
	private UUID receiverCustomerId;
	private UUID contractId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("description", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("transportType", exact())
				.withMatcher("startAt", exact())
				.withMatcher("endAt", exact())
				.withMatcher("senderCustomerId", exact())
				.withMatcher("receiverCustomerId", exact())
				.withMatcher("contractId", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase());
	}
}
