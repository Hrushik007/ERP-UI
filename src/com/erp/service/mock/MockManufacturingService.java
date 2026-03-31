package com.erp.service.mock;

import com.erp.model.BillOfMaterials;
import com.erp.model.BOMItem;
import com.erp.model.Product;
import com.erp.model.WorkOrder;
import com.erp.service.interfaces.ManufacturingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockManufacturingService provides sample data for Manufacturing Module UI.
 */
public class MockManufacturingService implements ManufacturingService {

    private Map<Integer, BillOfMaterials> boms;
    private Map<Integer, BOMItem> bomItems;
    private Map<Integer, WorkOrder> workOrders;

    private int nextBomId = 1;
    private int nextBomItemId = 1;
    private int nextWorkOrderId = 1;

    private static MockManufacturingService instance;

    public static synchronized MockManufacturingService getInstance() {
        if (instance == null) {
            instance = new MockManufacturingService();
        }
        return instance;
    }

    private MockManufacturingService() {
        boms = new HashMap<>();
        bomItems = new HashMap<>();
        workOrders = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create sample BOMs
        createSampleBOM("BOM-001", 1, "Desktop Computer", "1.0", "ACTIVE");
        createSampleBOM("BOM-002", 2, "Laptop Computer", "1.0", "ACTIVE");
        createSampleBOM("BOM-003", 3, "Tablet Device", "2.0", "ACTIVE");
        createSampleBOM("BOM-004", 4, "Server Unit", "1.0", "DRAFT");

        // Create sample work orders
        createSampleWorkOrder("WO-2024-001", 1, "Desktop Computer", 50, "IN_PROGRESS", "HIGH");
        createSampleWorkOrder("WO-2024-002", 2, "Laptop Computer", 30, "PLANNED", "MEDIUM");
        createSampleWorkOrder("WO-2024-003", 3, "Tablet Device", 100, "RELEASED", "HIGH");
        createSampleWorkOrder("WO-2024-004", 1, "Desktop Computer", 25, "COMPLETED", "LOW");
        createSampleWorkOrder("WO-2024-005", 2, "Laptop Computer", 40, "IN_PROGRESS", "URGENT");
    }

