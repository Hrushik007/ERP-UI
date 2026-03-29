package com.erp.service.interfaces;

import com.erp.model.Order;
import com.erp.model.OrderItem;

import java.time.LocalDate;
import java.util.List;

/**
 * OrderProcessingService Interface - CONTRACT for Order Processing Backend Team
 *
 * Covers: Order Capture, Validation, Fulfillment, Billing, Returns
 *
 * This service focuses on the processing/fulfillment side of orders,
 * while SalesService handles the sales/commercial side.
 */
public interface OrderProcessingService {

    // ==================== ORDER FULFILLMENT ====================

    /**
     * Get orders pending fulfillment.
     * @return List of orders ready to be processed
     */
    List<Order> getPendingFulfillment();

    /**
     * Get orders currently being processed.
     * @return List of orders in processing
     */
    List<Order> getOrdersInProcessing();

    /**
     * Start processing an order (move to PROCESSING status).
     * @param orderId The order ID
     * @return true if successful
     */
    boolean startProcessing(int orderId);

    /**
     * Mark order as shipped.
     * @param orderId The order ID
     * @param trackingNumber Shipping tracking number
     * @param shippingMethod The shipping method used
     * @return true if successful
     */
    boolean shipOrder(int orderId, String trackingNumber, String shippingMethod);

    /**
     * Mark order as delivered.
     * @param orderId The order ID
     * @return true if successful
     */
    boolean markDelivered(int orderId);

    /**
     * Check if all items are available for an order.
     * @param orderId The order ID
     * @return true if all items can be fulfilled
     */
    boolean checkAvailability(int orderId);

    /**
     * Get items that are back-ordered (insufficient stock).
     * @param orderId The order ID
     * @return List of items that can't be fulfilled
     */
    List<OrderItem> getBackOrderedItems(int orderId);


    // ==================== ORDER VALIDATION ====================

    /**
     * Validate an order (check customer credit, availability, etc.).
     * @param orderId The order ID
     * @return List of validation errors (empty if valid)
     */
    List<String> validateOrder(int orderId);

    /**
     * Confirm an order (move from PENDING to CONFIRMED).
     * @param orderId The order ID
     * @return true if confirmed successfully
     */
    boolean confirmOrder(int orderId);


    // ==================== RETURNS ====================

    /**
     * Initiate a return for an order item.
     * @param orderItemId The order item ID
     * @param quantity Quantity being returned
     * @param reason Return reason
     * @return Return authorization number
     */
    String initiateReturn(int orderItemId, int quantity, String reason);

    /**
     * Process a return (item received back).
     * @param returnAuthorizationNumber The RA number
     * @return true if processed successfully
     */
    boolean processReturn(String returnAuthorizationNumber);

    /**
     * Get all returns for an order.
     * @param orderId The order ID
     * @return List of return records (as OrderItems with return info)
     */
    List<OrderItem> getReturnsForOrder(int orderId);


    // ==================== SCHEDULING ====================

    /**
     * Get orders scheduled for delivery on a date.
     * @param deliveryDate The delivery date
     * @return List of orders
     */
    List<Order> getOrdersForDeliveryDate(LocalDate deliveryDate);

    /**
     * Schedule delivery for an order.
     * @param orderId The order ID
     * @param deliveryDate The scheduled delivery date
     * @return true if scheduled successfully
     */
    boolean scheduleDelivery(int orderId, LocalDate deliveryDate);
}
