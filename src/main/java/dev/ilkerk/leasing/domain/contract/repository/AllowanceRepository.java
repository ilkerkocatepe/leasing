package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Allowance;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AllowanceRepository extends R2dbcRepository<Allowance, UUID> {
    Mono<Allowance> findFirstByContractIdOrderByEndTimeDesc(UUID contractId);
    Mono<Long> countAllByContractId(UUID contractId);
}
