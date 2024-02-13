package dev.ilkerk.leasing.domain.product.repository;

import dev.ilkerk.leasing.domain.product.entity.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ProductRepository extends R2dbcRepository<Product, UUID> {
}
