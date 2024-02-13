package dev.ilkerk.leasing.domain.product.repository;

import dev.ilkerk.leasing.domain.product.entity.Stock;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StockRepository extends R2dbcRepository<Stock, UUID> {
	Mono<Stock> findByProductId(UUID productId);
}
