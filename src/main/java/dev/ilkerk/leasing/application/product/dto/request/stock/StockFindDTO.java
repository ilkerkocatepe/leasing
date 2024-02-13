package dev.ilkerk.leasing.application.product.dto.request.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.matching;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockFindDTO {
	private UUID id;
	private UUID productId;
	private UUID customerId;

	public static ExampleMatcher getExampleMatcher() {
		return  matching()
				.withMatcher("id", exact())
				.withMatcher("customerId", exact())
				.withMatcher("productId", exact());
	}
}
