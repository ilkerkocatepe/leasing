package dev.ilkerk.leasing.application.contract.dto.response.html;

import dev.ilkerk.leasing.application.contract.dto.response.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsHtml {
    private String productName;
    private String type;
    private String issueDate;
    private String amount;

    public static List<TransactionsHtml> from(List<TransactionResponse> transactionResponse) {
        if (transactionResponse == null || transactionResponse.isEmpty()) return List.of();

        return transactionResponse.stream()
                .map(transaction -> TransactionsHtml.builder()
                        .productName(transaction.getProduct().getName())
                        .type(String.valueOf(transaction.getType().getValue()))
                        .issueDate(transaction.getIssueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .amount(transaction.getAmount().toString())
                        .build())
                .toList();
    }
}
