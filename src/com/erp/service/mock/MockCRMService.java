package com.erp.service.mock;

import com.erp.model.*;
import com.erp.service.interfaces.CRMService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockCRMService provides sample data for CRM Module UI.
 */
public class MockCRMService implements CRMService {

    private Map<Integer, Customer> customers;
    private Map<Integer, SupportTicket> tickets;
    private Map<Integer, Lead> leads;
    private Map<Integer, Opportunity> opportunities;
    private Map<Integer, Contact> contacts;
    private Map<Integer, Activity> activities;

    private int nextCustomerId = 1;
    private int nextTicketId = 1;
    private int nextLeadId = 1;
    private int nextOpportunityId = 1;
    private int nextContactId = 1;
    private int nextActivityId = 1;

    private static MockCRMService instance;

    public static synchronized MockCRMService getInstance() {
        if (instance == null) {
            instance = new MockCRMService();
        }
        return instance;
    }

    private MockCRMService() {
        customers = new HashMap<>();
        tickets = new HashMap<>();
        leads = new HashMap<>();
        opportunities = new HashMap<>();
        contacts = new HashMap<>();
        activities = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create customers
        createSampleCustomer("Acme Corporation", "John Smith", "john@acme.com", "(555) 123-4567", "CUSTOMER");
        createSampleCustomer("TechStart Inc", "Sarah Johnson", "sarah@techstart.io", "(555) 234-5678", "CUSTOMER");
        createSampleCustomer("Global Industries", "Mike Brown", "mike@global.com", "(555) 345-6789", "VIP");
        createSampleCustomer("Local Shop LLC", "Emily Davis", "emily@localshop.com", "(555) 456-7890", "CUSTOMER");

        // Create leads
        createSampleLead("Future Tech Co", "Alex Wilson", "alex@futuretech.com", "(555) 567-8901", "NEW", "WEBSITE");
        createSampleLead("Startup Labs", "Chris Lee", "chris@startuplabs.io", "(555) 678-9012", "CONTACTED", "REFERRAL");
        createSampleLead("Enterprise Solutions", "Pat Taylor", "pat@enterprise.com", "(555) 789-0123", "QUALIFIED", "TRADE_SHOW");
        createSampleLead("Small Biz Inc", "Jordan Martinez", "jordan@smallbiz.com", "(555) 890-1234", "NEW", "COLD_CALL");
        createSampleLead("Growth Corp", "Sam Anderson", "sam@growthcorp.com", "(555) 901-2345", "CONTACTED", "WEBSITE");

        // Create contacts for customers
        for (Customer c : customers.values()) {
            createSampleContact(c.getCustomerId(), c.getContactName(), c.getEmail(), c.getPhone(), "Decision Maker", true);
        }

        // Create opportunities
        createSampleOpportunity("Acme Software Upgrade", 1, new BigDecimal("50000"), "PROPOSAL", 60);
        createSampleOpportunity("TechStart Annual Contract", 2, new BigDecimal("25000"), "NEGOTIATION", 80);
        createSampleOpportunity("Global Enterprise Deal", 3, new BigDecimal("150000"), "QUALIFICATION", 40);
        createSampleOpportunity("Local Shop POS System", 4, new BigDecimal("15000"), "PROSPECTING", 20);

        // Create activities
        createSampleActivity("CALL", "Follow-up call with Acme", "CUSTOMER", 1, "COMPLETED");
        createSampleActivity("MEETING", "Demo presentation for TechStart", "CUSTOMER", 2, "PLANNED");
        createSampleActivity("EMAIL", "Send proposal to Global", "OPPORTUNITY", 3, "COMPLETED");
        createSampleActivity("TASK", "Prepare quote for Local Shop", "OPPORTUNITY", 4, "IN_PROGRESS");
        createSampleActivity("CALL", "Initial contact with Future Tech", "LEAD", 1, "PLANNED");

        // Create support tickets
        createSampleTicket(1, "Login issues", "Cannot login to the system", "HIGH", "OPEN");
        createSampleTicket(2, "Feature request", "Need export to PDF", "MEDIUM", "IN_PROGRESS");
        createSampleTicket(3, "Billing question", "Invoice clarification needed", "LOW", "RESOLVED");
    }

    private void createSampleCustomer(String company, String contact, String email, String phone, String type) {
        Customer c = new Customer();
        c.setCustomerId(nextCustomerId++);
        c.setCompanyName(company);
        c.setContactName(contact);
        c.setEmail(email);
        c.setPhone(phone);
        c.setCustomerType(type);
        c.setActive(true);
        c.setCreatedAt(LocalDate.now().minusMonths((int)(Math.random() * 12)));
        customers.put(c.getCustomerId(), c);
    }

