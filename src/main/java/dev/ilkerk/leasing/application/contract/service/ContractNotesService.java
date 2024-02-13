package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.domain.contract.entity.ContractNotes;
import dev.ilkerk.leasing.domain.contract.repository.ContractNotesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractNotesService {
    private final ContractNotesRepository contractNotesRepository;

    public Mono<ContractNotes> create(UUID contractId, UUID noteId) {
        ContractNotes contractNotes = ContractNotes.builder()
                .contractId(contractId)
                .noteId(noteId)
                .build();

        return contractNotesRepository.save(contractNotes);
    }

    public Flux<ContractNotes> getAllByContractId(UUID id) {
        return contractNotesRepository.findAllByContractId(id);
    }
}
