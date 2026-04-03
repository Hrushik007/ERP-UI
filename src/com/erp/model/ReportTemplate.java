package com.erp.model;

import java.time.LocalDateTime;

/**
 * ReportTemplate model representing a report template with layout and formatting.
 */
public class ReportTemplate {
    private int templateId;
    private String templateCode;
    private String name;
    private String description;
    private String category; // FINANCIAL, SALES, INVENTORY, HR, PROJECT, CUSTOM
    private String layout; // PORTRAIT, LANDSCAPE
    private String paperSize; // A4, LETTER, LEGAL
    private String headerContent; // Header HTML/text
    private String footerContent; // Footer HTML/text
    private String columns; // JSON string of column definitions
    private String styling; // JSON string of styles (colors, fonts, etc.)
    private boolean includeHeader;
    private boolean includeFooter;
    private boolean includePageNumbers;
    private boolean includeDateStamp;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ReportTemplate() {
        this.active = true;
        this.includeHeader = true;
        this.includeFooter = true;
        this.includePageNumbers = true;
        this.includeDateStamp = true;
        this.layout = "PORTRAIT";
        this.paperSize = "A4";
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getHeaderContent() {
        return headerContent;
    }

    public void setHeaderContent(String headerContent) {
        this.headerContent = headerContent;
    }

    public String getFooterContent() {
        return footerContent;
    }

    public void setFooterContent(String footerContent) {
        this.footerContent = footerContent;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getStyling() {
        return styling;
    }

    public void setStyling(String styling) {
        this.styling = styling;
    }

    public boolean isIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public boolean isIncludeFooter() {
        return includeFooter;
    }

    public void setIncludeFooter(boolean includeFooter) {
        this.includeFooter = includeFooter;
    }

    public boolean isIncludePageNumbers() {
        return includePageNumbers;
    }

    public void setIncludePageNumbers(boolean includePageNumbers) {
        this.includePageNumbers = includePageNumbers;
    }

    public boolean isIncludeDateStamp() {
        return includeDateStamp;
    }

    public void setIncludeDateStamp(boolean includeDateStamp) {
        this.includeDateStamp = includeDateStamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
