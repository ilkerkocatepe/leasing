package dev.ilkerk.leasing.application.product.dto.request.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCreateDTO {
	private Double amount;
	private UUID productId;
	private UUID customerId;
}
