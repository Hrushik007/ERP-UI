package com.erp.model;

import java.math.BigDecimal;

/**
 * BOMItem - A component in a Bill of Materials.
 */
public class BOMItem {

    private int bomItemId;
    private int bomId;
    private int componentProductId;
    private Product componentProduct;
    private String componentName;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private String notes;
    private int sequence;

    public BOMItem() {
        this.quantity = BigDecimal.ONE;
    }

    public BOMItem(int componentProductId, String componentName, BigDecimal quantity) {
        this();
        this.componentProductId = componentProductId;
        this.componentName = componentName;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getBomItemId() { return bomItemId; }
    public void setBomItemId(int bomItemId) { this.bomItemId = bomItemId; }

    public int getBomId() { return bomId; }
    public void setBomId(int bomId) { this.bomId = bomId; }

    public int getComponentProductId() { return componentProductId; }
    public void setComponentProductId(int componentProductId) { this.componentProductId = componentProductId; }

    public Product getComponentProduct() { return componentProduct; }
    public void setComponentProduct(Product componentProduct) { this.componentProduct = componentProduct; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    @Override
    public String toString() {
        return "BOMItem{" + componentName + " x" + quantity + "}";
    }
}
