package com.erp.service.interfaces;

import com.erp.model.Order;
import com.erp.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * SalesService Interface - CONTRACT for Sales Module Backend Team
 *
 * Covers: Lead Management, Quotations, Order Management, Pricing, Forecasting
 *
 * IMPORTANT FOR BACKEND TEAM:
 * - Implement all methods in a class called SalesServiceImpl
 * - Return empty lists instead of null when no data found
 */
public interface SalesService {

    // ==================== ORDER MANAGEMENT ====================

    /**
     * Get all orders.
     * @return List of all orders
     */
    List<Order> getAllOrders();

    /**
     * Get orders by status.
     * @param status PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
     * @return List of orders with that status
     */
    List<Order> getOrdersByStatus(String status);

    /**
     * Get orders for a customer.
     * @param customerId The customer ID
     * @return List of customer's orders
     */
    List<Order> getOrdersByCustomer(int customerId);

    /**
     * Get orders by sales rep.
     * @param salesRepId The sales rep's employee ID
     * @return List of orders
     */
    List<Order> getOrdersBySalesRep(int salesRepId);

    /**
     * Get orders within a date range.
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of orders in range
     */
    List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get an order by ID.
     * @param orderId The order ID
     * @return Order object or null
     */
    Order getOrderById(int orderId);

    /**
     * Get an order by order number.
     * @param orderNumber The human-readable order number
     * @return Order object or null
     */
    Order getOrderByNumber(String orderNumber);

    /**
     * Create a new order.
     * @param order The order data
     * @return Created order with ID and order number
     */
    Order createOrder(Order order);

    /**
     * Update an order.
     * @param order The order to update
     * @return true if successful
     */
    boolean updateOrder(Order order);

    /**
     * Cancel an order.
     * @param orderId The order ID
     * @param reason Cancellation reason
     * @return true if successful
     */
    boolean cancelOrder(int orderId, String reason);

    /**
     * Update order status.
     * @param orderId The order ID
     * @param newStatus The new status
     * @return true if successful
     */
    boolean updateOrderStatus(int orderId, String newStatus);


    // ==================== ORDER ITEMS ====================

    /**
     * Add an item to an order.
     * @param orderId The order ID
     * @param item The item to add
     * @return Created order item with ID
     */
    OrderItem addOrderItem(int orderId, OrderItem item);

    /**
     * Update an order item.
     * @param item The item to update
     * @return true if successful
     */
    boolean updateOrderItem(OrderItem item);

    /**
     * Remove an item from an order.
     * @param orderItemId The order item ID
     * @return true if successful
     */
    boolean removeOrderItem(int orderItemId);

    /**
     * Get all items for an order.
     * @param orderId The order ID
     * @return List of order items
     */
    List<OrderItem> getOrderItems(int orderId);


    // ==================== PRICING ====================

    /**
     * Get the price for a product (may vary by quantity, customer, etc.).
     * @param productId The product ID
     * @param customerId The customer ID (for customer-specific pricing)
     * @param quantity The quantity (for volume discounts)
     * @return The unit price
     */
    BigDecimal getProductPrice(int productId, int customerId, int quantity);

    /**
     * Apply a discount to an order.
     * @param orderId The order ID
     * @param discountPercent The discount percentage
     * @param reason Reason for discount
     * @return true if successful
     */
    boolean applyDiscount(int orderId, BigDecimal discountPercent, String reason);


    // ==================== SALES ANALYTICS ====================

    /**
     * Get total sales for a date range.
     * @param startDate Start date
     * @param endDate End date
     * @return Total sales amount
     */
    BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate);

    /**
     * Get sales by status.
     * @return Map of status to total amount
     */
    Map<String, BigDecimal> getSalesByStatus();

    /**
     * Get sales by sales rep.
     * @param startDate Start date
     * @param endDate End date
     * @return Map of sales rep ID to total amount
     */
    Map<Integer, BigDecimal> getSalesBySalesRep(LocalDate startDate, LocalDate endDate);

    /**
     * Get top selling products.
     * @param limit Number of products to return
     * @param startDate Start date
     * @param endDate End date
     * @return Map of product ID to quantity sold
     */
    Map<Integer, Integer> getTopSellingProducts(int limit, LocalDate startDate, LocalDate endDate);

    /**
     * Get order count by status.
     * @return Map of status to count
     */
    Map<String, Integer> getOrderCountByStatus();
}
