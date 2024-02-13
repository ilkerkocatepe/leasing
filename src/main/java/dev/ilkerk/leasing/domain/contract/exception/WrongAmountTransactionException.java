package dev.ilkerk.leasing.domain.contract.exception;

public class WrongAmountTransactionException extends RuntimeException {
    public WrongAmountTransactionException(String message) {
        super(message);
    }
}
