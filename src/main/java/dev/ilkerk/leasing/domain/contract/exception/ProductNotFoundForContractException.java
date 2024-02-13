package dev.ilkerk.leasing.domain.contract.exception;

public class ProductNotFoundForContractException extends RuntimeException {
    public ProductNotFoundForContractException(String message) {
        super(message);
    }
}
