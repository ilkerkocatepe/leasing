package dev.ilkerk.leasing.domain.product.entity;

public enum FileDirectory {
    PRODUCT("product"), PRODUCT_CATEGORY("product-category"), CUSTOMER("customer");

    private final String name;

    FileDirectory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
