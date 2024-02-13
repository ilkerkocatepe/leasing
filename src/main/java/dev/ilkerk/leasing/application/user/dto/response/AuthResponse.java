package dev.ilkerk.leasing.application.user.dto.response;

import dev.ilkerk.leasing.application.customer.dto.response.CustomerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    UserResponse user;
    CustomerResponse customer;
}
