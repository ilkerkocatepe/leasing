package dev.ilkerk.leasing.application.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String details;
    private String district;
    private String city;
    private String country;
    private String zipcode;
    private String description;
    private UUID customerId;
    private Boolean isMain;
}
