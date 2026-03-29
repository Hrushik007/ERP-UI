package com.erp.service.interfaces;

import com.erp.model.Customer;
import com.erp.model.SupportTicket;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * CRMService Interface - CONTRACT for CRM Module Backend Team
 *
 * This interface defines ALL operations that the UI expects from the CRM backend.
 * Covers: Customer Management, Sales Force Automation, Customer Service & Support
 *
 * IMPORTANT FOR BACKEND TEAM:
 * - Implement all methods in a class called CRMServiceImpl
 * - Return empty lists instead of null when no data found
 * - Throw appropriate exceptions for error conditions
 */
public interface CRMService {

    // ==================== CUSTOMER MANAGEMENT ====================

    /**
     * Get all customers.
     * @return List of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Get customers by type (LEAD, PROSPECT, CUSTOMER, VIP).
     * @param customerType The customer type
     * @return List of customers of that type
     */
    List<Customer> getCustomersByType(String customerType);

    /**
     * Get a customer by ID.
     * @param customerId The customer ID
     * @return Customer object or null if not found
     */
    Customer getCustomerById(int customerId);

    /**
     * Search customers by company name or contact name.
     * @param searchTerm The search string
     * @return List of matching customers
     */
    List<Customer> searchCustomers(String searchTerm);

    /**
     * Get customers assigned to a sales rep.
     * @param salesRepId The sales rep's employee ID
     * @return List of assigned customers
     */
    List<Customer> getCustomersBySalesRep(int salesRepId);

    /**
     * Create a new customer.
     * @param customer The customer data
     * @return Created customer with ID
     */
    Customer createCustomer(Customer customer);

    /**
     * Update an existing customer.
     * @param customer The customer to update
     * @return true if successful
     */
    boolean updateCustomer(Customer customer);

    /**
     * Delete a customer.
     * @param customerId The customer ID
     * @return true if successful
     */
    boolean deleteCustomer(int customerId);

    /**
     * Convert a lead to a customer.
     * @param customerId The lead's ID
     * @return true if conversion successful
     */
    boolean convertLeadToCustomer(int customerId);

    /**
     * Assign a customer to a sales rep.
     * @param customerId The customer ID
     * @param salesRepId The sales rep's employee ID
     * @return true if successful
     */
    boolean assignCustomerToSalesRep(int customerId, int salesRepId);


    // ==================== CUSTOMER SERVICE & SUPPORT ====================

    /**
     * Create a new support ticket.
     * @param ticket The ticket data
     * @return Created ticket with ID
     */
    SupportTicket createTicket(SupportTicket ticket);

    /**
     * Get a ticket by ID.
     * @param ticketId The ticket ID
     * @return Ticket object or null
     */
    SupportTicket getTicketById(int ticketId);

    /**
     * Get all tickets for a customer.
     * @param customerId The customer ID
     * @return List of tickets
     */
    List<SupportTicket> getTicketsByCustomer(int customerId);

    /**
     * Get tickets by status.
     * @param status OPEN, IN_PROGRESS, RESOLVED, CLOSED
     * @return List of tickets
     */
    List<SupportTicket> getTicketsByStatus(String status);

    /**
     * Get tickets assigned to a support agent.
     * @param agentId The agent's employee ID
     * @return List of tickets
     */
    List<SupportTicket> getTicketsByAgent(int agentId);

    /**
     * Get unassigned tickets (for queue management).
     * @return List of unassigned tickets
     */
    List<SupportTicket> getUnassignedTickets();

    /**
     * Update a ticket.
     * @param ticket The ticket to update
     * @return true if successful
     */
    boolean updateTicket(SupportTicket ticket);

    /**
     * Assign a ticket to an agent.
     * @param ticketId The ticket ID
     * @param agentId The agent's employee ID
     * @return true if successful
     */
    boolean assignTicket(int ticketId, int agentId);

    /**
     * Resolve a ticket.
     * @param ticketId The ticket ID
     * @param resolution The resolution description
     * @return true if successful
     */
    boolean resolveTicket(int ticketId, String resolution);

    /**
     * Close a ticket.
     * @param ticketId The ticket ID
     * @return true if successful
     */
    boolean closeTicket(int ticketId);


    // ==================== ANALYTICS & REPORTING ====================

    /**
     * Get customer count by type.
     * @return Map of customer type to count
     */
    Map<String, Integer> getCustomerCountByType();

    /**
     * Get ticket count by status.
     * @return Map of status to count
     */
    Map<String, Integer> getTicketCountByStatus();

    /**
     * Get customers needing follow-up (no contact in X days).
     * @param daysSinceContact Number of days
     * @return List of customers needing follow-up
     */
    List<Customer> getCustomersNeedingFollowUp(int daysSinceContact);

    /**
     * Get average ticket resolution time (in hours).
     * @param startDate Start of period
     * @param endDate End of period
     * @return Average hours to resolve
     */
    double getAverageResolutionTime(LocalDate startDate, LocalDate endDate);
}
