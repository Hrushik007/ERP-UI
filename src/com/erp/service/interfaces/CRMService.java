package com.erp.service.interfaces;

import com.erp.model.*;

import java.math.BigDecimal;
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


    // ==================== LEAD MANAGEMENT ====================

    /**
     * Get all leads.
     * @return List of all leads
     */
    List<Lead> getAllLeads();

    /**
     * Get leads by status (NEW, CONTACTED, QUALIFIED, UNQUALIFIED, CONVERTED).
     * @param status The lead status
     * @return List of leads with that status
     */
    List<Lead> getLeadsByStatus(String status);

    /**
     * Get a lead by ID.
     * @param leadId The lead ID
     * @return Lead object or null if not found
     */
    Lead getLeadById(int leadId);

    /**
     * Search leads by company name, contact name, or email.
     * @param searchTerm The search string
     * @return List of matching leads
     */
    List<Lead> searchLeads(String searchTerm);

    /**
     * Get leads assigned to a sales rep.
     * @param salesRepId The sales rep's employee ID
     * @return List of assigned leads
     */
    List<Lead> getLeadsBySalesRep(int salesRepId);

    /**
     * Create a new lead.
     * @param lead The lead data
     * @return Created lead with ID
     */
    Lead createLead(Lead lead);

    /**
     * Update an existing lead.
     * @param lead The lead to update
     * @return true if successful
     */
    boolean updateLead(Lead lead);

    /**
     * Delete a lead.
     * @param leadId The lead ID
     * @return true if successful
     */
    boolean deleteLead(int leadId);

    /**
     * Assign a lead to a sales rep.
     * @param leadId The lead ID
     * @param salesRepId The sales rep's employee ID
     * @return true if successful
     */
    boolean assignLead(int leadId, int salesRepId);

    /**
     * Convert a lead to opportunity and contact.
     * @param leadId The lead ID
     * @return The created opportunity, or null if conversion failed
     */
    Opportunity convertLead(int leadId);


    // ==================== OPPORTUNITY MANAGEMENT ====================

    /**
     * Get all opportunities.
     * @return List of all opportunities
     */
    List<Opportunity> getAllOpportunities();

    /**
     * Get opportunities by stage.
     * @param stage The opportunity stage
     * @return List of opportunities at that stage
     */
    List<Opportunity> getOpportunitiesByStage(String stage);

    /**
     * Get an opportunity by ID.
     * @param opportunityId The opportunity ID
     * @return Opportunity object or null if not found
     */
    Opportunity getOpportunityById(int opportunityId);

    /**
     * Get opportunities assigned to a sales rep.
     * @param salesRepId The sales rep's employee ID
     * @return List of assigned opportunities
     */
    List<Opportunity> getOpportunitiesBySalesRep(int salesRepId);

    /**
     * Get opportunities for a customer.
     * @param customerId The customer ID
     * @return List of opportunities
     */
    List<Opportunity> getOpportunitiesByCustomer(int customerId);

    /**
     * Create a new opportunity.
     * @param opportunity The opportunity data
     * @return Created opportunity with ID
     */
    Opportunity createOpportunity(Opportunity opportunity);

    /**
     * Update an existing opportunity.
     * @param opportunity The opportunity to update
     * @return true if successful
     */
    boolean updateOpportunity(Opportunity opportunity);

    /**
     * Delete an opportunity.
     * @param opportunityId The opportunity ID
     * @return true if successful
     */
    boolean deleteOpportunity(int opportunityId);

    /**
     * Move opportunity to next stage.
     * @param opportunityId The opportunity ID
     * @return true if successful
     */
    boolean advanceOpportunityStage(int opportunityId);

    /**
     * Close opportunity as won.
     * @param opportunityId The opportunity ID
     * @param actualValue The actual closed value
     * @return true if successful
     */
    boolean closeOpportunityWon(int opportunityId, BigDecimal actualValue);

    /**
     * Close opportunity as lost.
     * @param opportunityId The opportunity ID
     * @param lostReason The reason for losing
     * @return true if successful
     */
    boolean closeOpportunityLost(int opportunityId, String lostReason);


    // ==================== CONTACT MANAGEMENT ====================

    /**
     * Get all contacts.
     * @return List of all contacts
     */
    List<Contact> getAllContacts();

    /**
     * Get a contact by ID.
     * @param contactId The contact ID
     * @return Contact object or null if not found
     */
    Contact getContactById(int contactId);

    /**
     * Get contacts for a customer.
     * @param customerId The customer ID
     * @return List of contacts
     */
    List<Contact> getContactsByCustomer(int customerId);

    /**
     * Search contacts by name or email.
     * @param searchTerm The search string
     * @return List of matching contacts
     */
    List<Contact> searchContacts(String searchTerm);

    /**
     * Create a new contact.
     * @param contact The contact data
     * @return Created contact with ID
     */
    Contact createContact(Contact contact);

    /**
     * Update an existing contact.
     * @param contact The contact to update
     * @return true if successful
     */
    boolean updateContact(Contact contact);

    /**
     * Delete a contact.
     * @param contactId The contact ID
     * @return true if successful
     */
    boolean deleteContact(int contactId);


    // ==================== ACTIVITY MANAGEMENT ====================

    /**
     * Get all activities.
     * @return List of all activities
     */
    List<Activity> getAllActivities();

    /**
     * Get an activity by ID.
     * @param activityId The activity ID
     * @return Activity object or null if not found
     */
    Activity getActivityById(int activityId);

    /**
     * Get activities by type (CALL, MEETING, EMAIL, TASK, NOTE).
     * @param type The activity type
     * @return List of activities of that type
     */
    List<Activity> getActivitiesByType(String type);

    /**
     * Get activities for a related entity (lead, contact, opportunity, customer).
     * @param relatedType The entity type (LEAD, CONTACT, OPPORTUNITY, CUSTOMER)
     * @param relatedId The entity ID
     * @return List of activities
     */
    List<Activity> getActivitiesByRelated(String relatedType, int relatedId);

    /**
     * Get activities assigned to an employee.
     * @param employeeId The employee ID
     * @return List of assigned activities
     */
    List<Activity> getActivitiesByAssignee(int employeeId);

    /**
     * Get overdue activities.
     * @return List of overdue activities
     */
    List<Activity> getOverdueActivities();

    /**
     * Get upcoming activities (due within X days).
     * @param days Number of days
     * @return List of upcoming activities
     */
    List<Activity> getUpcomingActivities(int days);

    /**
     * Create a new activity.
     * @param activity The activity data
     * @return Created activity with ID
     */
    Activity createActivity(Activity activity);

    /**
     * Update an existing activity.
     * @param activity The activity to update
     * @return true if successful
     */
    boolean updateActivity(Activity activity);

    /**
     * Delete an activity.
     * @param activityId The activity ID
     * @return true if successful
     */
    boolean deleteActivity(int activityId);

    /**
     * Mark an activity as completed.
     * @param activityId The activity ID
     * @param outcome The outcome/result of the activity
     * @return true if successful
     */
    boolean completeActivity(int activityId, String outcome);


    // ==================== EXTENDED ANALYTICS ====================

    /**
     * Get lead count by status.
     * @return Map of status to count
     */
    Map<String, Integer> getLeadCountByStatus();

    /**
     * Get opportunity count by stage.
     * @return Map of stage to count
     */
    Map<String, Integer> getOpportunityCountByStage();

    /**
     * Get total pipeline value (sum of all open opportunities).
     * @return Total pipeline value
     */
    BigDecimal getTotalPipelineValue();

    /**
     * Get pipeline value by stage.
     * @return Map of stage to total value
     */
    Map<String, BigDecimal> getPipelineValueByStage();

    /**
     * Get activity count by type.
     * @return Map of type to count
     */
    Map<String, Integer> getActivityCountByType();
}
