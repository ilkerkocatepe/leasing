package dev.ilkerk.leasing.domain.product.repository;

import dev.ilkerk.leasing.domain.product.entity.ProductHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ProductHistoryRepository extends R2dbcRepository<ProductHistory, UUID> {
}
