package dev.ilkerk.leasing.application.product.dto.request.productCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryCreateDTO {
	@NotBlank(message = "Name is mandatory")
	@Size(min = 3)
	private String name;
	@NotBlank(message = "Image is mandatory")
	private String image;
}
