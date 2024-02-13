package dev.ilkerk.leasing.domain.user.entity;

public enum Role {
    ADMIN,
    DEALER,
    CUSTOMER;

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    public String toString() {
        return this.name().toUpperCase();
    }
}
