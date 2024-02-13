package dev.ilkerk.leasing.application.product.dto.request.stock;

import dev.ilkerk.leasing.domain.product.entity.StockAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDTO {
	private StockAction action;
	@NotNull
	private Double amount;
}
