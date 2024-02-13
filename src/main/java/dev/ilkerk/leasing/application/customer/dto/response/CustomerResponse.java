package dev.ilkerk.leasing.application.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String logo;
    private String logoUrl;
    private String taxNumber;
    private String taxAdministration;
    private String mersisNumber;
    private String phoneNumber;
    private List<AddressResponse> addressList = new ArrayList<>();
    private Map<String, String> customerPreferences = new HashMap<>();
}
