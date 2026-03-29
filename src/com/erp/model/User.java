package com.erp.model;

/**
 * User model class representing a system user.
 *
 * This class demonstrates key OOP concepts:
 *
 * 1. ENCAPSULATION: All fields are private, accessed only through getters/setters.
 *    This protects the internal state and allows validation in setters.
 *
 * 2. Data classes in Java typically have:
 *    - Private fields
 *    - Constructor(s)
 *    - Getters and setters
 *    - toString() for debugging
 *    - equals() and hashCode() for comparison (can be added later)
 */
public class User {

    // Private fields - encapsulation
    private int id;
    private String username;
    private String password;  
    private String fullName;
    private String email;
    private String role;      // e.g., "Admin", "Manager", "Employee"
    private boolean active;

    /**
     * Default constructor - creates an empty User object.
     * Useful for frameworks and when you want to set properties individually.
     */
    public User() {
        this.active = true;
    }

    /**
     * Parameterized constructor - creates a fully initialized User.
     * This is an example of constructor overloading (method overloading applied to constructors).
     *
     * @param id       Unique identifier
     * @param username Login username
     * @param password Login password
     * @param fullName User's full name
     * @param email    User's email
     * @param role     User's role in the system
     */
    public User(int id, String username, String password, String fullName, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = true;
    }

    // ==================== GETTERS AND SETTERS ====================
    // These provide controlled access to private fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // In a real application, you would hash the password here
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if the user has admin privileges.
     * This is a business logic method that belongs in the model.
     * @return true if user is an admin
     */
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(this.role);
    }

    /**
     * Checks if the user has manager privileges.
     * @return true if user is a manager or admin
     */
    public boolean isManager() {
        return "Manager".equalsIgnoreCase(this.role) || isAdmin();
    }

    /**
     * toString() provides a string representation of the object.
     * Useful for debugging and logging.
     * Note: Password is intentionally excluded for security.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
