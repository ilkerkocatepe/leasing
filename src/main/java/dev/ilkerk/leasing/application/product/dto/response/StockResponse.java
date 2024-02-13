package dev.ilkerk.leasing.application.product.dto.response;

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
public class StockResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double totalAmount;
    private Double availableAmount;
    private UUID productId;
    private UUID customerId;
    private List<StockHistoryResponse> stockHistoryList = new ArrayList<>();
}