    private void createSampleLead(String company, String contact, String email, String phone, String status, String source) {
        Lead l = new Lead();
        l.setLeadId(nextLeadId++);
        l.setCompanyName(company);
        l.setContactName(contact);
        l.setEmail(email);
        l.setPhone(phone);
        l.setStatus(status);
        l.setSource(source);
        l.setAssignedTo(1); // Default sales rep
        l.setAssignedToName("Sales Rep");
        l.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 30)));
        leads.put(l.getLeadId(), l);
    }

    private void createSampleContact(int customerId, String name, String email, String phone, String title, boolean isPrimary) {
        Contact c = new Contact();
        c.setContactId(nextContactId++);
        c.setCustomerId(customerId);
        String[] nameParts = name.split(" ");
        c.setFirstName(nameParts[0]);
        c.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        c.setEmail(email);
        c.setPhone(phone);
        c.setTitle(title);
        c.setPrimary(isPrimary);
        c.setActive(true);
        c.setCreatedAt(LocalDateTime.now().minusMonths((int)(Math.random() * 6)));
        contacts.put(c.getContactId(), c);
    }

    private void createSampleOpportunity(String name, int customerId, BigDecimal value, String stage, int probability) {
        Opportunity o = new Opportunity();
        o.setOpportunityId(nextOpportunityId++);
        o.setName(name);
        o.setCustomerId(customerId);
        Customer c = customers.get(customerId);
        if (c != null) {
            o.setCustomerName(c.getCompanyName());
        }
        o.setEstimatedValue(value);
        o.setStage(stage);
        o.setProbability(probability);
        o.setExpectedCloseDate(LocalDate.now().plusDays(30 + (int)(Math.random() * 60)));
        o.setAssignedTo(1);
        o.setAssignedToName("Sales Rep");
        o.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 45)));
        opportunities.put(o.getOpportunityId(), o);
    }

    private void createSampleActivity(String type, String subject, String relatedType, int relatedId, String status) {
        Activity a = new Activity();
        a.setActivityId(nextActivityId++);
        a.setType(type);
        a.setSubject(subject);
        a.setRelatedType(relatedType);
        a.setRelatedId(relatedId);
        a.setStatus(status);
        a.setDueDate(LocalDateTime.now().plusDays((int)(Math.random() * 14) - 7));
        a.setAssignedTo(1);
        a.setAssignedToName("Sales Rep");
        a.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 10)));
        activities.put(a.getActivityId(), a);
    }

    private void createSampleTicket(int customerId, String subject, String description, String priority, String status) {
        SupportTicket t = new SupportTicket();
        t.setTicketId(nextTicketId++);
        t.setTicketNumber("TKT-" + String.format("%05d", t.getTicketId()));
        t.setCustomerId(customerId);
        t.setSubject(subject);
        t.setDescription(description);
        t.setPriority(priority);
        t.setStatus(status);
        t.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 14)));
        tickets.put(t.getTicketId(), t);
    }

    // ==================== CUSTOMER MANAGEMENT ====================

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<Customer> getCustomersByType(String customerType) {
        return customers.values().stream()
                .filter(c -> customerType.equals(c.getCustomerType()))
                .collect(Collectors.toList());
    }

    @Override
    public Customer getCustomerById(int customerId) {
        return customers.get(customerId);
    }

    @Override
    public List<Customer> searchCustomers(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return customers.values().stream()
                .filter(c -> c.getCompanyName().toLowerCase().contains(term) ||
                            c.getContactName().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> getCustomersBySalesRep(int salesRepId) {
        return customers.values().stream()
                .filter(c -> c.getAssignedSalesRepId() == salesRepId)
                .collect(Collectors.toList());
    }

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCustomerId(nextCustomerId++);
        customer.setCreatedAt(LocalDate.now());
        customers.put(customer.getCustomerId(), customer);
        return customer;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customers.containsKey(customer.getCustomerId())) {
            customers.put(customer.getCustomerId(), customer);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCustomer(int customerId) {
        return customers.remove(customerId) != null;
    }

    @Override
    public boolean convertLeadToCustomer(int customerId) {
        Customer c = customers.get(customerId);
        if (c != null && "LEAD".equals(c.getCustomerType())) {
            c.setCustomerType("CUSTOMER");
            return true;
        }
        return false;
    }

    @Override
    public boolean assignCustomerToSalesRep(int customerId, int salesRepId) {
        Customer c = customers.get(customerId);
        if (c != null) {
            c.setAssignedSalesRepId(salesRepId);
            return true;
        }
        return false;
    }

    // ==================== SUPPORT TICKETS ====================

    @Override
    public SupportTicket createTicket(SupportTicket ticket) {
        ticket.setTicketId(nextTicketId++);
        ticket.setTicketNumber("TKT-" + String.format("%05d", ticket.getTicketId()));
        ticket.setCreatedAt(LocalDateTime.now());
        tickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    @Override
    public SupportTicket getTicketById(int ticketId) {
        return tickets.get(ticketId);
    }

    @Override
    public List<SupportTicket> getTicketsByCustomer(int customerId) {
        return tickets.values().stream()
                .filter(t -> t.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicket> getTicketsByStatus(String status) {
        return tickets.values().stream()
                .filter(t -> status.equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicket> getTicketsByAgent(int agentId) {
        return tickets.values().stream()
                .filter(t -> t.getAssignedToId() == agentId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicket> getUnassignedTickets() {
        return tickets.values().stream()
                .filter(t -> t.getAssignedToId() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateTicket(SupportTicket ticket) {
        if (tickets.containsKey(ticket.getTicketId())) {
            ticket.setUpdatedAt(LocalDateTime.now());
            tickets.put(ticket.getTicketId(), ticket);
            return true;
        }
        return false;
    }

    @Override
    public boolean assignTicket(int ticketId, int agentId) {
        SupportTicket t = tickets.get(ticketId);
        if (t != null) {
            t.setAssignedToId(agentId);
            t.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean resolveTicket(int ticketId, String resolution) {
        SupportTicket t = tickets.get(ticketId);
        if (t != null) {
            t.setStatus("RESOLVED");
            t.setResolution(resolution);
            t.setResolvedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean closeTicket(int ticketId) {
        SupportTicket t = tickets.get(ticketId);
        if (t != null) {
            t.setStatus("CLOSED");
            t.setClosedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== LEAD MANAGEMENT ====================

    @Override
    public List<Lead> getAllLeads() {
        return new ArrayList<>(leads.values());
    }

    @Override
    public List<Lead> getLeadsByStatus(String status) {
        return leads.values().stream()
                .filter(l -> status.equals(l.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Lead getLeadById(int leadId) {
        return leads.get(leadId);
    }

    @Override
    public List<Lead> searchLeads(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return leads.values().stream()
                .filter(l -> l.getCompanyName().toLowerCase().contains(term) ||
                            l.getContactName().toLowerCase().contains(term) ||
                            l.getEmail().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public List<Lead> getLeadsBySalesRep(int salesRepId) {
        return leads.values().stream()
                .filter(l -> l.getAssignedTo() == salesRepId)
                .collect(Collectors.toList());
    }

    @Override
    public Lead createLead(Lead lead) {
        lead.setLeadId(nextLeadId++);
        lead.setCreatedAt(LocalDateTime.now());
        leads.put(lead.getLeadId(), lead);
        return lead;
    }

    @Override
    public boolean updateLead(Lead lead) {
        if (leads.containsKey(lead.getLeadId())) {
            lead.setUpdatedAt(LocalDateTime.now());
            leads.put(lead.getLeadId(), lead);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteLead(int leadId) {
        return leads.remove(leadId) != null;
    }

    @Override
    public boolean assignLead(int leadId, int salesRepId) {
        Lead l = leads.get(leadId);
        if (l != null) {
            l.setAssignedTo(salesRepId);
            l.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public Opportunity convertLead(int leadId) {
        Lead l = leads.get(leadId);
        if (l == null || "CONVERTED".equals(l.getStatus())) return null;

        // Create a customer from lead
        Customer c = new Customer();
        c.setCustomerId(nextCustomerId++);
        c.setCompanyName(l.getCompanyName());
        c.setContactName(l.getContactName());
        c.setEmail(l.getEmail());
        c.setPhone(l.getPhone());
        c.setCustomerType("PROSPECT");
        c.setActive(true);
        c.setAssignedSalesRepId(l.getAssignedTo());
        c.setCreatedAt(LocalDate.now());
        customers.put(c.getCustomerId(), c);

        // Create contact
        Contact contact = new Contact();
        contact.setContactId(nextContactId++);
        contact.setCustomerId(c.getCustomerId());
        String[] nameParts = l.getContactName().split(" ");
        contact.setFirstName(nameParts[0]);
        contact.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        contact.setEmail(l.getEmail());
        contact.setPhone(l.getPhone());
        contact.setPrimary(true);
        contact.setActive(true);
        contact.setCreatedAt(LocalDateTime.now());
        contacts.put(contact.getContactId(), contact);

        // Create opportunity
        Opportunity o = new Opportunity();
        o.setOpportunityId(nextOpportunityId++);
        o.setName(l.getCompanyName() + " - New Opportunity");
        o.setCustomerId(c.getCustomerId());
        o.setCustomerName(c.getCompanyName());
        o.setEstimatedValue(l.getEstimatedValue() != null ? l.getEstimatedValue() : BigDecimal.ZERO);
        o.setStage("PROSPECTING");
        o.setProbability(10);
        o.setExpectedCloseDate(LocalDate.now().plusDays(90));
        o.setAssignedTo(l.getAssignedTo());
        o.setAssignedToName(l.getAssignedToName());
        o.setCreatedAt(LocalDateTime.now());
        opportunities.put(o.getOpportunityId(), o);

        // Update lead status
        l.setStatus("CONVERTED");
        l.setConvertedAt(LocalDateTime.now());
        l.setConvertedToCustomerId(c.getCustomerId());
        l.setConvertedToOpportunityId(o.getOpportunityId());
        l.setUpdatedAt(LocalDateTime.now());

        return o;
    }

    // ==================== OPPORTUNITY MANAGEMENT ====================

    @Override
    public List<Opportunity> getAllOpportunities() {
        return new ArrayList<>(opportunities.values());
    }

    @Override
    public List<Opportunity> getOpportunitiesByStage(String stage) {
        return opportunities.values().stream()
                .filter(o -> stage.equals(o.getStage()))
                .collect(Collectors.toList());
    }

    @Override
    public Opportunity getOpportunityById(int opportunityId) {
        return opportunities.get(opportunityId);
    }

    @Override
    public List<Opportunity> getOpportunitiesBySalesRep(int salesRepId) {
        return opportunities.values().stream()
                .filter(o -> o.getAssignedTo() == salesRepId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Opportunity> getOpportunitiesByCustomer(int customerId) {
        return opportunities.values().stream()
                .filter(o -> o.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public Opportunity createOpportunity(Opportunity opportunity) {
        opportunity.setOpportunityId(nextOpportunityId++);
        opportunity.setCreatedAt(LocalDateTime.now());
        opportunities.put(opportunity.getOpportunityId(), opportunity);
        return opportunity;
    }

    @Override
    public boolean updateOpportunity(Opportunity opportunity) {
        if (opportunities.containsKey(opportunity.getOpportunityId())) {
            opportunity.setUpdatedAt(LocalDateTime.now());
            opportunities.put(opportunity.getOpportunityId(), opportunity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteOpportunity(int opportunityId) {
        return opportunities.remove(opportunityId) != null;
    }

    @Override
    public boolean advanceOpportunityStage(int opportunityId) {
        Opportunity o = opportunities.get(opportunityId);
        if (o == null || o.isClosed()) return false;

        String[] stages = {"PROSPECTING", "QUALIFICATION", "PROPOSAL", "NEGOTIATION", "CLOSED_WON"};
        int currentIndex = Arrays.asList(stages).indexOf(o.getStage());
        if (currentIndex >= 0 && currentIndex < stages.length - 1) {
            o.setStage(stages[currentIndex + 1]);
            o.setProbability(Math.min(o.getProbability() + 20, 100));
            o.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean closeOpportunityWon(int opportunityId, BigDecimal actualValue) {
        Opportunity o = opportunities.get(opportunityId);
        if (o != null && !o.isClosed()) {
            o.closeAsWon(actualValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean closeOpportunityLost(int opportunityId, String lostReason) {
        Opportunity o = opportunities.get(opportunityId);
        if (o != null && !o.isClosed()) {
            o.closeAsLost(lostReason);
            return true;
        }
        return false;
    }

    // ==================== CONTACT MANAGEMENT ====================

    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts.values());
    }

    @Override
    public Contact getContactById(int contactId) {
        return contacts.get(contactId);
    }

    @Override
    public List<Contact> getContactsByCustomer(int customerId) {
        return contacts.values().stream()
                .filter(c -> c.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contact> searchContacts(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return contacts.values().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(term) ||
                            c.getEmail().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public Contact createContact(Contact contact) {
        contact.setContactId(nextContactId++);
        contact.setCreatedAt(LocalDateTime.now());
        contacts.put(contact.getContactId(), contact);
        return contact;
    }

    @Override
    public boolean updateContact(Contact contact) {
        if (contacts.containsKey(contact.getContactId())) {
            contact.setUpdatedAt(LocalDateTime.now());
            contacts.put(contact.getContactId(), contact);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteContact(int contactId) {
        return contacts.remove(contactId) != null;
    }

    // ==================== ACTIVITY MANAGEMENT ====================

    @Override
    public List<Activity> getAllActivities() {
        return activities.values().stream()
                .sorted((a, b) -> {
                    if (a.getDueDate() == null) return 1;
                    if (b.getDueDate() == null) return -1;
                    return a.getDueDate().compareTo(b.getDueDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Activity getActivityById(int activityId) {
        return activities.get(activityId);
    }

    @Override
    public List<Activity> getActivitiesByType(String type) {
        return activities.values().stream()
                .filter(a -> type.equals(a.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getActivitiesByRelated(String relatedType, int relatedId) {
        return activities.values().stream()
                .filter(a -> relatedType.equals(a.getRelatedType()) && a.getRelatedId() == relatedId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getActivitiesByAssignee(int employeeId) {
        return activities.values().stream()
                .filter(a -> a.getAssignedTo() == employeeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getOverdueActivities() {
        return activities.values().stream()
                .filter(Activity::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getUpcomingActivities(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        return activities.values().stream()
                .filter(a -> a.getDueDate() != null &&
                            a.getDueDate().isAfter(now) &&
                            a.getDueDate().isBefore(future) &&
                            !a.isCompleted())
                .collect(Collectors.toList());
    }

    @Override
    public Activity createActivity(Activity activity) {
        activity.setActivityId(nextActivityId++);
        activity.setCreatedAt(LocalDateTime.now());
        activities.put(activity.getActivityId(), activity);
        return activity;
    }

    @Override
    public boolean updateActivity(Activity activity) {
        if (activities.containsKey(activity.getActivityId())) {
            activity.setUpdatedAt(LocalDateTime.now());
            activities.put(activity.getActivityId(), activity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteActivity(int activityId) {
        return activities.remove(activityId) != null;
    }

    @Override
    public boolean completeActivity(int activityId, String outcome) {
        Activity a = activities.get(activityId);
        if (a != null) {
            a.complete();
            a.setOutcome(outcome);
            a.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== ANALYTICS ====================

    @Override
    public Map<String, Integer> getCustomerCountByType() {
        Map<String, Integer> result = new HashMap<>();
        for (Customer c : customers.values()) {
            String type = c.getCustomerType();
            result.put(type, result.getOrDefault(type, 0) + 1);
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTicketCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        for (SupportTicket t : tickets.values()) {
            String status = t.getStatus();
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    @Override
    public List<Customer> getCustomersNeedingFollowUp(int daysSinceContact) {
        LocalDate threshold = LocalDate.now().minusDays(daysSinceContact);
        return customers.values().stream()
                .filter(c -> c.getLastContactDate() == null || c.getLastContactDate().isBefore(threshold))
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageResolutionTime(LocalDate startDate, LocalDate endDate) {
        // Mock implementation - returns a sample value
        return 24.5; // hours
    }

    @Override
    public Map<String, Integer> getLeadCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("NEW", 0);
        result.put("CONTACTED", 0);
        result.put("QUALIFIED", 0);
        result.put("UNQUALIFIED", 0);
        result.put("CONVERTED", 0);

        for (Lead l : leads.values()) {
            String status = l.getStatus();
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    @Override
    public Map<String, Integer> getOpportunityCountByStage() {
        Map<String, Integer> result = new HashMap<>();
        result.put("PROSPECTING", 0);
        result.put("QUALIFICATION", 0);
        result.put("PROPOSAL", 0);
        result.put("NEGOTIATION", 0);
        result.put("CLOSED_WON", 0);
        result.put("CLOSED_LOST", 0);

        for (Opportunity o : opportunities.values()) {
            String stage = o.getStage();
            result.put(stage, result.getOrDefault(stage, 0) + 1);
        }
        return result;
    }

    @Override
    public BigDecimal getTotalPipelineValue() {
        return opportunities.values().stream()
                .filter(o -> !o.isClosed())
                .map(Opportunity::getEstimatedValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getPipelineValueByStage() {
        Map<String, BigDecimal> result = new HashMap<>();
        for (Opportunity o : opportunities.values()) {
            if (!o.isClosed() && o.getEstimatedValue() != null) {
                String stage = o.getStage();
                result.put(stage, result.getOrDefault(stage, BigDecimal.ZERO).add(o.getEstimatedValue()));
            }
        }
        return result;
    }

    @Override
    public Map<String, Integer> getActivityCountByType() {
        Map<String, Integer> result = new HashMap<>();
        for (Activity a : activities.values()) {
            String type = a.getType();
            result.put(type, result.getOrDefault(type, 0) + 1);
        }
        return result;
    }
}
