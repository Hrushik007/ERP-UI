package com.erp.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

/**
 * Constants class containing application-wide constants.
 * Uses final static fields for immutability - a key OOP principle.
 *
 * This class cannot be instantiated (private constructor) following
 * the utility class pattern.
 */
public final class Constants {

    // Private constructor prevents instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ==================== APPLICATION INFO ====================
    public static final String APP_NAME = "Enterprise Resource Planning System";
    public static final String APP_VERSION = "1.0.0";

    // ==================== WINDOW DIMENSIONS ====================
    public static final int LOGIN_WIDTH = 400;
    public static final int LOGIN_HEIGHT = 500;
    public static final int MAIN_WIDTH = 1280;
    public static final int MAIN_HEIGHT = 720;
    public static final Dimension LOGIN_SIZE = new Dimension(LOGIN_WIDTH, LOGIN_HEIGHT);
    public static final Dimension MAIN_SIZE = new Dimension(MAIN_WIDTH, MAIN_HEIGHT);

    // ==================== COLOR SCHEME ====================
    // Primary colors - Professional blue theme
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Main blue
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);        // Darker blue
    public static final Color PRIMARY_LIGHT = new Color(174, 214, 241);     // Light blue

    // Secondary colors
    public static final Color SECONDARY_COLOR = new Color(44, 62, 80);      // Dark gray-blue
    public static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Green accent
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);       // Green for success
    public static final Color WARNING_COLOR = new Color(241, 196, 15);      // Yellow for warnings
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red for errors

    // Background colors
    public static final Color BG_DARK = new Color(52, 73, 94);              // Sidebar background
    public static final Color BG_LIGHT = new Color(236, 240, 241);          // Main content background
    public static final Color BG_WHITE = new Color(255, 255, 255);          // Card background

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);         // Main text
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);    // Secondary text
    public static final Color TEXT_LIGHT = new Color(255, 255, 255);        // Light text on dark bg

    // ==================== FONTS ====================
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    // ==================== SPACING ====================
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_XLARGE = 30;

    // ==================== SIDEBAR ====================
    public static final int SIDEBAR_WIDTH = 250;

    // ==================== MODULE NAMES ====================
    public static final String MODULE_DASHBOARD = "Dashboard";
    public static final String MODULE_CRM = "CRM";
    public static final String MODULE_PROJECT = "Project Management";
    public static final String MODULE_HR = "HR Management";
    public static final String MODULE_FINANCE = "Financial Management";
    public static final String MODULE_SALES = "Sales Management";
    public static final String MODULE_INVENTORY = "Supply Chain";
    public static final String MODULE_MANUFACTURING = "Manufacturing";
    public static final String MODULE_ACCOUNTING = "Accounting";
    public static final String MODULE_REPORTING = "Reporting";
    public static final String MODULE_ANALYTICS = "Data Analytics";
    public static final String MODULE_MARKETING = "Marketing";
    public static final String MODULE_ORDER = "Order Processing";
    public static final String MODULE_AUTOMATION = "Automation";
    public static final String MODULE_BI = "Business Intelligence";
    public static final String MODULE_INTEGRATION = "Integration";
}
