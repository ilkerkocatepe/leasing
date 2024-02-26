package dev.ilkerk.leasing.application.contract.dto.response.html;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionsHtml {
    private String name;
    private String value;

    public static List<ConditionsHtml> from(Map<String, String> conditions) {
        return conditions.entrySet().stream()
                .map(condition -> ConditionsHtml.builder()
                        .name(condition.getKey())
                        .value(condition.getValue())
                        .build())
                .toList();

    }
}
