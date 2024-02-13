package dev.ilkerk.leasing.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("customer_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUsers {
    private UUID customerId;
    private UUID userId;
}