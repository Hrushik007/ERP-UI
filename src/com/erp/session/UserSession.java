package com.erp.session;

/**
 * Singleton session holder. Created on successful login.
 * Contains the identity (userId, role) and validity flag.
 *
 * Roles: ADMIN, MANAGER, EMPLOYEE, HR, SALES.
 */
public final class UserSession {

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_EMPLOYEE = "Employee";
    public static final String ROLE_HR = "HR";
    public static final String ROLE_SALES = "Sales";

    private static UserSession instance;

    private String userId;
    private String displayName;
    private String role;
    private boolean valid;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void begin(String userId, String displayName, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
        this.valid = true;
    }

    public void end() {
        this.userId = null;
        this.displayName = null;
        this.role = null;
        this.valid = false;
    }

    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public boolean isValid() { return valid; }
}
