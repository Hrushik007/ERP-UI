package com.erp.model;

import java.time.LocalDateTime;

/**
 * Warehouse entity representing a storage location for inventory.
 *
 * Used by:
 * - Inventory Module
 * - Order Processing (fulfillment)
 * - Manufacturing (raw materials)
 */
public class Warehouse {

    private int warehouseId;
    private String code;           // e.g., "WH-001", "MAIN"
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String managerName;
    private String phone;
    private String email;
    private String type;           // MAIN, DISTRIBUTION, RETAIL, RETURNS
    private int capacity;          // Total capacity in units
    private int usedCapacity;      // Current used capacity
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Warehouse() {
        this.active = true;
        this.capacity = 10000;
        this.usedCapacity = 0;
    }

    public Warehouse(int warehouseId, String code, String name) {
        this();
        this.warehouseId = warehouseId;
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getUsedCapacity() { return usedCapacity; }
    public void setUsedCapacity(int usedCapacity) { this.usedCapacity = usedCapacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getAvailableCapacity() {
        return capacity - usedCapacity;
    }

    public double getUtilizationPercent() {
        if (capacity == 0) return 0;
        return (usedCapacity * 100.0) / capacity;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
