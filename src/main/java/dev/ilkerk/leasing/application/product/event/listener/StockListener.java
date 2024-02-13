package dev.ilkerk.leasing.application.product.event.listener;

import dev.ilkerk.leasing.application.product.dto.request.stock.StockCreateDTO;
import dev.ilkerk.leasing.application.product.event.ProductCreatedEvent;
import dev.ilkerk.leasing.application.product.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockListener {
	private final StockService stockService;

	@EventListener
	public Mono<Void> handleProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {
		log.info("ProductCreatedEvent is handled: " + productCreatedEvent.toString());

		return stockService.create(StockCreateDTO.builder()
				.productId(productCreatedEvent.getProduct().getId())
				.customerId(productCreatedEvent.getProduct().getCustomerId())
				.amount(productCreatedEvent.getAmount())
				.build()).then();
	}
}
