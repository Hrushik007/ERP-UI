package com.erp.service.mock;

import com.erp.model.Campaign;
import com.erp.service.interfaces.MarketingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockMarketingService provides sample data for Marketing Module UI.
 *
 * Demonstrates the Singleton pattern - only one instance exists.
 */
public class MockMarketingService implements MarketingService {

    private Map<Integer, Campaign> campaigns;
    private Map<Integer, List<Integer>> campaignLeads; // campaignId -> list of customerIds

    private int nextCampaignId = 1;

    private static MockMarketingService instance;

    public static synchronized MockMarketingService getInstance() {
        if (instance == null) {
            instance = new MockMarketingService();
        }
        return instance;
    }

    private MockMarketingService() {
        campaigns = new HashMap<>();
        campaignLeads = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        createSampleCampaign("Spring Product Launch", "Launch campaign for new product line",
                "EMAIL", "ACTIVE", LocalDate.now().minusDays(15), LocalDate.now().plusDays(15),
                new BigDecimal("5000"), new BigDecimal("3200"), "Small Business Owners", 100, 45, "Email");

        createSampleCampaign("Social Media Blitz", "Brand awareness campaign on social platforms",
                "SOCIAL_MEDIA", "ACTIVE", LocalDate.now().minusDays(30), LocalDate.now().plusDays(30),
                new BigDecimal("8000"), new BigDecimal("6100"), "Tech Professionals", 200, 120, "Social Media");

        createSampleCampaign("Trade Show 2025", "Annual industry trade show booth and presentations",
                "EVENT", "COMPLETED", LocalDate.now().minusDays(60), LocalDate.now().minusDays(55),
                new BigDecimal("15000"), new BigDecimal("14500"), "Enterprise Clients", 50, 38, "Events");

        createSampleCampaign("Google Ads Q2", "PPC campaign targeting key product searches",
                "ADS", "ACTIVE", LocalDate.now().minusDays(10), LocalDate.now().plusDays(80),
                new BigDecimal("12000"), new BigDecimal("4800"), "General Audience", 300, 95, "Paid Search");

        createSampleCampaign("Blog Content Series", "SEO-driven content marketing initiative",
                "CONTENT", "PLANNED", LocalDate.now().plusDays(5), LocalDate.now().plusDays(90),
                new BigDecimal("3000"), BigDecimal.ZERO, "Industry Decision Makers", 150, 0, "Content");

        createSampleCampaign("Holiday Sale Email Blast", "Promotional emails for end-of-year sale",
                "EMAIL", "COMPLETED", LocalDate.now().minusDays(120), LocalDate.now().minusDays(90),
                new BigDecimal("2000"), new BigDecimal("1950"), "Existing Customers", 80, 72, "Email");

        createSampleCampaign("LinkedIn Sponsored Posts", "B2B lead generation via LinkedIn",
                "SOCIAL_MEDIA", "PAUSED", LocalDate.now().minusDays(20), LocalDate.now().plusDays(40),
                new BigDecimal("6000"), new BigDecimal("2800"), "B2B Decision Makers", 75, 30, "Social Media");

        // Add sample leads for campaigns
        campaignLeads.put(1, new ArrayList<>(Arrays.asList(1, 2, 3)));
        campaignLeads.put(2, new ArrayList<>(Arrays.asList(4, 5, 6, 7)));
        campaignLeads.put(3, new ArrayList<>(Arrays.asList(8, 9)));
        campaignLeads.put(4, new ArrayList<>(Arrays.asList(10, 11, 12)));
    }

