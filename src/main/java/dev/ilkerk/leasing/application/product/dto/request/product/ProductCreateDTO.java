package dev.ilkerk.leasing.application.product.dto.request.product;

import dev.ilkerk.leasing.domain.product.entity.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
	private String name;
	private String image;
	private Double price = 0.0;
	private Double factor = 1.0;
	private Double stock = 0.0;
	private ProductUnit unit;
	private UUID categoryId;
}
