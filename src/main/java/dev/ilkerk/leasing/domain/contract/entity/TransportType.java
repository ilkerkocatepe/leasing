package dev.ilkerk.leasing.domain.contract.entity;

import lombok.Getter;

@Getter
public enum TransportType {
    INBOUND("GİRİŞ"), OUTBOUND("ÇIKIŞ");

    private final String value;

    TransportType(String value) {
        this.value = value;
    }

}
