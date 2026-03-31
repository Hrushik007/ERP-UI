package com.erp.model;

import java.time.LocalDateTime;

/**
 * StockLevel entity representing current inventory for a product at a warehouse.
 *
 * Used by:
 * - Inventory Module
 * - Order Processing (availability check)
 * - Purchasing (reorder alerts)
 */
public class StockLevel {

    private int stockLevelId;
    private int productId;
    private String productName;        // Cached for display
    private String productSku;
    private int warehouseId;
    private String warehouseName;      // Cached for display
    private int quantity;              // Current stock quantity
    private int reservedQuantity;      // Reserved for orders
    private int reorderLevel;          // Minimum quantity before reorder
    private int reorderQuantity;       // Standard reorder amount
    private int maxQuantity;           // Maximum stock level
    private String binLocation;        // Physical location in warehouse
    private LocalDateTime lastCountDate;
    private LocalDateTime updatedAt;

    public StockLevel() {
        this.quantity = 0;
        this.reservedQuantity = 0;
        this.reorderLevel = 10;
        this.reorderQuantity = 50;
        this.maxQuantity = 1000;
    }

    public StockLevel(int productId, int warehouseId) {
        this();
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    // Getters and Setters
    public int getStockLevelId() { return stockLevelId; }
    public void setStockLevelId(int stockLevelId) { this.stockLevelId = stockLevelId; }

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

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public int getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(int reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public int getMaxQuantity() { return maxQuantity; }
    public void setMaxQuantity(int maxQuantity) { this.maxQuantity = maxQuantity; }

    public String getBinLocation() { return binLocation; }
    public void setBinLocation(String binLocation) { this.binLocation = binLocation; }

    public LocalDateTime getLastCountDate() { return lastCountDate; }
    public void setLastCountDate(LocalDateTime lastCountDate) { this.lastCountDate = lastCountDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Calculated fields
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean needsReorder() {
        return getAvailableQuantity() <= reorderLevel;
    }

    public boolean isOverstock() {
        return quantity > maxQuantity;
    }

    public String getStockStatus() {
        if (quantity == 0) return "OUT_OF_STOCK";
        if (needsReorder()) return "LOW_STOCK";
        if (isOverstock()) return "OVERSTOCK";
        return "IN_STOCK";
    }

    @Override
    public String toString() {
        return productSku + " @ " + warehouseName + ": " + quantity + " units";
    }
}
