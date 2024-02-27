package dev.ilkerk.leasing.application.contract.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String modifiedBy;
    private String serialNumber;
    private Double amount;
    private Boolean isPaid;
    private String description;
    private Double specialAreaPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID contractId;
    private UUID customerId;
    private UUID discountId;
    private UUID invoiceId;
    private ContractResponse contract;
    private List<TransactionResponse> transactions;
    private Map<String, Double> products = new HashMap<>();
    private DiscountResponse discount;
}
