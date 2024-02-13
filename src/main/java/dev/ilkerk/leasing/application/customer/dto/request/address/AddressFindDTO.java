package dev.ilkerk.leasing.application.customer.dto.request.address;

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
public class AddressFindDTO {
	private String name;
	private String details;
	private String district;
	private String city;
	private String country;
	private String zipcode;
	private String description;
	private UUID customerId;
	private Boolean isMain;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("name", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("details", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("district", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("city", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("country", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("zipcode", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("description", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("customerId", exact())
				.withMatcher("isMain", exact());
	}
}
