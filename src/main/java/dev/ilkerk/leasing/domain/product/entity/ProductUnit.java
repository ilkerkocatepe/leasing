package dev.ilkerk.leasing.domain.product.entity;

public enum ProductUnit {
    MM("millimeter"),
    CM("centimeter"),
    M("meter"),
    G("gram"),
    KG("kilogram"),
    PIECE("piece");

    public final String longName;

    ProductUnit(String longName) {
        this.longName = longName;
    }
}
