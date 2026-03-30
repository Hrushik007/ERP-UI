package com.erp.model;

import java.math.BigDecimal;

/**
 * OrderItem entity representing a line item in an order.
 *
 * Used by:
 * - Order Processing Module
 * - Sales Module
 * - Inventory Module
 */
public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int productId;
    private Product product; // Optional: loaded when needed
    private String productName; // Cached for display
    private String productSku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    private String notes;

    public OrderItem() {
        this.quantity = 1;
        this.discountPercent = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
    }

    public OrderItem(int productId, String productName, int quantity, BigDecimal unitPrice) {
        this();
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    // Calculate line total
    public void calculateLineTotal() {
        if (unitPrice == null) {
            this.lineTotal = BigDecimal.ZERO;
            return;
        }
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.lineTotal = gross.subtract(discountAmount);
        } else if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = gross.multiply(discountPercent).divide(BigDecimal.valueOf(100));
            this.lineTotal = gross.subtract(discount);
        } else {
            this.lineTotal = gross;
        }
    }

    // Getters and Setters
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
        calculateLineTotal();
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "OrderItem{" + productName + " x" + quantity + " = " + lineTotal + "}";
    }
}
