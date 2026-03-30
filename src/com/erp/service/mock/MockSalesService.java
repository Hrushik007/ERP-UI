package com.erp.service.mock;

import com.erp.model.*;
import com.erp.service.interfaces.SalesService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockSalesService provides sample data for Sales & Order Processing UI.
 *
 * Demonstrates the same patterns as MockHRService:
 * - Singleton pattern for single instance
 * - Interface implementation for contract compliance
 * - In-memory data storage simulating database
 */
public class MockSalesService implements SalesService {

    private Map<Integer, Order> orders;
    private Map<Integer, Customer> customers;
    private Map<Integer, Product> products;
    private int nextOrderId = 1000;
    private int nextOrderItemId = 5000;
    private int nextCustomerId = 500;
    private int nextProductId = 200;

    private static MockSalesService instance;

    public static synchronized MockSalesService getInstance() {
        if (instance == null) {
            instance = new MockSalesService();
        }
        return instance;
    }

    private MockSalesService() {
        orders = new HashMap<>();
        customers = new HashMap<>();
        products = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create sample customers
        createSampleCustomer("Acme Corporation", "John Buyer", "john@acme.com", "CUSTOMER");
        createSampleCustomer("Tech Solutions Inc", "Sarah Tech", "sarah@techsol.com", "VIP");
        createSampleCustomer("Global Industries", "Mike Global", "mike@global.com", "CUSTOMER");
        createSampleCustomer("StartUp Labs", "Emily Start", "emily@startup.com", "PROSPECT");
        createSampleCustomer("Enterprise Co", "David Enter", "david@enterprise.com", "CUSTOMER");

        // Create sample products
        createSampleProduct("LAPTOP-001", "Business Laptop Pro", new BigDecimal("1299.99"), "Electronics", 50);
        createSampleProduct("MONITOR-24", "24-inch LED Monitor", new BigDecimal("299.99"), "Electronics", 100);
        createSampleProduct("KEYBOARD-WL", "Wireless Keyboard", new BigDecimal("79.99"), "Accessories", 200);
        createSampleProduct("MOUSE-WL", "Wireless Mouse", new BigDecimal("49.99"), "Accessories", 250);
        createSampleProduct("DESK-STD", "Standard Office Desk", new BigDecimal("399.99"), "Furniture", 30);
        createSampleProduct("CHAIR-ERG", "Ergonomic Office Chair", new BigDecimal("549.99"), "Furniture", 40);
        createSampleProduct("WEBCAM-HD", "HD Webcam", new BigDecimal("89.99"), "Electronics", 150);
        createSampleProduct("HEADSET-BT", "Bluetooth Headset", new BigDecimal("129.99"), "Accessories", 120);

        // Create sample orders
        createSampleOrder(500, 1, Arrays.asList(
            new int[]{200, 2}, new int[]{201, 2}, new int[]{202, 2}
        ), "DELIVERED");

        createSampleOrder(501, 2, Arrays.asList(
            new int[]{203, 5}, new int[]{204, 5}
        ), "SHIPPED");

        createSampleOrder(502, 1, Arrays.asList(
            new int[]{205, 1}, new int[]{206, 1}
        ), "PROCESSING");

        createSampleOrder(503, 3, Arrays.asList(
            new int[]{200, 1}, new int[]{207, 2}
        ), "CONFIRMED");

        createSampleOrder(504, 2, Arrays.asList(
            new int[]{201, 3}, new int[]{202, 3}, new int[]{203, 3}
        ), "PENDING");
    }

    private void createSampleCustomer(String company, String contact, String email, String type) {
        Customer c = new Customer();
        c.setCustomerId(nextCustomerId++);
        c.setCompanyName(company);
        c.setContactName(contact);
        c.setEmail(email);
        c.setPhone("555-" + (1000 + (int)(Math.random() * 9000)));
        c.setCustomerType(type);
        c.setAddress("123 Business St");
        c.setCity("Commerce City");
        c.setState("CA");
        c.setPostalCode("90210");
        c.setCountry("USA");
        c.setCreditLimit(new BigDecimal("50000"));
        c.setActive(true);
        c.setCreatedAt(LocalDate.now().minusDays((int)(Math.random() * 365)));
        customers.put(c.getCustomerId(), c);
    }

    private void createSampleProduct(String sku, String name, BigDecimal price, String category, int stock) {
        Product p = new Product();
        p.setProductId(nextProductId++);
        p.setSku(sku);
        p.setName(name);
        p.setUnitPrice(price);
        p.setCostPrice(price.multiply(new BigDecimal("0.6")));
        p.setCategory(category);
        p.setQuantityInStock(stock);
        p.setReorderLevel(10);
        p.setStatus("ACTIVE");
        p.setActive(true);
        products.put(p.getProductId(), p);
    }

