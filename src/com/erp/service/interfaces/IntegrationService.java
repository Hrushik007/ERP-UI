package com.erp.service.interfaces;

import java.util.List;
import java.util.Map;

/**
 * IntegrationService Interface - CONTRACT for Integration Subsystem Backend Team
 *
 * Covers: Inter-Module Connectivity, API Integration, Data Synchronization
 *
 * This service handles communication between modules and external systems.
 */
public interface IntegrationService {

    // ==================== DATA SYNCHRONIZATION ====================

    /**
     * Sync data between modules.
     * @param sourceModule Source module name
     * @param targetModule Target module name
     * @param dataType Type of data to sync
     * @return Number of records synced
     */
    int syncData(String sourceModule, String targetModule, String dataType);

    /**
     * Get sync status between modules.
     * @param sourceModule Source module
     * @param targetModule Target module
     * @return Map with lastSync, status, pendingChanges
     */
    Map<String, Object> getSyncStatus(String sourceModule, String targetModule);

    /**
     * Force full resync between modules.
     * @param sourceModule Source module
     * @param targetModule Target module
     * @return true if resync initiated
     */
    boolean forceResync(String sourceModule, String targetModule);


    // ==================== EVENT/MESSAGE HANDLING ====================

    /**
     * Publish an event to other modules.
     * @param eventType Type of event (ORDER_CREATED, PAYMENT_RECEIVED, etc.)
     * @param eventData Data associated with the event
     * @return true if published successfully
     */
    boolean publishEvent(String eventType, Map<String, Object> eventData);

    /**
     * Get pending events for a module.
     * @param moduleName The module name
     * @return List of pending events
     */
    List<Map<String, Object>> getPendingEvents(String moduleName);

    /**
     * Acknowledge event processing.
     * @param eventId The event ID
     * @param success Whether processing was successful
     * @return true if acknowledged
     */
    boolean acknowledgeEvent(String eventId, boolean success);


    // ==================== EXTERNAL API INTEGRATION ====================

    /**
     * Get available external integrations.
     * @return List of integration names and status
     */
    List<Map<String, Object>> getExternalIntegrations();

    /**
     * Test connection to external system.
     * @param integrationName Name of the integration
     * @return Map with success status and details
     */
    Map<String, Object> testConnection(String integrationName);

    /**
     * Get integration configuration.
     * @param integrationName Name of the integration
     * @return Configuration map
     */
    Map<String, Object> getIntegrationConfig(String integrationName);

    /**
     * Update integration configuration.
     * @param integrationName Name of the integration
     * @param config New configuration
     * @return true if updated
     */
    boolean updateIntegrationConfig(String integrationName, Map<String, Object> config);


    // ==================== DATA IMPORT/EXPORT ====================

    /**
     * Import data from external source.
     * @param dataType Type of data (CUSTOMERS, PRODUCTS, ORDERS, etc.)
     * @param format Data format (CSV, JSON, XML)
     * @param data The data to import
     * @return Map with recordsImported, errors, warnings
     */
    Map<String, Object> importData(String dataType, String format, String data);

    /**
     * Export data to external format.
     * @param dataType Type of data to export
     * @param format Output format
     * @param filters Optional filters
     * @return Exported data as string
     */
    String exportData(String dataType, String format, Map<String, Object> filters);

    /**
     * Get import/export history.
     * @param limit Number of records to return
     * @return List of import/export operations
     */
    List<Map<String, Object>> getImportExportHistory(int limit);


    // ==================== HEALTH & MONITORING ====================

    /**
     * Get integration health status.
     * @return Map of integration name to health status
     */
    Map<String, String> getIntegrationHealth();

    /**
     * Get integration logs.
     * @param integrationName Name of integration (null for all)
     * @param limit Number of log entries
     * @return List of log entries
     */
    List<Map<String, Object>> getIntegrationLogs(String integrationName, int limit);
}
