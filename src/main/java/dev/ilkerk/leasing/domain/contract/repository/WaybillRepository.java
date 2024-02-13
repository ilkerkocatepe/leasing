package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Waybill;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface WaybillRepository extends R2dbcRepository<Waybill, UUID> {
}
