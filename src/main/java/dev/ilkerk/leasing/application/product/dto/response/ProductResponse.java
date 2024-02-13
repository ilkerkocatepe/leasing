package dev.ilkerk.leasing.application.product.dto.response;

import dev.ilkerk.leasing.domain.product.entity.ProductUnit;
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
public class ProductResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String image;
    private String imageUrl;
    private Double price;
    private Double factor;
    private ProductUnit unit;
    private UUID categoryId;
    private UUID customerId;
    private StockResponse stock;
}
