package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.ContractNotes;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ContractNotesRepository extends R2dbcRepository<ContractNotes, UUID> {
    Flux<ContractNotes> findAllByContractId(UUID contractId);
}
