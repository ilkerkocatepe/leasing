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

@Table("transports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transport {
    @Id
    private UUID id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String description;
    private TransportType transportType;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private UUID senderCustomerId;
    private UUID receiverCustomerId;
    private UUID contractId;
}
