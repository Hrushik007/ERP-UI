package com.erp.service.interfaces;

import com.erp.model.BillOfMaterials;
import com.erp.model.BOMItem;
import com.erp.model.WorkOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ManufacturingService Interface - CONTRACT for Manufacturing Module Backend Team
 *
 * Covers: Production Planning, BOM Management, Shop Floor Control, Quality Management
 */
public interface ManufacturingService {

    // ==================== BILL OF MATERIALS ====================

    /**
     * Get all BOMs.
     * @return List of all BOMs
     */
    List<BillOfMaterials> getAllBOMs();

    /**
     * Get active BOMs.
     * @return List of active BOMs
     */
    List<BillOfMaterials> getActiveBOMs();

    /**
     * Get BOM by ID.
     * @param bomId The BOM ID
     * @return BOM or null
     */
    BillOfMaterials getBOMById(int bomId);

    /**
     * Get BOM for a product.
     * @param productId The product ID
     * @return Active BOM for the product, or null
     */
    BillOfMaterials getBOMByProduct(int productId);

    /**
     * Create a new BOM.
     * @param bom The BOM data
     * @return Created BOM with ID
     */
    BillOfMaterials createBOM(BillOfMaterials bom);

    /**
     * Update a BOM.
     * @param bom The BOM to update
     * @return true if successful
     */
    boolean updateBOM(BillOfMaterials bom);

    /**
     * Activate a BOM (and deactivate previous version).
     * @param bomId The BOM ID
     * @return true if successful
     */
    boolean activateBOM(int bomId);

    /**
     * Add component to BOM.
     * @param bomId The BOM ID
     * @param item The component to add
     * @return Created BOM item
     */
    BOMItem addBOMComponent(int bomId, BOMItem item);

    /**
     * Remove component from BOM.
     * @param bomItemId The BOM item ID
     * @return true if successful
     */
    boolean removeBOMComponent(int bomItemId);


    // ==================== WORK ORDERS ====================

    /**
     * Get all work orders.
     * @return List of all work orders
     */
    List<WorkOrder> getAllWorkOrders();

    /**
     * Get work orders by status.
     * @param status PLANNED, RELEASED, IN_PROGRESS, COMPLETED, CANCELLED
     * @return List of work orders
     */
    List<WorkOrder> getWorkOrdersByStatus(String status);

    /**
     * Get work orders for a product.
     * @param productId The product ID
     * @return List of work orders
     */
    List<WorkOrder> getWorkOrdersByProduct(int productId);

    /**
     * Get overdue work orders.
     * @return List of overdue work orders
     */
    List<WorkOrder> getOverdueWorkOrders();

    /**
     * Get a work order by ID.
     * @param workOrderId The work order ID
     * @return WorkOrder or null
     */
    WorkOrder getWorkOrderById(int workOrderId);

    /**
     * Get work order by number.
     * @param workOrderNumber The work order number
     * @return WorkOrder or null
     */
    WorkOrder getWorkOrderByNumber(String workOrderNumber);

    /**
     * Create a work order.
     * @param workOrder The work order data
     * @return Created work order with ID
     */
    WorkOrder createWorkOrder(WorkOrder workOrder);

    /**
     * Update a work order.
     * @param workOrder The work order to update
     * @return true if successful
     */
    boolean updateWorkOrder(WorkOrder workOrder);

    /**
     * Release a work order (make available for production).
     * @param workOrderId The work order ID
     * @return true if released
     */
    boolean releaseWorkOrder(int workOrderId);

    /**
     * Start a work order.
     * @param workOrderId The work order ID
     * @return true if started
     */
    boolean startWorkOrder(int workOrderId);

    /**
     * Record production completion.
     * @param workOrderId The work order ID
     * @param quantityCompleted Quantity completed
     * @param quantityScrapped Quantity scrapped
     * @return true if recorded
     */
    boolean recordProduction(int workOrderId, int quantityCompleted, int quantityScrapped);

    /**
     * Complete a work order.
     * @param workOrderId The work order ID
     * @return true if completed
     */
    boolean completeWorkOrder(int workOrderId);

    /**
     * Cancel a work order.
     * @param workOrderId The work order ID
     * @param reason Cancellation reason
     * @return true if cancelled
     */
    boolean cancelWorkOrder(int workOrderId, String reason);


    // ==================== PRODUCTION PLANNING ====================

    /**
     * Get work orders scheduled for a date range.
     * @param startDate Start date
     * @param endDate End date
     * @return List of scheduled work orders
     */
    List<WorkOrder> getScheduledWorkOrders(LocalDate startDate, LocalDate endDate);

    /**
     * Check material availability for a work order.
     * @param workOrderId The work order ID
     * @return Map of component ID to available quantity
     */
    Map<Integer, Integer> checkMaterialAvailability(int workOrderId);

    /**
     * Get production schedule (by date).
     * @param date The date
     * @return List of work orders scheduled for that date
     */
    List<WorkOrder> getProductionSchedule(LocalDate date);


    // ==================== QUALITY MANAGEMENT ====================

    /**
     * Record quality check result.
     * @param workOrderId The work order ID
     * @param passed Number of units passed
     * @param failed Number of units failed
     * @param notes Quality notes
     * @return true if recorded
     */
    boolean recordQualityCheck(int workOrderId, int passed, int failed, String notes);

    /**
     * Get quality statistics for a product.
     * @param productId The product ID
     * @return Map with keys: totalProduced, passed, failed, passRate
     */
    Map<String, Object> getQualityStats(int productId);
}
