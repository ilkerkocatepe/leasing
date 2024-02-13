package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Contract;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface ContractRepository extends R2dbcRepository<Contract, UUID> {
}
