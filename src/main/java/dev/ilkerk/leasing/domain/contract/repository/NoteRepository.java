package dev.ilkerk.leasing.domain.contract.repository;

import dev.ilkerk.leasing.domain.contract.entity.Note;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface NoteRepository extends R2dbcRepository<Note, UUID> {
}
