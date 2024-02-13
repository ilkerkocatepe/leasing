package dev.ilkerk.leasing.application.contract.dto.request.contract;

import dev.ilkerk.leasing.application.contract.dto.request.note.NoteUpdateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionUpdateDTO;
import dev.ilkerk.leasing.domain.contract.entity.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionWithContractUpdateDTO {
	private String contractNumber;
	private ContractStatus status;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Double specialAreaPrice;
	private UUID sellerCustomerId;
	private UUID takerCustomerId;
	private UUID userId;
	private UUID addressId;
	private List<TransactionUpdateDTO> transactionList;
	private List<NoteUpdateDTO> noteList;
}
