package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkOrder entity for Manufacturing module.
 * Represents a production order to manufacture products.
 */
public class WorkOrder {

    private int workOrderId;
    private String workOrderNumber;
    private int productId;
    private Product product;
    private int bomId;
    private BillOfMaterials bom;
    private int quantityOrdered;
    private int quantityCompleted;
    private int quantityScrapped;
    private String status; // PLANNED, RELEASED, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private int workCenterId;
    private int supervisorId; // Employee
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkOrder() {
        this.status = "PLANNED";
        this.priority = "MEDIUM";
        this.quantityCompleted = 0;
        this.quantityScrapped = 0;
    }

    public int getQuantityPending() {
        return quantityOrdered - quantityCompleted - quantityScrapped;
    }

    public double getCompletionPercentage() {
        if (quantityOrdered == 0) return 0;
        return (double) quantityCompleted / quantityOrdered * 100;
    }

    public boolean isOverdue() {
        if (plannedEndDate == null) return false;
        return LocalDate.now().isAfter(plannedEndDate) && !"COMPLETED".equals(status);
    }

    // Getters and Setters
    public int getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(int workOrderId) { this.workOrderId = workOrderId; }

    public String getWorkOrderNumber() { return workOrderNumber; }
    public void setWorkOrderNumber(String workOrderNumber) { this.workOrderNumber = workOrderNumber; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getBomId() { return bomId; }
    public void setBomId(int bomId) { this.bomId = bomId; }

    public BillOfMaterials getBom() { return bom; }
    public void setBom(BillOfMaterials bom) { this.bom = bom; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public int getQuantityCompleted() { return quantityCompleted; }
    public void setQuantityCompleted(int quantityCompleted) { this.quantityCompleted = quantityCompleted; }

    public int getQuantityScrapped() { return quantityScrapped; }
    public void setQuantityScrapped(int quantityScrapped) { this.quantityScrapped = quantityScrapped; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(LocalDate plannedStartDate) { this.plannedStartDate = plannedStartDate; }

    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(LocalDate plannedEndDate) { this.plannedEndDate = plannedEndDate; }

    public LocalDate getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }

    public LocalDate getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }

    public int getWorkCenterId() { return workCenterId; }
    public void setWorkCenterId(int workCenterId) { this.workCenterId = workCenterId; }

    public int getSupervisorId() { return supervisorId; }
    public void setSupervisorId(int supervisorId) { this.supervisorId = supervisorId; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "WorkOrder{" + workOrderNumber + ", product=" + productId + ", status=" + status + "}";
    }
}
