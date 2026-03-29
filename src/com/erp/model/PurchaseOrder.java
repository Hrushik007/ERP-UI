package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Purchase Order entity for Supply Chain/Purchasing module.
 *
 * Used by:
 * - Supply Chain/Purchasing Module
 * - Inventory Module (stock receiving)
 * - Accounting Module (payables)
 */
public class PurchaseOrder {

    private int purchaseOrderId;
    private String poNumber; // Human-readable PO number (e.g., PO-2024-001)
    private int vendorId;
    private Vendor vendor;
    private LocalDateTime orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String status; // DRAFT, SUBMITTED, APPROVED, RECEIVED, PARTIAL, CANCELLED
    private String shippingMethod;
    private String shippingAddress;
    private BigDecimal shippingCost;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private String paymentStatus; // PENDING, PARTIAL, PAID
    private int requestedById; // Employee who requested
    private int approvedById; // Employee who approved
    private LocalDateTime approvedDate;
    private List<PurchaseOrderItem> items;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseOrder() {
        this.items = new ArrayList<>();
        this.status = "DRAFT";
        this.paymentStatus = "PENDING";
        this.orderDate = LocalDateTime.now();
    }

    public void calculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        for (PurchaseOrderItem item : items) {
            this.subtotal = this.subtotal.add(item.getLineTotal());
        }
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (shippingCost == null) shippingCost = BigDecimal.ZERO;
        this.totalAmount = subtotal.add(taxAmount).add(shippingCost);
    }

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        calculateTotals();
    }

    // Getters and Setters
    public int getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(int purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDate getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public int getRequestedById() { return requestedById; }
    public void setRequestedById(int requestedById) { this.requestedById = requestedById; }

    public int getApprovedById() { return approvedById; }
    public void setApprovedById(int approvedById) { this.approvedById = approvedById; }

    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }

    public List<PurchaseOrderItem> getItems() { return items; }
    public void setItems(List<PurchaseOrderItem> items) { this.items = items; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PurchaseOrder{" + poNumber + ", status=" + status + ", total=" + totalAmount + "}";
    }
}
