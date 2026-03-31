package com.erp.service.interfaces;

import com.erp.model.StockLevel;
import com.erp.model.StockMovement;
import com.erp.model.Warehouse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * InventoryService Interface - CONTRACT for Inventory Module Backend Team
 *
 * Covers: Warehouse Management, Stock Levels, Stock Movements, Reorder Alerts
 *
 * IMPORTANT FOR BACKEND TEAM:
 * - Implement all methods in a class called InventoryServiceImpl
 * - Return empty lists instead of null when no data found
 */
public interface InventoryService {

    // ==================== WAREHOUSE MANAGEMENT ====================

    /**
     * Get all warehouses.
     * @return List of all warehouses
     */
    List<Warehouse> getAllWarehouses();

    /**
     * Get active warehouses only.
     * @return List of active warehouses
     */
    List<Warehouse> getActiveWarehouses();

    /**
     * Get warehouse by ID.
     * @param warehouseId The warehouse ID
     * @return Warehouse or null
     */
    Warehouse getWarehouseById(int warehouseId);

    /**
     * Get warehouse by code.
     * @param code The warehouse code
     * @return Warehouse or null
     */
    Warehouse getWarehouseByCode(String code);

    /**
     * Create a new warehouse.
     * @param warehouse The warehouse data
     * @return Created warehouse with ID
     */
    Warehouse createWarehouse(Warehouse warehouse);

    /**
     * Update a warehouse.
     * @param warehouse The warehouse to update
     * @return true if successful
     */
    boolean updateWarehouse(Warehouse warehouse);

    /**
     * Deactivate a warehouse.
     * @param warehouseId The warehouse ID
     * @return true if successful
     */
    boolean deactivateWarehouse(int warehouseId);


    // ==================== STOCK LEVELS ====================

    /**
     * Get all stock levels.
     * @return List of all stock levels
     */
    List<StockLevel> getAllStockLevels();

    /**
     * Get stock levels by warehouse.
     * @param warehouseId The warehouse ID
     * @return List of stock levels
     */
    List<StockLevel> getStockLevelsByWarehouse(int warehouseId);

    /**
     * Get stock levels for a product across all warehouses.
     * @param productId The product ID
     * @return List of stock levels
     */
    List<StockLevel> getStockLevelsByProduct(int productId);

    /**
     * Get stock level for a specific product at a specific warehouse.
     * @param productId The product ID
     * @param warehouseId The warehouse ID
     * @return StockLevel or null
     */
    StockLevel getStockLevel(int productId, int warehouseId);

    /**
     * Get total stock for a product across all warehouses.
     * @param productId The product ID
     * @return Total quantity
     */
    int getTotalStock(int productId);

    /**
     * Get available stock (total - reserved) for a product.
     * @param productId The product ID
     * @return Available quantity
     */
    int getAvailableStock(int productId);

    /**
     * Update stock level.
     * @param stockLevel The stock level to update
     * @return true if successful
     */
    boolean updateStockLevel(StockLevel stockLevel);


    // ==================== STOCK MOVEMENTS ====================

    /**
     * Get all stock movements.
     * @return List of all movements
     */
    List<StockMovement> getAllMovements();

    /**
     * Get movements by type.
     * @param movementType INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT
     * @return List of movements
     */
    List<StockMovement> getMovementsByType(String movementType);

    /**
     * Get movements by warehouse.
     * @param warehouseId The warehouse ID
     * @return List of movements
     */
    List<StockMovement> getMovementsByWarehouse(int warehouseId);

    /**
     * Get movements for a product.
     * @param productId The product ID
     * @return List of movements
     */
    List<StockMovement> getMovementsByProduct(int productId);

    /**
     * Get movements within a date range.
     * @param startDate Start date
     * @param endDate End date
     * @return List of movements
     */
    List<StockMovement> getMovementsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get movement by ID.
     * @param movementId The movement ID
     * @return StockMovement or null
     */
    StockMovement getMovementById(int movementId);

    /**
     * Record an inbound stock movement (receiving).
     * @param productId The product ID
     * @param warehouseId The warehouse ID
     * @param quantity The quantity received
     * @param reason The reason (PURCHASE, RETURN, etc.)
     * @param referenceNumber Related document number
     * @param notes Additional notes
     * @return Created movement
     */
    StockMovement recordInbound(int productId, int warehouseId, int quantity,
                                String reason, String referenceNumber, String notes);

    /**
     * Record an outbound stock movement (shipping).
     * @param productId The product ID
     * @param warehouseId The warehouse ID
     * @param quantity The quantity shipped
     * @param reason The reason (SALE, DAMAGE, etc.)
     * @param referenceNumber Related document number
     * @param notes Additional notes
     * @return Created movement or null if insufficient stock
     */
    StockMovement recordOutbound(int productId, int warehouseId, int quantity,
                                 String reason, String referenceNumber, String notes);

    /**
     * Record a stock adjustment.
     * @param productId The product ID
     * @param warehouseId The warehouse ID
     * @param newQuantity The corrected quantity
     * @param reason Reason for adjustment
     * @param notes Additional notes
     * @return Created movement
     */
    StockMovement recordAdjustment(int productId, int warehouseId, int newQuantity,
                                   String reason, String notes);


    // ==================== REORDER ALERTS ====================

    /**
     * Get products that need reordering (below reorder level).
     * @return List of stock levels needing reorder
     */
    List<StockLevel> getReorderAlerts();

    /**
     * Get out of stock products.
     * @return List of stock levels with zero quantity
     */
    List<StockLevel> getOutOfStockProducts();

    /**
     * Get overstock products.
     * @return List of stock levels above max quantity
     */
    List<StockLevel> getOverstockProducts();


    // ==================== ANALYTICS ====================

    /**
     * Get stock count by status.
     * @return Map of status to count
     */
    Map<String, Integer> getStockCountByStatus();

    /**
     * Get total inventory value.
     * @return Total value of all stock
     */
    java.math.BigDecimal getTotalInventoryValue();

    /**
     * Get warehouse utilization.
     * @return Map of warehouse ID to utilization percentage
     */
    Map<Integer, Double> getWarehouseUtilization();
}
