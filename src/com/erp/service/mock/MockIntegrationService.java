package com.erp.service.mock;

import com.erp.service.interfaces.IntegrationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockIntegrationService provides sample data for Integration Module UI.
 *
 * Uses Map-based data structures as defined in the IntegrationService interface.
 *
 * Demonstrates the Singleton pattern - only one instance exists.
 */
public class MockIntegrationService implements IntegrationService {

    private List<Map<String, Object>> syncConfigs;
    private List<Map<String, Object>> events;
    private List<Map<String, Object>> externalIntegrations;
    private List<Map<String, Object>> importExportHistory;
    private List<Map<String, Object>> integrationLogs;

    private int nextEventId = 1;
    private int nextLogId = 1;

    private static MockIntegrationService instance;

    public static synchronized MockIntegrationService getInstance() {
        if (instance == null) {
            instance = new MockIntegrationService();
        }
        return instance;
    }

    private MockIntegrationService() {
        syncConfigs = new ArrayList<>();
        events = new ArrayList<>();
        externalIntegrations = new ArrayList<>();
        importExportHistory = new ArrayList<>();
        integrationLogs = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Sample sync configurations
        createSampleSync("Sales", "Finance", "ORDERS", "SYNCED",
                LocalDateTime.now().minusMinutes(15), 142, 0);
        createSampleSync("Sales", "Inventory", "STOCK_LEVELS", "SYNCED",
                LocalDateTime.now().minusMinutes(30), 89, 0);
        createSampleSync("HR", "Finance", "PAYROLL", "PENDING",
                LocalDateTime.now().minusHours(2), 0, 23);
        createSampleSync("CRM", "Marketing", "LEADS", "SYNCED",
                LocalDateTime.now().minusMinutes(45), 67, 0);
        createSampleSync("Inventory", "Manufacturing", "BOM_ITEMS", "ERROR",
                LocalDateTime.now().minusHours(4), 0, 15);
        createSampleSync("Finance", "Accounting", "TRANSACTIONS", "SYNCED",
                LocalDateTime.now().minusMinutes(10), 256, 0);
        createSampleSync("Sales", "CRM", "CUSTOMERS", "SYNCED",
                LocalDateTime.now().minusHours(1), 38, 0);
        createSampleSync("Manufacturing", "Inventory", "WORK_ORDERS", "PENDING",
                LocalDateTime.now().minusHours(3), 0, 7);

        // Sample events
        createSampleEvent("ORDER_CREATED", "Sales", Map.of("orderId", "ORD-2024-0156", "amount", "$4,500"), "DELIVERED");
        createSampleEvent("PAYMENT_RECEIVED", "Finance", Map.of("invoiceId", "INV-2024-0088", "amount", "$12,000"), "DELIVERED");
        createSampleEvent("STOCK_LOW", "Inventory", Map.of("productId", "PRD-042", "currentStock", "5"), "PENDING");
        createSampleEvent("EMPLOYEE_CREATED", "HR", Map.of("employeeId", "EMP-089", "name", "Jane Wilson"), "DELIVERED");
        createSampleEvent("LEAD_CONVERTED", "CRM", Map.of("leadId", "LD-2024-034", "customer", "Acme Corp"), "PENDING");
        createSampleEvent("PO_APPROVED", "Manufacturing", Map.of("poId", "PO-2024-0042", "vendor", "Steel Supply Co"), "FAILED");
        createSampleEvent("INVOICE_OVERDUE", "Accounting", Map.of("invoiceId", "INV-2024-0072", "daysOverdue", "15"), "PENDING");

        // Sample external integrations
        createSampleIntegration("Salesforce CRM", "CRM", "CONNECTED", "https://api.salesforce.com",
                LocalDateTime.now().minusMinutes(5), true);
        createSampleIntegration("QuickBooks", "ACCOUNTING", "CONNECTED", "https://api.quickbooks.com",
                LocalDateTime.now().minusMinutes(12), true);
        createSampleIntegration("Stripe Payments", "PAYMENT", "CONNECTED", "https://api.stripe.com",
                LocalDateTime.now().minusMinutes(3), true);
        createSampleIntegration("Shopify Store", "E_COMMERCE", "DISCONNECTED", "https://api.shopify.com",
                LocalDateTime.now().minusDays(2), false);
        createSampleIntegration("SendGrid Email", "EMAIL", "CONNECTED", "https://api.sendgrid.com",
                LocalDateTime.now().minusMinutes(20), true);
        createSampleIntegration("Twilio SMS", "MESSAGING", "ERROR", "https://api.twilio.com",
                LocalDateTime.now().minusHours(6), true);
        createSampleIntegration("AWS S3 Storage", "STORAGE", "CONNECTED", "https://s3.amazonaws.com",
                LocalDateTime.now().minusMinutes(8), true);

        // Sample import/export history
        createSampleImportExport("IMPORT", "CUSTOMERS", "CSV", 150, 148, 2, LocalDateTime.now().minusHours(3));
        createSampleImportExport("EXPORT", "ORDERS", "JSON", 500, 500, 0, LocalDateTime.now().minusHours(5));
        createSampleImportExport("IMPORT", "PRODUCTS", "CSV", 75, 75, 0, LocalDateTime.now().minusDays(1));
        createSampleImportExport("EXPORT", "EMPLOYEES", "CSV", 200, 200, 0, LocalDateTime.now().minusDays(1));
        createSampleImportExport("IMPORT", "VENDORS", "XML", 30, 28, 2, LocalDateTime.now().minusDays(2));
        createSampleImportExport("EXPORT", "TRANSACTIONS", "JSON", 1200, 1200, 0, LocalDateTime.now().minusDays(3));
        createSampleImportExport("IMPORT", "LEADS", "CSV", 250, 245, 5, LocalDateTime.now().minusDays(4));

        // Sample integration logs
        createSampleLog("Salesforce CRM", "INFO", "Data sync completed: 42 records updated");
        createSampleLog("QuickBooks", "INFO", "Invoice sync completed successfully");
        createSampleLog("Stripe Payments", "INFO", "Payment webhook received: PAY-2024-0156");
        createSampleLog("Twilio SMS", "ERROR", "Authentication failed: Invalid API key");
        createSampleLog("Shopify Store", "WARN", "Connection lost, retrying in 60 seconds...");
        createSampleLog("AWS S3 Storage", "INFO", "Backup file uploaded: backup_2024_03_15.zip");
        createSampleLog("Salesforce CRM", "WARN", "Rate limit approaching: 80% of daily quota used");
        createSampleLog("QuickBooks", "ERROR", "Failed to sync invoice INV-2024-0090: duplicate entry");
        createSampleLog("SendGrid Email", "INFO", "Email notification sent to 15 recipients");
        createSampleLog("Stripe Payments", "INFO", "Refund processed: REF-2024-0023");
        createSampleLog("Twilio SMS", "ERROR", "SMS delivery failed: invalid phone number +1-555-0000");
        createSampleLog("Salesforce CRM", "INFO", "Contact sync completed: 18 new, 5 updated");
    }

