package dev.ilkerk.leasing.application.contract.dto.response.html;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsHtml {
    private String name;
    private String value;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static List<ProductsHtml> from(Map<String, Double> products) {
        if (products == null || products.isEmpty()) return List.of();

        List<ProductsHtml> productsHtml = new java.util.ArrayList<>(List.of());
        products.forEach((key, value) -> {
            productsHtml.add(ProductsHtml.builder()
                    .name(key)
                    .value(df.format(value))
                    .build());
        });

        return productsHtml;
    }
}
