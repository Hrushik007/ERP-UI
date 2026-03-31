package com.erp.model;

import java.time.LocalDateTime;

/**
 * StockMovement entity representing inventory transactions.
 *
 * Used by:
 * - Inventory Module
 * - Order Processing (shipments)
 * - Purchasing (receiving)
 * - Manufacturing (consumption)
 */
public class StockMovement {

    private int movementId;
    private String movementNumber;     // e.g., "MOV-2024-001"
    private int productId;
    private String productName;        // Cached for display
    private String productSku;
    private int warehouseId;
    private String warehouseName;      // Cached for display
    private String movementType;       // INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT
    private String reason;             // PURCHASE, SALE, RETURN, DAMAGE, CORRECTION, PRODUCTION
    private int quantity;
    private int quantityBefore;        // Stock before movement
    private int quantityAfter;         // Stock after movement
    private String referenceType;      // ORDER, PURCHASE_ORDER, TRANSFER, MANUAL
    private String referenceNumber;    // Related document number
    private String notes;
    private int createdBy;             // Employee ID
    private String createdByName;
    private LocalDateTime createdAt;

    public StockMovement() {
        this.createdAt = LocalDateTime.now();
    }

    public StockMovement(int productId, int warehouseId, String movementType, int quantity) {
        this();
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }

    public String getMovementNumber() { return movementNumber; }
    public void setMovementNumber(String movementNumber) { this.movementNumber = movementNumber; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getQuantityBefore() { return quantityBefore; }
    public void setQuantityBefore(int quantityBefore) { this.quantityBefore = quantityBefore; }

    public int getQuantityAfter() { return quantityAfter; }
    public void setQuantityAfter(int quantityAfter) { this.quantityAfter = quantityAfter; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper to determine if movement adds or removes stock
    public boolean isInbound() {
        return "INBOUND".equals(movementType);
    }

    public boolean isOutbound() {
        return "OUTBOUND".equals(movementType);
    }

    @Override
    public String toString() {
        return movementNumber + " - " + movementType + " " + quantity + " units";
    }
}
