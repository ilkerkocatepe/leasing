package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    Flux<Transaction> findAllByContractId(UUID contractId);
}