    private void createSampleSync(String source, String target, String dataType, String status,
                                   LocalDateTime lastSync, int recordsSynced, int pendingChanges) {
        Map<String, Object> sync = new HashMap<>();
        sync.put("sourceModule", source);
        sync.put("targetModule", target);
        sync.put("dataType", dataType);
        sync.put("status", status);
        sync.put("lastSync", lastSync.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        sync.put("recordsSynced", recordsSynced);
        sync.put("pendingChanges", pendingChanges);
        syncConfigs.add(sync);
    }

    private void createSampleEvent(String eventType, String sourceModule, Map<String, Object> eventData, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", "EVT-" + nextEventId++);
        event.put("eventType", eventType);
        event.put("sourceModule", sourceModule);
        event.put("eventData", eventData);
        event.put("status", status);
        event.put("createdAt", LocalDateTime.now().minusHours((int)(Math.random() * 48))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        events.add(event);
    }

    private void createSampleIntegration(String name, String category, String status, String endpoint,
                                          LocalDateTime lastCheck, boolean enabled) {
        Map<String, Object> integration = new HashMap<>();
        integration.put("name", name);
        integration.put("category", category);
        integration.put("status", status);
        integration.put("endpoint", endpoint);
        integration.put("lastCheck", lastCheck.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        integration.put("enabled", enabled);
        integration.put("apiVersion", "v2");
        integration.put("rateLimitRemaining", (int)(Math.random() * 900) + 100);
        externalIntegrations.add(integration);
    }

    private void createSampleImportExport(String operation, String dataType, String format,
                                           int totalRecords, int successRecords, int errorRecords,
                                           LocalDateTime timestamp) {
        Map<String, Object> record = new HashMap<>();
        record.put("operation", operation);
        record.put("dataType", dataType);
        record.put("format", format);
        record.put("totalRecords", totalRecords);
        record.put("successRecords", successRecords);
        record.put("errorRecords", errorRecords);
        record.put("timestamp", timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        record.put("status", errorRecords > 0 ? "COMPLETED_WITH_ERRORS" : "SUCCESS");
        importExportHistory.add(record);
    }

    private void createSampleLog(String integrationName, String level, String message) {
        Map<String, Object> log = new HashMap<>();
        log.put("id", "LOG-" + nextLogId++);
        log.put("integrationName", integrationName);
        log.put("level", level);
        log.put("message", message);
        log.put("timestamp", LocalDateTime.now().minusMinutes((int)(Math.random() * 1440))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        integrationLogs.add(log);
    }

    // ==================== DATA SYNCHRONIZATION ====================

    @Override
    public int syncData(String sourceModule, String targetModule, String dataType) {
        int recordsSynced = (int)(Math.random() * 50) + 10;
        for (Map<String, Object> sync : syncConfigs) {
            if (sourceModule.equals(sync.get("sourceModule")) && targetModule.equals(sync.get("targetModule"))) {
                sync.put("status", "SYNCED");
                sync.put("lastSync", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                sync.put("recordsSynced", recordsSynced);
                sync.put("pendingChanges", 0);
                break;
            }
        }
        createSampleLog(sourceModule + " -> " + targetModule, "INFO",
                "Sync completed: " + recordsSynced + " records for " + dataType);
        return recordsSynced;
    }

    @Override
    public Map<String, Object> getSyncStatus(String sourceModule, String targetModule) {
        for (Map<String, Object> sync : syncConfigs) {
            if (sourceModule.equals(sync.get("sourceModule")) && targetModule.equals(sync.get("targetModule"))) {
                return new HashMap<>(sync);
            }
        }
        Map<String, Object> defaultStatus = new HashMap<>();
        defaultStatus.put("status", "NOT_CONFIGURED");
        defaultStatus.put("lastSync", "Never");
        defaultStatus.put("pendingChanges", 0);
        return defaultStatus;
    }

    @Override
    public boolean forceResync(String sourceModule, String targetModule) {
        for (Map<String, Object> sync : syncConfigs) {
            if (sourceModule.equals(sync.get("sourceModule")) && targetModule.equals(sync.get("targetModule"))) {
                sync.put("status", "SYNCED");
                sync.put("lastSync", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                sync.put("pendingChanges", 0);
                sync.put("recordsSynced", (int)(Math.random() * 100) + 20);
                createSampleLog(sourceModule + " -> " + targetModule, "INFO", "Force resync completed");
                return true;
            }
        }
        return false;
    }

    // ==================== EVENT/MESSAGE HANDLING ====================

    @Override
    public boolean publishEvent(String eventType, Map<String, Object> eventData) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", "EVT-" + nextEventId++);
        event.put("eventType", eventType);
        event.put("sourceModule", "Manual");
        event.put("eventData", eventData);
        event.put("status", "PENDING");
        event.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        events.add(event);
        return true;
    }

    @Override
    public List<Map<String, Object>> getPendingEvents(String moduleName) {
        return events.stream()
                .filter(e -> "PENDING".equals(e.get("status")))
                .collect(Collectors.toList());
    }

    @Override
    public boolean acknowledgeEvent(String eventId, boolean success) {
        for (Map<String, Object> event : events) {
            if (eventId.equals(event.get("id"))) {
                event.put("status", success ? "DELIVERED" : "FAILED");
                return true;
            }
        }
        return false;
    }

    // ==================== EXTERNAL API INTEGRATION ====================

    @Override
    public List<Map<String, Object>> getExternalIntegrations() {
        return new ArrayList<>(externalIntegrations);
    }

    @Override
    public Map<String, Object> testConnection(String integrationName) {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> integration : externalIntegrations) {
            if (integrationName.equals(integration.get("name"))) {
                boolean success = !"DISCONNECTED".equals(integration.get("status"));
                result.put("success", success);
                result.put("responseTime", (int)(Math.random() * 500) + 50 + "ms");
                result.put("message", success ? "Connection successful" : "Connection refused");
                if (success) {
                    integration.put("status", "CONNECTED");
                    integration.put("lastCheck", LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
                return result;
            }
        }
        result.put("success", false);
        result.put("message", "Integration not found");
        return result;
    }

    @Override
    public Map<String, Object> getIntegrationConfig(String integrationName) {
        for (Map<String, Object> integration : externalIntegrations) {
            if (integrationName.equals(integration.get("name"))) {
                return new HashMap<>(integration);
            }
        }
        return null;
    }

    @Override
    public boolean updateIntegrationConfig(String integrationName, Map<String, Object> config) {
        for (int i = 0; i < externalIntegrations.size(); i++) {
            if (integrationName.equals(externalIntegrations.get(i).get("name"))) {
                config.put("name", integrationName);
                config.put("lastCheck", externalIntegrations.get(i).get("lastCheck"));
                externalIntegrations.set(i, config);
                createSampleLog(integrationName, "INFO", "Configuration updated");
                return true;
            }
        }
        return false;
    }

    // ==================== DATA IMPORT/EXPORT ====================

    @Override
    public Map<String, Object> importData(String dataType, String format, String data) {
        int total = (int)(Math.random() * 100) + 10;
        int errors = (int)(Math.random() * 5);
        Map<String, Object> result = new HashMap<>();
        result.put("recordsImported", total - errors);
        result.put("errors", errors);
        result.put("warnings", 0);

        createSampleImportExport("IMPORT", dataType, format, total, total - errors, errors, LocalDateTime.now());
        createSampleLog("Import", "INFO", "Imported " + (total - errors) + " " + dataType + " records from " + format);
        return result;
    }

    @Override
    public String exportData(String dataType, String format, Map<String, Object> filters) {
        int total = (int)(Math.random() * 200) + 50;
        createSampleImportExport("EXPORT", dataType, format, total, total, 0, LocalDateTime.now());
        createSampleLog("Export", "INFO", "Exported " + total + " " + dataType + " records to " + format);
        return "Export completed: " + total + " records";
    }

    @Override
    public List<Map<String, Object>> getImportExportHistory(int limit) {
        return importExportHistory.stream()
                .sorted((a, b) -> ((String) b.get("timestamp")).compareTo((String) a.get("timestamp")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==================== HEALTH & MONITORING ====================

    @Override
    public Map<String, String> getIntegrationHealth() {
        Map<String, String> health = new LinkedHashMap<>();
        for (Map<String, Object> integration : externalIntegrations) {
            health.put((String) integration.get("name"), (String) integration.get("status"));
        }
        return health;
    }

    @Override
    public List<Map<String, Object>> getIntegrationLogs(String integrationName, int limit) {
        return integrationLogs.stream()
                .filter(l -> integrationName == null || integrationName.equals(l.get("integrationName")))
                .sorted((a, b) -> ((String) b.get("timestamp")).compareTo((String) a.get("timestamp")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==================== ADDITIONAL HELPERS (for UI) ====================

    /**
     * Get all sync configurations.
     * @return List of all sync configs
     */
    public List<Map<String, Object>> getAllSyncConfigs() {
        return new ArrayList<>(syncConfigs);
    }

    /**
     * Get all events (not just pending).
     * @return List of all events
     */
    public List<Map<String, Object>> getAllEvents() {
        return new ArrayList<>(events);
    }

    /**
     * Get sync count by status.
     * @return Map of status to count
     */
    public Map<String, Integer> getSyncCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("SYNCED", 0);
        result.put("PENDING", 0);
        result.put("ERROR", 0);
        for (Map<String, Object> sync : syncConfigs) {
            String status = (String) sync.get("status");
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    /**
     * Get integration count by status.
     * @return Map of status to count
     */
    public Map<String, Integer> getIntegrationCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("CONNECTED", 0);
        result.put("DISCONNECTED", 0);
        result.put("ERROR", 0);
        for (Map<String, Object> integration : externalIntegrations) {
            String status = (String) integration.get("status");
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    /**
     * Get event count by status.
     * @return Map of status to count
     */
    public Map<String, Integer> getEventCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("PENDING", 0);
        result.put("DELIVERED", 0);
        result.put("FAILED", 0);
        for (Map<String, Object> event : events) {
            String status = (String) event.get("status");
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }
}
