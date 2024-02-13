package dev.ilkerk.leasing.domain.contract.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("contracts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    private UUID id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String contractNumber;
    private ContractStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Double specialAreaPrice;
    private UUID sellerCustomerId;
    private UUID takerCustomerId;
    private UUID userId;
    private UUID addressId;
}
