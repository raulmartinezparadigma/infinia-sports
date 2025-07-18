package com.infinia.sports.model;

/**
 * Enumeración que define los tipos de productos disponibles
 */
public enum ProductType {

    SNEAKERS("Zapatillas"),
    CLOTHING("Ropa"),
    SUPPLEMENT("Suplemento");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
