package dev.ilkerk.leasing.application.contract.dto.request.note;

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
public class NoteFindDTO {
	private UUID id;
	private String text;
	private UUID userId;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("text", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("userId", exact());
	}
}
