package dev.ilkerk.leasing.presentation.product;

import dev.ilkerk.leasing.application.product.dto.request.productCategory.ProductCategoryCreateDTO;
import dev.ilkerk.leasing.application.product.dto.request.productCategory.ProductCategoryFindDTO;
import dev.ilkerk.leasing.application.product.dto.response.ProductCategoryResponse;
import dev.ilkerk.leasing.application.product.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("product-category")
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryController {
	private final ProductCategoryService productCategoryService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ProductCategoryResponse> getById(@PathVariable UUID id) {
		try {
			return productCategoryService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<ProductCategoryResponse> getAll(Authentication authentication, @ModelAttribute @Valid ProductCategoryFindDTO productCategoryFindDTO) {
		try {
			productCategoryFindDTO.setCustomerId(UUID.fromString(((Map<String, String>) authentication.getDetails()).get("customerId")));
			return productCategoryService.getAllByCriteria(productCategoryFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ProductCategoryResponse> create(Authentication authentication, @Valid @RequestBody ProductCategoryCreateDTO productCategoryCreateDTO) {
		try {
			productCategoryCreateDTO.setCustomerId(UUID.fromString(((Map<String, String>) authentication.getDetails()).get("customerId")));
			return productCategoryService.create(productCategoryCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ProductCategoryResponse> update(@PathVariable UUID id, @RequestBody @Valid ProductCategoryCreateDTO productCategoryCreateDTO) {
		try {
			return productCategoryService.update(id, productCategoryCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return productCategoryService.deleteById(id);
	}
}
