package dev.ilkerk.leasing.domain.contract.entity;

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

@Table("waybills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waybill {
	@Id
	private UUID id;
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
	private String serialNumber;
	private String description;
	private UUID senderCustomerId;
	private UUID receiverCustomerId;
	private UUID contractId;
	private UUID receiverAddressId;
	private UUID senderAddressId;
}
