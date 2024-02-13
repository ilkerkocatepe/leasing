package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Transport;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface TransportRepository extends R2dbcRepository<Transport, UUID> {
}