    private void createSampleBOM(String bomCode, int productId, String productName, String version, String status) {
        BillOfMaterials bom = new BillOfMaterials();
        bom.setBomId(nextBomId++);
        bom.setBomCode(bomCode);
        bom.setProductId(productId);
        bom.setVersion(version);
        bom.setStatus(status);
        bom.setDescription("BOM for " + productName);
        bom.setOutputQuantity(1);
        bom.setUnitOfMeasure("unit");
        bom.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 90)));

        // Add sample components
        List<BOMItem> components = new ArrayList<>();
        components.add(createBOMComponent(bom.getBomId(), 101, "Motherboard", new BigDecimal("1")));
        components.add(createBOMComponent(bom.getBomId(), 102, "CPU", new BigDecimal("1")));
        components.add(createBOMComponent(bom.getBomId(), 103, "RAM 8GB", new BigDecimal("2")));
        components.add(createBOMComponent(bom.getBomId(), 104, "SSD 512GB", new BigDecimal("1")));
        components.add(createBOMComponent(bom.getBomId(), 105, "Power Supply", new BigDecimal("1")));

        bom.setComponents(components);
        boms.put(bom.getBomId(), bom);
    }

    private BOMItem createBOMComponent(int bomId, int componentId, String componentName, BigDecimal quantity) {
        BOMItem item = new BOMItem();
        item.setBomItemId(nextBomItemId++);
        item.setBomId(bomId);
        item.setComponentProductId(componentId);
        item.setComponentName(componentName);
        item.setQuantity(quantity);
        item.setUnitOfMeasure("unit");
        bomItems.put(item.getBomItemId(), item);
        return item;
    }

    private void createSampleWorkOrder(String woNumber, int productId, String productName, int quantity, String status, String priority) {
        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(nextWorkOrderId++);
        wo.setWorkOrderNumber(woNumber);
        wo.setProductId(productId);
        wo.setProduct(createDummyProduct(productId, productName));
        wo.setQuantityOrdered(quantity);
        wo.setStatus(status);
        wo.setPriority(priority);
        wo.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 30)));

        // Set dates based on status
        LocalDate today = LocalDate.now();
        wo.setPlannedStartDate(today.minusDays(5));
        wo.setPlannedEndDate(today.plusDays(10));

        if ("IN_PROGRESS".equals(status) || "COMPLETED".equals(status)) {
            wo.setActualStartDate(today.minusDays(3));
            wo.setQuantityCompleted((int)(quantity * 0.4));
        }

        if ("COMPLETED".equals(status)) {
            wo.setActualEndDate(today.minusDays(1));
            wo.setQuantityCompleted(quantity);
        }

        wo.setEstimatedCost(BigDecimal.valueOf(quantity * 1000));
        wo.setActualCost(BigDecimal.valueOf(quantity * 950));

        workOrders.put(wo.getWorkOrderId(), wo);
    }

    private Product createDummyProduct(int id, String name) {
        Product p = new Product();
        p.setProductId(id);
        p.setName(name);
        p.setSku("SKU-" + id);
        return p;
    }

    // ==================== BILL OF MATERIALS ====================

    @Override
    public List<BillOfMaterials> getAllBOMs() {
        return new ArrayList<>(boms.values());
    }

    @Override
    public List<BillOfMaterials> getActiveBOMs() {
        return boms.values().stream()
                .filter(b -> "ACTIVE".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public BillOfMaterials getBOMById(int bomId) {
        return boms.get(bomId);
    }

    @Override
    public BillOfMaterials getBOMByProduct(int productId) {
        return boms.values().stream()
                .filter(b -> b.getProductId() == productId && "ACTIVE".equals(b.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public BillOfMaterials createBOM(BillOfMaterials bom) {
        bom.setBomId(nextBomId++);
        bom.setCreatedAt(LocalDateTime.now());
        boms.put(bom.getBomId(), bom);
        return bom;
    }

    @Override
    public boolean updateBOM(BillOfMaterials bom) {
        if (boms.containsKey(bom.getBomId())) {
            bom.setUpdatedAt(LocalDateTime.now());
            boms.put(bom.getBomId(), bom);
            return true;
        }
        return false;
    }

    @Override
    public boolean activateBOM(int bomId) {
        BillOfMaterials bom = boms.get(bomId);
        if (bom != null) {
            // Deactivate other BOMs for same product
            boms.values().stream()
                    .filter(b -> b.getProductId() == bom.getProductId())
                    .forEach(b -> b.setStatus("OBSOLETE"));

            bom.setStatus("ACTIVE");
            bom.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public BOMItem addBOMComponent(int bomId, BOMItem item) {
        BillOfMaterials bom = boms.get(bomId);
        if (bom != null) {
            item.setBomItemId(nextBomItemId++);
            item.setBomId(bomId);
            bomItems.put(item.getBomItemId(), item);
            bom.addComponent(item);
            return item;
        }
        return null;
    }

    @Override
    public boolean removeBOMComponent(int bomItemId) {
        BOMItem item = bomItems.remove(bomItemId);
        if (item != null) {
            BillOfMaterials bom = boms.get(item.getBomId());
            if (bom != null) {
                bom.getComponents().removeIf(c -> c.getBomItemId() == bomItemId);
            }
            return true;
        }
        return false;
    }

    // ==================== WORK ORDERS ====================

    @Override
    public List<WorkOrder> getAllWorkOrders() {
        return new ArrayList<>(workOrders.values());
    }

    @Override
    public List<WorkOrder> getWorkOrdersByStatus(String status) {
        return workOrders.values().stream()
                .filter(wo -> status.equals(wo.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getWorkOrdersByProduct(int productId) {
        return workOrders.values().stream()
                .filter(wo -> wo.getProductId() == productId)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getOverdueWorkOrders() {
        return workOrders.values().stream()
                .filter(WorkOrder::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public WorkOrder getWorkOrderById(int workOrderId) {
        return workOrders.get(workOrderId);
    }

    @Override
    public WorkOrder getWorkOrderByNumber(String workOrderNumber) {
        return workOrders.values().stream()
                .filter(wo -> workOrderNumber.equals(wo.getWorkOrderNumber()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        workOrder.setWorkOrderId(nextWorkOrderId++);
        workOrder.setWorkOrderNumber("WO-" + String.format("%06d", workOrder.getWorkOrderId()));
        workOrder.setCreatedAt(LocalDateTime.now());
        workOrders.put(workOrder.getWorkOrderId(), workOrder);
        return workOrder;
    }

    @Override
    public boolean updateWorkOrder(WorkOrder workOrder) {
        if (workOrders.containsKey(workOrder.getWorkOrderId())) {
            workOrder.setUpdatedAt(LocalDateTime.now());
            workOrders.put(workOrder.getWorkOrderId(), workOrder);
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseWorkOrder(int workOrderId) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null && "PLANNED".equals(wo.getStatus())) {
            wo.setStatus("RELEASED");
            wo.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean startWorkOrder(int workOrderId) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null && wo.canStart()) {
            wo.start();
            return true;
        }
        return false;
    }

    @Override
    public boolean recordProduction(int workOrderId, int quantityCompleted, int quantityScrapped) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null) {
            wo.setQuantityCompleted(quantityCompleted);
            wo.setQuantityScrapped(quantityScrapped);
            wo.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean completeWorkOrder(int workOrderId) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null && !wo.isCompleted()) {
            wo.complete();
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelWorkOrder(int workOrderId, String reason) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null && !wo.isCompleted()) {
            wo.setStatus("CANCELLED");
            wo.setNotes("Cancelled: " + reason);
            wo.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== PRODUCTION PLANNING ====================

    @Override
    public List<WorkOrder> getScheduledWorkOrders(LocalDate startDate, LocalDate endDate) {
        return workOrders.values().stream()
                .filter(wo -> {
                    LocalDate planned = wo.getPlannedStartDate();
                    return planned != null &&
                           !planned.isBefore(startDate) &&
                           !planned.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Integer> checkMaterialAvailability(int workOrderId) {
        // Mock implementation - returns sample availability
        Map<Integer, Integer> availability = new HashMap<>();
        availability.put(101, 100); // Motherboard
        availability.put(102, 150); // CPU
        availability.put(103, 200); // RAM
        availability.put(104, 80);  // SSD
        availability.put(105, 120); // Power Supply
        return availability;
    }

    @Override
    public List<WorkOrder> getProductionSchedule(LocalDate date) {
        return workOrders.values().stream()
                .filter(wo -> {
                    LocalDate planned = wo.getPlannedStartDate();
                    return planned != null && planned.equals(date);
                })
                .collect(Collectors.toList());
    }

    // ==================== QUALITY MANAGEMENT ====================

    @Override
    public boolean recordQualityCheck(int workOrderId, int passed, int failed, String notes) {
        WorkOrder wo = workOrders.get(workOrderId);
        if (wo != null) {
            String existingNotes = wo.getNotes() != null ? wo.getNotes() + "\n" : "";
            wo.setNotes(existingNotes + "Quality Check: " + passed + " passed, " + failed + " failed. " + notes);
            wo.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getQualityStats(int productId) {
        Map<String, Object> stats = new HashMap<>();

        List<WorkOrder> productWOs = getWorkOrdersByProduct(productId);
        int totalProduced = productWOs.stream()
                .mapToInt(WorkOrder::getQuantityCompleted)
                .sum();

        // Mock pass rate
        int passed = (int)(totalProduced * 0.95);
        int failed = totalProduced - passed;
        double passRate = totalProduced > 0 ? (double)passed / totalProduced * 100 : 0;

        stats.put("totalProduced", totalProduced);
        stats.put("passed", passed);
        stats.put("failed", failed);
        stats.put("passRate", passRate);

        return stats;
    }
}
