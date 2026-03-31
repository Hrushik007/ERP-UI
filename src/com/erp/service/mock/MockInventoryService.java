package com.erp.service.mock;

import com.erp.model.*;
import com.erp.service.interfaces.InventoryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockInventoryService provides sample data for Inventory Management UI.
 */
public class MockInventoryService implements InventoryService {

    private Map<Integer, Warehouse> warehouses;
    private Map<Integer, StockLevel> stockLevels;
    private Map<Integer, StockMovement> movements;
    private Map<Integer, Product> products;

    private int nextWarehouseId = 1;
    private int nextStockLevelId = 1;
    private int nextMovementId = 1;

    private static MockInventoryService instance;

    public static synchronized MockInventoryService getInstance() {
        if (instance == null) {
            instance = new MockInventoryService();
        }
        return instance;
    }

    private MockInventoryService() {
        warehouses = new HashMap<>();
        stockLevels = new HashMap<>();
        movements = new HashMap<>();
        products = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create warehouses
        createWarehouse("MAIN", "Main Warehouse", "MAIN", "123 Industrial Blvd", "Los Angeles", "CA", 10000);
        createWarehouse("EAST", "East Distribution Center", "DISTRIBUTION", "456 Commerce Way", "New York", "NY", 8000);
        createWarehouse("WEST", "West Coast Fulfillment", "DISTRIBUTION", "789 Pacific Ave", "Seattle", "WA", 6000);

        // Create sample products (mirror from sales)
        createProduct(200, "LAPTOP-001", "Business Laptop Pro", new BigDecimal("1299.99"));
        createProduct(201, "MONITOR-24", "24-inch LED Monitor", new BigDecimal("299.99"));
        createProduct(202, "KEYBOARD-WL", "Wireless Keyboard", new BigDecimal("79.99"));
        createProduct(203, "MOUSE-WL", "Wireless Mouse", new BigDecimal("49.99"));
        createProduct(204, "DESK-STD", "Standard Office Desk", new BigDecimal("399.99"));
        createProduct(205, "CHAIR-ERG", "Ergonomic Office Chair", new BigDecimal("549.99"));
        createProduct(206, "WEBCAM-HD", "HD Webcam", new BigDecimal("89.99"));
        createProduct(207, "HEADSET-BT", "Bluetooth Headset", new BigDecimal("129.99"));

        // Create stock levels for each product in each warehouse
        for (Product p : products.values()) {
            for (Warehouse w : warehouses.values()) {
                int baseQty = 20 + (int)(Math.random() * 80);
                createStockLevel(p, w, baseQty);
            }
        }

        // Create some sample movements
        createSampleMovements();
    }

    private void createWarehouse(String code, String name, String type, String address, String city, String state, int capacity) {
        Warehouse w = new Warehouse();
        w.setWarehouseId(nextWarehouseId++);
        w.setCode(code);
        w.setName(name);
        w.setType(type);
        w.setAddress(address);
        w.setCity(city);
        w.setState(state);
        w.setCountry("USA");
        w.setCapacity(capacity);
        w.setActive(true);
        w.setCreatedAt(LocalDateTime.now().minusMonths(6));
        warehouses.put(w.getWarehouseId(), w);
    }

    private void createProduct(int id, String sku, String name, BigDecimal price) {
        Product p = new Product();
        p.setProductId(id);
        p.setSku(sku);
        p.setName(name);
        p.setUnitPrice(price);
        p.setActive(true);
        products.put(id, p);
    }

    private void createStockLevel(Product product, Warehouse warehouse, int quantity) {
        StockLevel sl = new StockLevel();
        sl.setStockLevelId(nextStockLevelId++);
        sl.setProductId(product.getProductId());
        sl.setProductName(product.getName());
        sl.setProductSku(product.getSku());
        sl.setWarehouseId(warehouse.getWarehouseId());
        sl.setWarehouseName(warehouse.getName());
        sl.setQuantity(quantity);
        sl.setReservedQuantity((int)(quantity * 0.1)); // 10% reserved
        sl.setReorderLevel(15);
        sl.setReorderQuantity(50);
        sl.setMaxQuantity(200);
        sl.setBinLocation("A" + (1 + (int)(Math.random() * 10)) + "-" + (char)('A' + (int)(Math.random() * 6)));
        sl.setUpdatedAt(LocalDateTime.now());
        stockLevels.put(sl.getStockLevelId(), sl);

        // Update warehouse used capacity
        warehouse.setUsedCapacity(warehouse.getUsedCapacity() + quantity);
    }

