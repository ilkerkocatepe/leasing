package dev.ilkerk.leasing.application.product.dto.response;

import dev.ilkerk.leasing.domain.product.entity.StockAction;
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
public class StockHistoryResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private StockAction action;
    private Double amount;
    private Double totalAmount;
}
