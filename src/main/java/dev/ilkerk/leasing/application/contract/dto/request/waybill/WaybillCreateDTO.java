package dev.ilkerk.leasing.application.contract.dto.request.waybill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaybillCreateDTO {
	private String serialNumber;
	private String description;
	private UUID senderCustomerId;
	private UUID receiverCustomerId;
	private UUID contractId;
	private UUID receiverAddressId;
	private UUID senderAddressId;
}
