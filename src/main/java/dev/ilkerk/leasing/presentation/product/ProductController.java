package dev.ilkerk.leasing.presentation.product;

import dev.ilkerk.leasing.application.product.dto.request.product.ProductCreateDTO;
import dev.ilkerk.leasing.application.product.dto.request.product.ProductFindDTO;
import dev.ilkerk.leasing.application.product.dto.response.ProductResponse;
import dev.ilkerk.leasing.application.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ProductResponse> getById(@PathVariable UUID id) {
		try {
			return productService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<ProductResponse> getAll(@ModelAttribute @Valid ProductFindDTO productFindDTO) {
		try {
			return productService.getAllByCriteria(productFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ProductResponse> create(@RequestBody @Valid ProductCreateDTO productCreateDTO, Authentication authentication) {
		try {
			return productService.create(productCreateDTO, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ProductResponse> update(@PathVariable UUID id, @RequestBody @Valid ProductCreateDTO productCreateDTO) {
		try {
			return productService.update(id, productCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return productService.deleteById(id);
	}

}