    private void createSampleMovements() {
        // Create recent movements
        for (int i = 0; i < 15; i++) {
            StockLevel sl = stockLevels.values().stream().skip((int)(Math.random() * stockLevels.size())).findFirst().orElse(null);
            if (sl != null) {
                String type = Math.random() > 0.5 ? "INBOUND" : "OUTBOUND";
                int qty = 5 + (int)(Math.random() * 20);
                String reason = type.equals("INBOUND") ? "PURCHASE" : "SALE";

                StockMovement sm = new StockMovement();
                sm.setMovementId(nextMovementId++);
                sm.setMovementNumber("MOV-" + String.format("%05d", sm.getMovementId()));
                sm.setProductId(sl.getProductId());
                sm.setProductName(sl.getProductName());
                sm.setProductSku(sl.getProductSku());
                sm.setWarehouseId(sl.getWarehouseId());
                sm.setWarehouseName(sl.getWarehouseName());
                sm.setMovementType(type);
                sm.setReason(reason);
                sm.setQuantity(qty);
                sm.setQuantityBefore(sl.getQuantity());
                sm.setQuantityAfter(type.equals("INBOUND") ? sl.getQuantity() + qty : sl.getQuantity() - qty);
                sm.setReferenceType(type.equals("INBOUND") ? "PURCHASE_ORDER" : "ORDER");
                sm.setReferenceNumber("REF-" + (1000 + (int)(Math.random() * 9000)));
                sm.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 30)));
                sm.setCreatedByName("System");
                movements.put(sm.getMovementId(), sm);
            }
        }
    }

    // ==================== WAREHOUSE MANAGEMENT ====================

    @Override
    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouses.values());
    }

    @Override
    public List<Warehouse> getActiveWarehouses() {
        return warehouses.values().stream()
                .filter(Warehouse::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public Warehouse getWarehouseById(int warehouseId) {
        return warehouses.get(warehouseId);
    }

    @Override
    public Warehouse getWarehouseByCode(String code) {
        return warehouses.values().stream()
                .filter(w -> code.equals(w.getCode()))
                .findFirst().orElse(null);
    }

    @Override
    public Warehouse createWarehouse(Warehouse warehouse) {
        warehouse.setWarehouseId(nextWarehouseId++);
        warehouse.setCreatedAt(LocalDateTime.now());
        warehouses.put(warehouse.getWarehouseId(), warehouse);
        return warehouse;
    }

    @Override
    public boolean updateWarehouse(Warehouse warehouse) {
        if (warehouses.containsKey(warehouse.getWarehouseId())) {
            warehouse.setUpdatedAt(LocalDateTime.now());
            warehouses.put(warehouse.getWarehouseId(), warehouse);
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivateWarehouse(int warehouseId) {
        Warehouse w = warehouses.get(warehouseId);
        if (w != null) {
            w.setActive(false);
            w.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== STOCK LEVELS ====================

    @Override
    public List<StockLevel> getAllStockLevels() {
        return new ArrayList<>(stockLevels.values());
    }

    @Override
    public List<StockLevel> getStockLevelsByWarehouse(int warehouseId) {
        return stockLevels.values().stream()
                .filter(sl -> sl.getWarehouseId() == warehouseId)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getStockLevelsByProduct(int productId) {
        return stockLevels.values().stream()
                .filter(sl -> sl.getProductId() == productId)
                .collect(Collectors.toList());
    }

    @Override
    public StockLevel getStockLevel(int productId, int warehouseId) {
        return stockLevels.values().stream()
                .filter(sl -> sl.getProductId() == productId && sl.getWarehouseId() == warehouseId)
                .findFirst().orElse(null);
    }

    @Override
    public int getTotalStock(int productId) {
        return stockLevels.values().stream()
                .filter(sl -> sl.getProductId() == productId)
                .mapToInt(StockLevel::getQuantity)
                .sum();
    }

    @Override
    public int getAvailableStock(int productId) {
        return stockLevels.values().stream()
                .filter(sl -> sl.getProductId() == productId)
                .mapToInt(StockLevel::getAvailableQuantity)
                .sum();
    }

    @Override
    public boolean updateStockLevel(StockLevel stockLevel) {
        if (stockLevels.containsKey(stockLevel.getStockLevelId())) {
            stockLevel.setUpdatedAt(LocalDateTime.now());
            stockLevels.put(stockLevel.getStockLevelId(), stockLevel);
            return true;
        }
        return false;
    }

    // ==================== STOCK MOVEMENTS ====================

    @Override
    public List<StockMovement> getAllMovements() {
        return movements.values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovement> getMovementsByType(String movementType) {
        return movements.values().stream()
                .filter(m -> movementType.equals(m.getMovementType()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovement> getMovementsByWarehouse(int warehouseId) {
        return movements.values().stream()
                .filter(m -> m.getWarehouseId() == warehouseId)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovement> getMovementsByProduct(int productId) {
        return movements.values().stream()
                .filter(m -> m.getProductId() == productId)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovement> getMovementsByDateRange(LocalDate startDate, LocalDate endDate) {
        return movements.values().stream()
                .filter(m -> {
                    LocalDate d = m.getCreatedAt().toLocalDate();
                    return !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public StockMovement getMovementById(int movementId) {
        return movements.get(movementId);
    }

    @Override
    public StockMovement recordInbound(int productId, int warehouseId, int quantity,
                                       String reason, String referenceNumber, String notes) {
        StockLevel sl = getStockLevel(productId, warehouseId);
        if (sl == null) return null;

        int before = sl.getQuantity();
        sl.setQuantity(before + quantity);
        sl.setUpdatedAt(LocalDateTime.now());

        StockMovement sm = new StockMovement();
        sm.setMovementId(nextMovementId++);
        sm.setMovementNumber("MOV-" + String.format("%05d", sm.getMovementId()));
        sm.setProductId(productId);
        sm.setProductName(sl.getProductName());
        sm.setProductSku(sl.getProductSku());
        sm.setWarehouseId(warehouseId);
        sm.setWarehouseName(sl.getWarehouseName());
        sm.setMovementType("INBOUND");
        sm.setReason(reason);
        sm.setQuantity(quantity);
        sm.setQuantityBefore(before);
        sm.setQuantityAfter(sl.getQuantity());
        sm.setReferenceNumber(referenceNumber);
        sm.setNotes(notes);
        sm.setCreatedAt(LocalDateTime.now());

        movements.put(sm.getMovementId(), sm);
        return sm;
    }

    @Override
    public StockMovement recordOutbound(int productId, int warehouseId, int quantity,
                                        String reason, String referenceNumber, String notes) {
        StockLevel sl = getStockLevel(productId, warehouseId);
        if (sl == null || sl.getAvailableQuantity() < quantity) return null;

        int before = sl.getQuantity();
        sl.setQuantity(before - quantity);
        sl.setUpdatedAt(LocalDateTime.now());

        StockMovement sm = new StockMovement();
        sm.setMovementId(nextMovementId++);
        sm.setMovementNumber("MOV-" + String.format("%05d", sm.getMovementId()));
        sm.setProductId(productId);
        sm.setProductName(sl.getProductName());
        sm.setProductSku(sl.getProductSku());
        sm.setWarehouseId(warehouseId);
        sm.setWarehouseName(sl.getWarehouseName());
        sm.setMovementType("OUTBOUND");
        sm.setReason(reason);
        sm.setQuantity(quantity);
        sm.setQuantityBefore(before);
        sm.setQuantityAfter(sl.getQuantity());
        sm.setReferenceNumber(referenceNumber);
        sm.setNotes(notes);
        sm.setCreatedAt(LocalDateTime.now());

        movements.put(sm.getMovementId(), sm);
        return sm;
    }

    @Override
    public StockMovement recordAdjustment(int productId, int warehouseId, int newQuantity,
                                          String reason, String notes) {
        StockLevel sl = getStockLevel(productId, warehouseId);
        if (sl == null) return null;

        int before = sl.getQuantity();
        int diff = newQuantity - before;
        sl.setQuantity(newQuantity);
        sl.setUpdatedAt(LocalDateTime.now());

        StockMovement sm = new StockMovement();
        sm.setMovementId(nextMovementId++);
        sm.setMovementNumber("MOV-" + String.format("%05d", sm.getMovementId()));
        sm.setProductId(productId);
        sm.setProductName(sl.getProductName());
        sm.setProductSku(sl.getProductSku());
        sm.setWarehouseId(warehouseId);
        sm.setWarehouseName(sl.getWarehouseName());
        sm.setMovementType("ADJUSTMENT");
        sm.setReason(reason);
        sm.setQuantity(Math.abs(diff));
        sm.setQuantityBefore(before);
        sm.setQuantityAfter(newQuantity);
        sm.setNotes(notes);
        sm.setCreatedAt(LocalDateTime.now());

        movements.put(sm.getMovementId(), sm);
        return sm;
    }

    // ==================== REORDER ALERTS ====================

    @Override
    public List<StockLevel> getReorderAlerts() {
        return stockLevels.values().stream()
                .filter(StockLevel::needsReorder)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getOutOfStockProducts() {
        return stockLevels.values().stream()
                .filter(sl -> sl.getQuantity() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getOverstockProducts() {
        return stockLevels.values().stream()
                .filter(StockLevel::isOverstock)
                .collect(Collectors.toList());
    }

    // ==================== ANALYTICS ====================

    @Override
    public Map<String, Integer> getStockCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("IN_STOCK", 0);
        result.put("LOW_STOCK", 0);
        result.put("OUT_OF_STOCK", 0);
        result.put("OVERSTOCK", 0);

        for (StockLevel sl : stockLevels.values()) {
            String status = sl.getStockStatus();
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (StockLevel sl : stockLevels.values()) {
            Product p = products.get(sl.getProductId());
            if (p != null && p.getUnitPrice() != null) {
                total = total.add(p.getUnitPrice().multiply(BigDecimal.valueOf(sl.getQuantity())));
            }
        }
        return total;
    }

    @Override
    public Map<Integer, Double> getWarehouseUtilization() {
        Map<Integer, Double> result = new HashMap<>();
        for (Warehouse w : warehouses.values()) {
            result.put(w.getWarehouseId(), w.getUtilizationPercent());
        }
        return result;
    }

    // Helper method to get products (for UI)
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(int productId) {
        return products.get(productId);
    }
}
