package com.erp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Product entity for Inventory, Sales, and Manufacturing modules.
 *
 * Used by:
 * - Inventory/Supply Chain Module
 * - Sales Module
 * - Order Processing Module
 * - Manufacturing Module
 */
public class Product {

    private int productId;
    private String sku; // Stock Keeping Unit
    private String name;
    private String description;
    private String category;
    private String subcategory;
    private String brand;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private String unitOfMeasure; // PIECE, KG, LITER, BOX, etc.
    private int quantityInStock;
    private int reorderLevel; // Minimum stock before reorder
    private int reorderQuantity; // How many to order
    private int leadTimeDays; // Days to receive after ordering
    private String warehouseLocation;
    private String status; // ACTIVE, DISCONTINUED, OUT_OF_STOCK
    private int supplierId;
    private String supplierProductCode;
    private BigDecimal weight;
    private String dimensions; // LxWxH
    private boolean taxable;
    private BigDecimal taxRate;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public Product() {
        this.status = "ACTIVE";
        this.active = true;
        this.taxable = true;
        this.reorderLevel = 10;
        this.reorderQuantity = 50;
    }

    public Product(int productId, String sku, String name, BigDecimal unitPrice) {
        this();
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    // Business logic methods
    public boolean needsReorder() {
        return quantityInStock <= reorderLevel;
    }

    public BigDecimal getStockValue() {
        if (costPrice == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(quantityInStock));
    }

    public BigDecimal getMargin() {
        if (unitPrice == null || costPrice == null) return BigDecimal.ZERO;
        return unitPrice.subtract(costPrice);
    }

    public BigDecimal getMarginPercent() {
        if (unitPrice == null || costPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getMargin().divide(unitPrice, 4, RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100));
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public int getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(int reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierProductCode() { return supplierProductCode; }
    public void setSupplierProductCode(String supplierProductCode) { this.supplierProductCode = supplierProductCode; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean taxable) { this.taxable = taxable; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Product{" + sku + ": " + name + ", price=" + unitPrice + "}";
    }
}
