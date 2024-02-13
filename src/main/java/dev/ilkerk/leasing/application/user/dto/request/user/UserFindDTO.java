package dev.ilkerk.leasing.application.user.dto.request.user;

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
public class UserFindDTO {
	private UUID id;
	private String name;
	private String email;

	public static ExampleMatcher getExampleMatcher() {
		return matching()
				.withMatcher("id", exact())
				.withMatcher("name", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
				.withMatcher("email", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase());
	}
}
