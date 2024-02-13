package dev.ilkerk.leasing.application.product.dto.request.product;

import dev.ilkerk.leasing.domain.product.entity.ProductUnit;
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
public class ProductFindDTO {
	private UUID id;
	private String name;
	private ProductUnit unit;
	private UUID categoryId;
	private UUID customerId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("name", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("unit", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("categoryId", exact())
				.withMatcher("customerId", exact());
	}
}
