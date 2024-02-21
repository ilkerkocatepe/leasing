package dev.ilkerk.leasing.application.contract.dto.request.transaction;

import dev.ilkerk.leasing.domain.contract.entity.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateDTO {
	private String receiptNumber;
	private TransportType type;
	private Double amount;
	private String description;
	private LocalDateTime issueDate;
	private UUID contractId;
	private UUID productId;
}
