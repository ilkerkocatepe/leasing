package dev.ilkerk.leasing.domain.contract.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("contract_notes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractNotes {
    private UUID contractId;
    private UUID noteId;
}
