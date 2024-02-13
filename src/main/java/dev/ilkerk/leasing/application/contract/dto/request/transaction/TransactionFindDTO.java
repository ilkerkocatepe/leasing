package dev.ilkerk.leasing.application.contract.dto.request.transaction;

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
public class TransactionFindDTO {
	private UUID id;
	private String receiptNumber;
	private TransportType type;
	private Double amount;
	private String description;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private UUID contractId;
	private UUID productId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("receiptNumber", exact())
				.withMatcher("type", exact())
				.withMatcher("amount", exact())
				.withMatcher("description", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("startAt", exact())
				.withMatcher("endAt", exact())
				.withMatcher("contractId", exact())
				.withMatcher("productId", exact());
	}
}
