package dev.ilkerk.leasing.application.contract.dto.response;

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
public class DiscountResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String modifiedBy;
    private Double amount;
    private Double beforeAmount;
    private Double afterAmount;
    private String description;
    private UUID allowanceId;
}
