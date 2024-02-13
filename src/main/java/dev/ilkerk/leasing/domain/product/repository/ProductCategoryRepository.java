package dev.ilkerk.leasing.domain.product.repository;

import dev.ilkerk.leasing.domain.product.entity.ProductCategory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, UUID> {
}
