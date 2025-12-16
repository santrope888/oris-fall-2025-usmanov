package com.shop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private String category;
    private BigDecimal price;
    private int quantity;
    private LocalDate expirationDate;
    private String manufacturer;

    public Product(int id, String name, String category, BigDecimal price,
                   int quantity, LocalDate expirationDate, String manufacturer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.manufacturer = manufacturer;
    }

    public boolean isValid() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (quantity < 0) {
            return false;
        }
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            return false;
        }
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | %.2f руб. | %d шт. | %s | %s",
                id, name, category, price, quantity, expirationDate, manufacturer);
    }
}