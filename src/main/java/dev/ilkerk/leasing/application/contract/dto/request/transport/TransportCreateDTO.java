package dev.ilkerk.leasing.application.contract.dto.request.transport;

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
public class TransportCreateDTO {
	private String description;
	private TransportType transportType;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private UUID senderCustomerId;
	private UUID receiverCustomerId;
	private UUID contractId;
}
