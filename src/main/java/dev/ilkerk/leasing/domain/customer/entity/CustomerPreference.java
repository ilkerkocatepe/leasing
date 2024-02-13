package dev.ilkerk.leasing.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("customer_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreference {
	@Id
	private UUID id;
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
	private String name;
	private String value;
	private String description;
	private UUID customerId;
}