    private void createSampleOrder(int customerId, int salesRepId, List<int[]> items, String status) {
        Order order = new Order();
        order.setOrderId(nextOrderId++);
        order.setOrderNumber("ORD-" + String.format("%05d", order.getOrderId()));
        order.setCustomerId(customerId);
        order.setSalesRepId(salesRepId);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now().minusDays((int)(Math.random() * 30)));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (int[] item : items) {
            int productId = item[0];
            int qty = item[1];
            Product product = products.get(productId);
            if (product != null) {
                OrderItem oi = new OrderItem();
                oi.setOrderItemId(nextOrderItemId++);
                oi.setOrderId(order.getOrderId());
                oi.setProductId(productId);
                oi.setProductName(product.getName());
                oi.setProductSku(product.getSku());
                oi.setQuantity(qty);
                oi.setUnitPrice(product.getUnitPrice());
                oi.calculateLineTotal();
                orderItems.add(oi);
                total = total.add(oi.getLineTotal());
            }
        }

        order.setItems(orderItems);
        order.setSubtotal(total);
        order.setTaxAmount(total.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP));
        order.setTotalAmount(order.getSubtotal().add(order.getTaxAmount()));
        order.setCreatedAt(order.getOrderDate());

        if ("DELIVERED".equals(status)) {
            order.setShippedDate(order.getOrderDate().plusDays(2));
            order.setDeliveredDate(order.getOrderDate().plusDays(5));
        } else if ("SHIPPED".equals(status)) {
            order.setShippedDate(order.getOrderDate().plusDays(2));
        }

        orders.put(order.getOrderId(), order);
    }

    // ==================== ORDER MANAGEMENT (Interface Methods) ====================

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orders.values().stream()
                .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByCustomer(int customerId) {
        return orders.values().stream()
                .filter(o -> o.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersBySalesRep(int salesRepId) {
        return orders.values().stream()
                .filter(o -> o.getSalesRepId() == salesRepId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return orders.values().stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderDate().toLocalDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(int orderId) {
        return orders.get(orderId);
    }

    @Override
    public Order getOrderByNumber(String orderNumber) {
        return orders.values().stream()
                .filter(o -> orderNumber.equals(o.getOrderNumber()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Order createOrder(Order order) {
        order.setOrderId(nextOrderId++);
        order.setOrderNumber("ORD-" + String.format("%05d", order.getOrderId()));
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderDate(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrderItemId(nextOrderItemId++);
                item.setOrderId(order.getOrderId());
                item.calculateLineTotal();
                subtotal = subtotal.add(item.getLineTotal());
            }
        }
        order.setSubtotal(subtotal);
        order.setTaxAmount(subtotal.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP));
        order.setTotalAmount(order.getSubtotal().add(order.getTaxAmount()));

        orders.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public boolean updateOrder(Order order) {
        if (orders.containsKey(order.getOrderId())) {
            order.setUpdatedAt(LocalDateTime.now());
            orders.put(order.getOrderId(), order);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());

            if ("SHIPPED".equals(status)) {
                order.setShippedDate(LocalDateTime.now());
            } else if ("DELIVERED".equals(status)) {
                if (order.getShippedDate() == null) {
                    order.setShippedDate(LocalDateTime.now().minusDays(2));
                }
                order.setDeliveredDate(LocalDateTime.now());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelOrder(int orderId, String reason) {
        Order order = orders.get(orderId);
        if (order != null && !"DELIVERED".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            order.setNotes(reason);
            order.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== ORDER ITEMS (Interface Methods) ====================

    @Override
    public OrderItem addOrderItem(int orderId, OrderItem item) {
        Order order = orders.get(orderId);
        if (order != null) {
            item.setOrderItemId(nextOrderItemId++);
            item.setOrderId(orderId);
            item.calculateLineTotal();

            if (order.getItems() == null) {
                order.setItems(new ArrayList<>());
            }
            order.getItems().add(item);
            recalculateOrderTotals(order);
            return item;
        }
        return null;
    }

    @Override
    public boolean updateOrderItem(OrderItem item) {
        Order order = orders.get(item.getOrderId());
        if (order != null && order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                if (order.getItems().get(i).getOrderItemId() == item.getOrderItemId()) {
                    item.calculateLineTotal();
                    order.getItems().set(i, item);
                    recalculateOrderTotals(order);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeOrderItem(int orderItemId) {
        // Find which order contains this item
        for (Order order : orders.values()) {
            if (order.getItems() != null) {
                boolean removed = order.getItems().removeIf(item -> item.getOrderItemId() == orderItemId);
                if (removed) {
                    recalculateOrderTotals(order);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        Order order = orders.get(orderId);
        if (order != null && order.getItems() != null) {
            return new ArrayList<>(order.getItems());
        }
        return new ArrayList<>();
    }

    private void recalculateOrderTotals(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                subtotal = subtotal.add(item.getLineTotal());
            }
        }
        order.setSubtotal(subtotal);
        order.setTaxAmount(subtotal.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP));
        order.setTotalAmount(order.getSubtotal().add(order.getTaxAmount()));
        order.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== PRICING (Interface Methods) ====================

    @Override
    public BigDecimal getProductPrice(int productId, int customerId, int quantity) {
        Product product = products.get(productId);
        if (product == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = product.getUnitPrice();

        // Apply volume discount
        if (quantity >= 100) {
            basePrice = basePrice.multiply(new BigDecimal("0.85")); // 15% discount
        } else if (quantity >= 50) {
            basePrice = basePrice.multiply(new BigDecimal("0.90")); // 10% discount
        } else if (quantity >= 10) {
            basePrice = basePrice.multiply(new BigDecimal("0.95")); // 5% discount
        }

        // Apply VIP customer discount
        Customer customer = customers.get(customerId);
        if (customer != null && "VIP".equals(customer.getCustomerType())) {
            basePrice = basePrice.multiply(new BigDecimal("0.95")); // Additional 5%
        }

        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean applyDiscount(int orderId, BigDecimal discountPercent, String reason) {
        Order order = orders.get(orderId);
        if (order != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multiplier = BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            order.setSubtotal(order.getSubtotal().multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
            order.setTaxAmount(order.getSubtotal().multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP));
            order.setTotalAmount(order.getSubtotal().add(order.getTaxAmount()));
            BigDecimal discountAmt = order.getSubtotal().multiply(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            order.setDiscountAmount(discountAmt.setScale(2, RoundingMode.HALF_UP));
            order.setNotes((order.getNotes() != null ? order.getNotes() + "; " : "") + "Discount " + discountPercent + "%: " + reason);
            order.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== SALES ANALYTICS (Interface Methods) ====================

    @Override
    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        return getOrdersByDateRange(startDate, endDate).stream()
                .filter(o -> !"CANCELLED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getSalesByStatus() {
        Map<String, BigDecimal> result = new HashMap<>();
        for (Order order : orders.values()) {
            String status = order.getStatus();
            result.merge(status, order.getTotalAmount(), BigDecimal::add);
        }
        return result;
    }

    @Override
    public Map<Integer, BigDecimal> getSalesBySalesRep(LocalDate startDate, LocalDate endDate) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Order order : getOrdersByDateRange(startDate, endDate)) {
            if (!"CANCELLED".equals(order.getStatus())) {
                result.merge(order.getSalesRepId(), order.getTotalAmount(), BigDecimal::add);
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Integer> getTopSellingProducts(int limit, LocalDate startDate, LocalDate endDate) {
        Map<Integer, Integer> productQuantities = new HashMap<>();

        for (Order order : getOrdersByDateRange(startDate, endDate)) {
            if (!"CANCELLED".equals(order.getStatus()) && order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
                }
            }
        }

        // Sort and limit
        return productQuantities.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Integer> getOrderCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        for (Order order : orders.values()) {
            result.merge(order.getStatus(), 1, Integer::sum);
        }
        return result;
    }

    // ==================== HELPER METHODS (Not in Interface) ====================
    // These are additional methods for UI convenience, not part of the interface contract

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public Customer getCustomerById(int customerId) {
        return customers.get(customerId);
    }

    public List<Customer> searchCustomers(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return customers.values().stream()
                .filter(c -> c.getCompanyName().toLowerCase().contains(term) ||
                            c.getContactName().toLowerCase().contains(term) ||
                            c.getEmail().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> getAvailableProducts() {
        return products.values().stream()
                .filter(p -> p.isActive() && p.getQuantityInStock() > 0)
                .collect(Collectors.toList());
    }

    public Product getProductById(int productId) {
        return products.get(productId);
    }

    public Product getProductBySku(String sku) {
        return products.values().stream()
                .filter(p -> sku.equals(p.getSku()))
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchProducts(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(term) ||
                            p.getSku().toLowerCase().contains(term) ||
                            (p.getCategory() != null && p.getCategory().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .collect(Collectors.toList());
    }
}
