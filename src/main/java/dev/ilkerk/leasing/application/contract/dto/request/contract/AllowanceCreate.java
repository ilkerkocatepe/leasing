package dev.ilkerk.leasing.application.contract.dto.request.contract;

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
public class AllowanceCreate {
    private Boolean isPaid;
    private String description;
    private Double specialAreaPrice;
    private LocalDateTime endTime;
    private UUID contractId;
    private DiscountCreate discount;
}
