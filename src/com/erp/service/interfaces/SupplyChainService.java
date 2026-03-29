package com.erp.service.interfaces;

import com.erp.model.Product;
import com.erp.model.PurchaseOrder;
import com.erp.model.PurchaseOrderItem;
import com.erp.model.Vendor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * SupplyChainService Interface - CONTRACT for Supply Chain/Purchasing Backend Team
 *
 * Covers: Procurement, Vendor Management, Inventory Control, Logistics
 */
public interface SupplyChainService {

    // ==================== VENDOR MANAGEMENT ====================

    /**
     * Get all vendors.
     * @return List of all vendors
     */
    List<Vendor> getAllVendors();

    /**
     * Get vendors by status.
     * @param status ACTIVE, INACTIVE, BLOCKED
     * @return List of vendors
     */
    List<Vendor> getVendorsByStatus(String status);

    /**
     * Get a vendor by ID.
     * @param vendorId The vendor ID
     * @return Vendor object or null
     */
    Vendor getVendorById(int vendorId);

    /**
     * Search vendors by name.
     * @param searchTerm Search string
     * @return List of matching vendors
     */
    List<Vendor> searchVendors(String searchTerm);

    /**
     * Get vendors by category.
     * @param category RAW_MATERIAL, FINISHED_GOODS, SERVICES, etc.
     * @return List of vendors
     */
    List<Vendor> getVendorsByCategory(String category);

    /**
     * Create a new vendor.
     * @param vendor The vendor data
     * @return Created vendor with ID
     */
    Vendor createVendor(Vendor vendor);

    /**
     * Update a vendor.
     * @param vendor The vendor to update
     * @return true if successful
     */
    boolean updateVendor(Vendor vendor);

    /**
     * Deactivate a vendor.
     * @param vendorId The vendor ID
     * @return true if successful
     */
    boolean deactivateVendor(int vendorId);


    // ==================== PURCHASE ORDERS ====================

    /**
     * Get all purchase orders.
     * @return List of all POs
     */
    List<PurchaseOrder> getAllPurchaseOrders();

    /**
     * Get POs by status.
     * @param status DRAFT, SUBMITTED, APPROVED, RECEIVED, PARTIAL, CANCELLED
     * @return List of POs
     */
    List<PurchaseOrder> getPurchaseOrdersByStatus(String status);

    /**
     * Get POs for a vendor.
     * @param vendorId The vendor ID
     * @return List of POs
     */
    List<PurchaseOrder> getPurchaseOrdersByVendor(int vendorId);

    /**
     * Get a PO by ID.
     * @param poId The PO ID
     * @return PurchaseOrder or null
     */
    PurchaseOrder getPurchaseOrderById(int poId);

    /**
     * Get a PO by number.
     * @param poNumber The PO number
     * @return PurchaseOrder or null
     */
    PurchaseOrder getPurchaseOrderByNumber(String poNumber);

    /**
     * Create a purchase order.
     * @param po The PO data
     * @return Created PO with ID and number
     */
    PurchaseOrder createPurchaseOrder(PurchaseOrder po);

    /**
     * Update a purchase order.
     * @param po The PO to update
     * @return true if successful
     */
    boolean updatePurchaseOrder(PurchaseOrder po);

    /**
     * Submit a PO for approval.
     * @param poId The PO ID
     * @return true if submitted
     */
    boolean submitPurchaseOrder(int poId);

    /**
     * Approve a PO.
     * @param poId The PO ID
     * @param approverId The approving employee ID
     * @return true if approved
     */
    boolean approvePurchaseOrder(int poId, int approverId);

    /**
     * Cancel a PO.
     * @param poId The PO ID
     * @param reason Cancellation reason
     * @return true if cancelled
     */
    boolean cancelPurchaseOrder(int poId, String reason);

    /**
     * Receive goods against a PO.
     * @param poId The PO ID
     * @param itemsReceived List of items with quantities received
     * @return true if recorded
     */
    boolean receiveGoods(int poId, List<PurchaseOrderItem> itemsReceived);


    // ==================== INVENTORY MANAGEMENT ====================

    /**
     * Get all products/inventory items.
     * @return List of all products
     */
    List<Product> getAllProducts();

    /**
     * Get products by category.
     * @param category The category
     * @return List of products
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Get a product by ID.
     * @param productId The product ID
     * @return Product or null
     */
    Product getProductById(int productId);

    /**
     * Get product by SKU.
     * @param sku The SKU
     * @return Product or null
     */
    Product getProductBySku(String sku);

    /**
     * Search products.
     * @param searchTerm Search string
     * @return List of matching products
     */
    List<Product> searchProducts(String searchTerm);

    /**
     * Get products that need reordering (below reorder level).
     * @return List of products needing reorder
     */
    List<Product> getProductsNeedingReorder();

    /**
     * Get out-of-stock products.
     * @return List of out-of-stock products
     */
    List<Product> getOutOfStockProducts();

    /**
     * Create a new product.
     * @param product The product data
     * @return Created product with ID
     */
    Product createProduct(Product product);

    /**
     * Update a product.
     * @param product The product to update
     * @return true if successful
     */
    boolean updateProduct(Product product);

    /**
     * Update stock quantity.
     * @param productId The product ID
     * @param quantityChange The change in quantity (positive for increase, negative for decrease)
     * @param reason Reason for adjustment
     * @return true if successful
     */
    boolean updateStock(int productId, int quantityChange, String reason);


    // ==================== ANALYTICS ====================

    /**
     * Get total inventory value.
     * @return Total value of all inventory
     */
    BigDecimal getTotalInventoryValue();

    /**
     * Get inventory value by category.
     * @return Map of category to value
     */
    Map<String, BigDecimal> getInventoryValueByCategory();

    /**
     * Get vendor performance (on-time delivery rate).
     * @param vendorId The vendor ID
     * @return Percentage of on-time deliveries
     */
    double getVendorOnTimeDeliveryRate(int vendorId);
}
