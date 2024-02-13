package dev.ilkerk.leasing.domain.product.repository;

import dev.ilkerk.leasing.domain.product.entity.StockHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface StockHistoryRepository extends R2dbcRepository<StockHistory, UUID> {

    Flux<StockHistory> findAllByStockIdOrderByCreatedAtDesc(UUID stockId);
}
