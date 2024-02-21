package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Discount;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface DiscountRepository extends R2dbcRepository<Discount, UUID> {
}
