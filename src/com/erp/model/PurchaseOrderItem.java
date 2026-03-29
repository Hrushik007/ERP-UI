package com.erp.model;

import java.math.BigDecimal;

/**
 * PurchaseOrderItem entity - line item in a purchase order.
 */
public class PurchaseOrderItem {

    private int poItemId;
    private int purchaseOrderId;
    private int productId;
    private Product product;
    private String productName;
    private String productSku;
    private int quantityOrdered;
    private int quantityReceived;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String notes;

    public PurchaseOrderItem() {
        this.quantityOrdered = 1;
        this.quantityReceived = 0;
    }

    public PurchaseOrderItem(int productId, String productName, int quantity, BigDecimal unitPrice) {
        this();
        this.productId = productId;
        this.productName = productName;
        this.quantityOrdered = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public void calculateLineTotal() {
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantityOrdered));
    }

    public boolean isFullyReceived() {
        return quantityReceived >= quantityOrdered;
    }

    public int getQuantityPending() {
        return quantityOrdered - quantityReceived;
    }

    // Getters and Setters
    public int getPoItemId() { return poItemId; }
    public void setPoItemId(int poItemId) { this.poItemId = poItemId; }

    public int getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(int purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
        calculateLineTotal();
    }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "PurchaseOrderItem{" + productName + " x" + quantityOrdered + "}";
    }
}
