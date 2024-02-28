package dev.ilkerk.leasing.application.contract.dto.response.html;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceHtml {
    private String date;
    private String logo;
    private String dealerTitle;
    private String serialNumber;
    private String startTime;
    private String endTime;
    private String dealerAddress;
    private String dealerPhoneNumber;
    private String dealerEmail;
    private String customerTitle;
    private String customerAddress;
    private String customerPhoneNumber;
    private String customerEmail;
    private List<ConditionsHtml> conditions;
    private List<TransactionsHtml> transactions;
    private List<ProductsHtml> products;
    private String discount;
    private String totalAmount;
}