    private void createSampleCampaign(String name, String description, String type, String status,
                                       LocalDate startDate, LocalDate endDate, BigDecimal budget,
                                       BigDecimal actualSpend, String targetAudience,
                                       int leadTarget, int leadsGenerated, String channel) {
        Campaign c = new Campaign();
        c.setCampaignId(nextCampaignId++);
        c.setName(name);
        c.setDescription(description);
        c.setType(type);
        c.setStatus(status);
        c.setStartDate(startDate);
        c.setEndDate(endDate);
        c.setBudget(budget);
        c.setActualSpend(actualSpend);
        c.setTargetAudience(targetAudience);
        c.setLeadTarget(leadTarget);
        c.setLeadsGenerated(leadsGenerated);
        c.setOwnerId(1);
        c.setChannel(channel);
        c.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 60) + 10));
        c.setUpdatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 5)));
        campaigns.put(c.getCampaignId(), c);
    }

    // ==================== CAMPAIGN MANAGEMENT ====================

    @Override
    public List<Campaign> getAllCampaigns() {
        return new ArrayList<>(campaigns.values());
    }

    @Override
    public List<Campaign> getCampaignsByStatus(String status) {
        return campaigns.values().stream()
                .filter(c -> status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Campaign> getCampaignsByType(String type) {
        return campaigns.values().stream()
                .filter(c -> type.equals(c.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Campaign> getActiveCampaigns() {
        return getCampaignsByStatus("ACTIVE");
    }

    @Override
    public Campaign getCampaignById(int campaignId) {
        return campaigns.get(campaignId);
    }

    @Override
    public Campaign createCampaign(Campaign campaign) {
        campaign.setCampaignId(nextCampaignId++);
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());
        campaigns.put(campaign.getCampaignId(), campaign);
        return campaign;
    }

    @Override
    public boolean updateCampaign(Campaign campaign) {
        if (campaigns.containsKey(campaign.getCampaignId())) {
            campaign.setUpdatedAt(LocalDateTime.now());
            campaigns.put(campaign.getCampaignId(), campaign);
            return true;
        }
        return false;
    }

    @Override
    public boolean startCampaign(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        if (c != null && ("PLANNED".equals(c.getStatus()) || "PAUSED".equals(c.getStatus()))) {
            c.setStatus("ACTIVE");
            c.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean pauseCampaign(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        if (c != null && "ACTIVE".equals(c.getStatus())) {
            c.setStatus("PAUSED");
            c.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean endCampaign(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        if (c != null && ("ACTIVE".equals(c.getStatus()) || "PAUSED".equals(c.getStatus()))) {
            c.setStatus("COMPLETED");
            c.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCampaign(int campaignId) {
        campaignLeads.remove(campaignId);
        return campaigns.remove(campaignId) != null;
    }

    // ==================== LEAD MANAGEMENT ====================

    @Override
    public boolean recordLead(int campaignId, int customerId) {
        Campaign c = campaigns.get(campaignId);
        if (c == null) return false;

        campaignLeads.computeIfAbsent(campaignId, k -> new ArrayList<>());
        List<Integer> leads = campaignLeads.get(campaignId);
        if (!leads.contains(customerId)) {
            leads.add(customerId);
            c.setLeadsGenerated(c.getLeadsGenerated() + 1);
            c.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> getLeadsByCampaign(int campaignId) {
        return campaignLeads.getOrDefault(campaignId, new ArrayList<>());
    }

    @Override
    public int getLeadCount(int campaignId) {
        return campaignLeads.getOrDefault(campaignId, new ArrayList<>()).size();
    }

    // ==================== MARKETING ANALYTICS ====================

    @Override
    public Map<String, Object> getCampaignMetrics(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        Map<String, Object> metrics = new HashMap<>();
        if (c == null) return metrics;

        metrics.put("leads", c.getLeadsGenerated());
        metrics.put("leadTarget", c.getLeadTarget());
        metrics.put("budget", c.getBudget());
        metrics.put("spent", c.getActualSpend());
        metrics.put("variance", c.getBudgetVariance());
        metrics.put("conversionRate", c.getLeadConversionRate());

        // Calculate cost per lead
        if (c.getLeadsGenerated() > 0 && c.getActualSpend() != null) {
            metrics.put("costPerLead", c.getActualSpend().divide(
                    new BigDecimal(c.getLeadsGenerated()), 2, RoundingMode.HALF_UP));
        } else {
            metrics.put("costPerLead", BigDecimal.ZERO);
        }

        // Mock ROI
        double roi = c.getLeadsGenerated() > 0 ? (c.getLeadsGenerated() * 150.0 - c.getActualSpend().doubleValue()) / c.getActualSpend().doubleValue() * 100 : 0;
        metrics.put("roi", Math.round(roi * 100.0) / 100.0);

        return metrics;
    }

    @Override
    public BigDecimal getTotalMarketingSpend(LocalDate startDate, LocalDate endDate) {
        return campaigns.values().stream()
                .filter(c -> c.getActualSpend() != null)
                .filter(c -> c.getStartDate() != null && !c.getStartDate().isAfter(endDate))
                .filter(c -> c.getEndDate() != null && !c.getEndDate().isBefore(startDate))
                .map(Campaign::getActualSpend)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getSpendByType(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> result = new HashMap<>();
        for (Campaign c : campaigns.values()) {
            if (c.getActualSpend() != null && c.getActualSpend().compareTo(BigDecimal.ZERO) > 0) {
                String type = c.getType();
                result.put(type, result.getOrDefault(type, BigDecimal.ZERO).add(c.getActualSpend()));
            }
        }
        return result;
    }

    @Override
    public double getConversionRate(int campaignId) {
        if (campaignId == 0) {
            int totalLeads = campaigns.values().stream().mapToInt(Campaign::getLeadsGenerated).sum();
            int totalTarget = campaigns.values().stream().mapToInt(Campaign::getLeadTarget).sum();
            return totalTarget > 0 ? (double) totalLeads / totalTarget * 100 : 0;
        }
        Campaign c = campaigns.get(campaignId);
        return c != null ? c.getLeadConversionRate() : 0;
    }

    @Override
    public BigDecimal getCostPerLead(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        if (c != null && c.getLeadsGenerated() > 0 && c.getActualSpend() != null) {
            return c.getActualSpend().divide(new BigDecimal(c.getLeadsGenerated()), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public double getCampaignROI(int campaignId) {
        Campaign c = campaigns.get(campaignId);
        if (c != null && c.getActualSpend() != null && c.getActualSpend().compareTo(BigDecimal.ZERO) > 0) {
            // Mock: assume each lead is worth $150 in revenue
            double revenue = c.getLeadsGenerated() * 150.0;
            double spend = c.getActualSpend().doubleValue();
            return Math.round((revenue - spend) / spend * 100 * 100.0) / 100.0;
        }
        return 0;
    }

    // ==================== ADDITIONAL HELPERS (for UI) ====================

    /**
     * Get campaign count by status.
     * @return Map of status to count
     */
    public Map<String, Integer> getCampaignCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("PLANNED", 0);
        result.put("ACTIVE", 0);
        result.put("PAUSED", 0);
        result.put("COMPLETED", 0);
        result.put("CANCELLED", 0);

        for (Campaign c : campaigns.values()) {
            String status = c.getStatus();
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }

    /**
     * Get campaign count by type.
     * @return Map of type to count
     */
    public Map<String, Integer> getCampaignCountByType() {
        Map<String, Integer> result = new HashMap<>();
        for (Campaign c : campaigns.values()) {
            String type = c.getType();
            result.put(type, result.getOrDefault(type, 0) + 1);
        }
        return result;
    }

    /**
     * Get total budget across all campaigns.
     * @return Total budget
     */
    public BigDecimal getTotalBudget() {
        return campaigns.values().stream()
                .filter(c -> c.getBudget() != null)
                .map(Campaign::getBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total actual spend across all campaigns.
     * @return Total spend
     */
    public BigDecimal getTotalSpend() {
        return campaigns.values().stream()
                .filter(c -> c.getActualSpend() != null)
                .map(Campaign::getActualSpend)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total leads generated across all campaigns.
     * @return Total leads
     */
    public int getTotalLeadsGenerated() {
        return campaigns.values().stream()
                .mapToInt(Campaign::getLeadsGenerated)
                .sum();
    }
}
