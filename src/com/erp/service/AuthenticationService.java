package com.erp.service;

import com.erp.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationService handles user authentication.
 *
 * This class demonstrates the SINGLETON DESIGN PATTERN:
 * - Only one instance exists throughout the application
 * - Provides a global point of access
 * - Lazy initialization (created only when first needed)
 *
 * Why Singleton here?
 * - We need only one authentication service in the app
 * - It maintains state (logged-in user, user database)
 * - Multiple instances would cause inconsistency
 *
 * The service layer sits between controllers and data (model).
 * It contains business logic and data access operations.
 */
public class AuthenticationService {

    // The single instance (static - belongs to class, not objects)
    private static AuthenticationService instance;

    // Simulated user database (In real app, this would connect to a database)
    private Map<String, User> userDatabase;

    // Currently logged in user
    private User currentUser;

    /**
     * Private constructor - prevents external instantiation.
     * This is KEY to the Singleton pattern.
     */
    private AuthenticationService() {
        userDatabase = new HashMap<>();
        initializeSampleUsers();
    }

    /**
     * Gets the singleton instance.
     * Uses lazy initialization - instance created on first call.
     *
     * The 'synchronized' keyword ensures thread safety:
     * If two threads call this simultaneously, only one can execute at a time.
     *
     * @return The single AuthenticationService instance
     */
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Initialize sample users for testing.
     * In a real application, users would come from a database.
     */
    private void initializeSampleUsers() {
        // Create sample users with different roles
        User admin = new User(1, "admin", "admin123", "System Administrator",
                             "admin@erp.com", "Admin");
        User manager = new User(2, "manager", "manager123", "John Manager",
                               "manager@erp.com", "Manager");
        User employee = new User(3, "employee", "emp123", "Jane Employee",
                                "employee@erp.com", "Employee");

        // Add to our simulated database
        userDatabase.put(admin.getUsername(), admin);
        userDatabase.put(manager.getUsername(), manager);
        userDatabase.put(employee.getUsername(), employee);
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        // Input validation
        if (username == null || password == null) {
            return false;
        }

        // Check if user exists
        User user = userDatabase.get(username);
        if (user == null) {
            return false;
        }

        // Verify password and check if user is active
        if (user.getPassword().equals(password) && user.isActive()) {
            currentUser = user;
            return true;
        }

        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the currently logged-in user.
     * @return The current User, or null if no one is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if someone is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user has a specific role.
     * @param role The role to check
     * @return true if the current user has the specified role
     */
    public boolean hasRole(String role) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase(role);
    }

    /**
     * Checks if the current user is an administrator.
     * @return true if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
