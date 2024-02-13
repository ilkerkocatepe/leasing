package dev.ilkerk.leasing.application.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreferenceResponse {
    private String name;
    private String value;
    private String description;
}
