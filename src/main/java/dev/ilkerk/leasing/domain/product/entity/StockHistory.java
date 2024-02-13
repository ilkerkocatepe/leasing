package dev.ilkerk.leasing.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("stock_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHistory {
    @Id
    private UUID id;
    @CreatedDate
    private LocalDateTime createdAt;
    private StockAction action;
    private Double amount;
    private Double totalAmount;
    private UUID productId;
    private UUID stockId;
}
