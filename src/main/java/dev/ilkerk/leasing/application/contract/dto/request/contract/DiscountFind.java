package dev.ilkerk.leasing.application.contract.dto.request.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;

import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountFind {
    private UUID id;

    public static ExampleMatcher getExampleMatcher() {
        return ExampleMatcher.matching()
                .withMatcher("id", exact())
                .withMatcher("createdAt", exact())
                .withMatcher("updatedAt", exact())
                .withMatcher("modifiedBy", contains().stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase())
                .withMatcher("amount", exact())
                .withMatcher("isPaid", exact())
                .withMatcher("contractId", exact())
                .withMatcher("discountId", exact())
                .withMatcher("invoiceId", exact());
    }
}
