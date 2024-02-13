package dev.ilkerk.leasing.application.contract.dto.response;

import dev.ilkerk.leasing.application.customer.dto.response.CustomerResponse;
import dev.ilkerk.leasing.domain.contract.entity.ContractStatus;
import dev.ilkerk.leasing.domain.customer.preference.PaymentCalculationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String contractNumber;
    private ContractStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private CustomerResponse sellerCustomer;
    private CustomerResponse takerCustomer;
    private UUID userId;
    private UUID addressId;
    private Double specialAreaPrice;
    private PaymentCalculationType paymentCalculationType;
    private Double totalArea;
    private Double totalPrice;
    private List<TransactionResponse> transactionList = new ArrayList<>();
    private List<NoteResponse> noteList = new ArrayList<>();
}
