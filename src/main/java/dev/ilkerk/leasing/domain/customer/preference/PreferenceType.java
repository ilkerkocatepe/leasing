package dev.ilkerk.leasing.domain.customer.preference;

public enum PreferenceType {
    PaymentCalculationType("payment_calculation_type"),
    SpecialAreaPrice("special_area_price");

    private final String keyName;

    PreferenceType(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
