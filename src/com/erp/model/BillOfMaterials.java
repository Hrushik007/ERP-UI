package com.erp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BillOfMaterials entity for Manufacturing module.
 * Represents the components needed to manufacture a product.
 */
public class BillOfMaterials {

    private int bomId;
    private int productId; // The finished product
    private Product product;
    private String bomCode;
    private String version;
    private String status; // DRAFT, ACTIVE, OBSOLETE
    private String description;
    private int outputQuantity;
    private String unitOfMeasure;
    private List<BOMItem> components;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillOfMaterials() {
        this.status = "DRAFT";
        this.outputQuantity = 1;
        this.components = new ArrayList<>();
    }

    public void addComponent(BOMItem item) {
        components.add(item);
    }

    // Getters and Setters
    public int getBomId() { return bomId; }
    public void setBomId(int bomId) { this.bomId = bomId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getBomCode() { return bomCode; }
    public void setBomCode(String bomCode) { this.bomCode = bomCode; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getOutputQuantity() { return outputQuantity; }
    public void setOutputQuantity(int outputQuantity) { this.outputQuantity = outputQuantity; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public List<BOMItem> getComponents() { return components; }
    public void setComponents(List<BOMItem> components) { this.components = components; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "BillOfMaterials{" + bomCode + " v" + version + " for product " + productId + "}";
    }
}
