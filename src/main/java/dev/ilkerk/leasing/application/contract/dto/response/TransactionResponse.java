package dev.ilkerk.leasing.application.contract.dto.response;

import dev.ilkerk.leasing.application.product.dto.response.ProductResponse;
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
public class TransactionResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String receiptNumber;
    private TransportType type;
    private Double amount;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private ProductResponse product;
    private UUID productId;
}
