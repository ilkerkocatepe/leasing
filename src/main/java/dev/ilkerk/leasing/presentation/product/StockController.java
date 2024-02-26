package dev.ilkerk.leasing.presentation.product;

import dev.ilkerk.leasing.application.product.dto.request.stock.StockFindDTO;
import dev.ilkerk.leasing.application.product.dto.request.stock.StockUpdateDTO;
import dev.ilkerk.leasing.application.product.dto.response.StockResponse;
import dev.ilkerk.leasing.application.product.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("stock")
@Slf4j
@RequiredArgsConstructor
public class StockController {
	private final StockService stockService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<StockResponse> getById(@PathVariable UUID id) {
		try {
			return stockService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping("product/{productId}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<StockResponse> getByProductId(@PathVariable UUID productId) {
		try {
			return stockService.getByProductId(productId);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<StockResponse> getAll(@ModelAttribute @Valid StockFindDTO stockFindDTO) {
		try {
			return stockService.getAllByCriteria(stockFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	/*@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	templates Mono<StockResponse> create(@RequestBody @Valid StockUpdateDTO stockUpdateDTO) {
		try {
			return stockService.create(stockUpdateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}*/

	@PutMapping("{productId}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<StockResponse> update(@PathVariable UUID productId, @RequestBody @Valid StockUpdateDTO stockUpdateDTO) {
		try {
			return stockService.update(productId, stockUpdateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	/*@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	templates Mono<Void> delete(@PathVariable UUID id) {
		return stockService.deleteById(id);
	}*/
}
