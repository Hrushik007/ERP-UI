package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity for Order Processing and Sales modules.
 *
 * Used by:
 * - Order Processing Module
 * - Sales Module
 * - Inventory Module (stock updates)
 * - Finance Module (invoicing)
 */
public class Order {

    private int orderId;
    private String orderNumber; // Human-readable order number (e.g., ORD-2024-001)
    private int customerId;
    private Customer customer; // Optional: loaded when needed
    private LocalDateTime orderDate;
    private LocalDateTime requiredDate;
    private LocalDateTime shippedDate;
    private String status; // PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private String shippingMethod;
    private String shippingAddress;
    private BigDecimal shippingCost;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentStatus; // PENDING, PARTIAL, PAID, REFUNDED
    private String paymentMethod;
    private int salesRepId; // References Employee
    private String notes;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.items = new ArrayList<>();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.orderDate = LocalDateTime.now();
    }

    public Order(int orderId, String orderNumber, int customerId) {
        this();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
    }

    // Calculate totals based on items
    public void calculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        for (OrderItem item : items) {
            this.subtotal = this.subtotal.add(item.getLineTotal());
        }
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (shippingCost == null) shippingCost = BigDecimal.ZERO;

        this.totalAmount = subtotal.subtract(discountAmount).add(taxAmount).add(shippingCost);
    }

    // Add item to order
    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotals();
    }

    // Remove item from order
    public void removeItem(OrderItem item) {
        items.remove(item);
        calculateTotals();
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDateTime requiredDate) { this.requiredDate = requiredDate; }

    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }

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

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getSalesRepId() { return salesRepId; }
    public void setSalesRepId(int salesRepId) { this.salesRepId = salesRepId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getItemCount() { return items.size(); }

    @Override
    public String toString() {
        return "Order{" + orderNumber + ", status=" + status + ", total=" + totalAmount + "}";
    }
}
