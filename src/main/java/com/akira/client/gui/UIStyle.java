package com.akira.client.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Единый стиль UI для всего приложения.
 */
public class UIStyle {
    private static final float SCALE = computeScale();

    public static final Color BACKGROUND = Color.decode("#0B1020");
    public static final Color BACKGROUND_LIGHT = Color.decode("#111827");
    public static final Color PANEL = Color.decode("#141B2D");
    public static final Color BORDER = Color.decode("#2A3655");
    public static final Color ACCENT = Color.decode("#22D3EE");
    public static final Color HOVER = Color.decode("#06B6D4");
    public static final Color TEXT = Color.decode("#E2E8F0");
    public static final Color TEXT_SECONDARY = Color.decode("#94A3B8");

    // YES/NO button colors
    public static final Color YES_BG = Color.decode("#1F2F1E");
    public static final Color YES_BORDER = Color.decode("#446E2C");
    public static final Color NO_BG = Color.decode("#512D2B");
    public static final Color NO_BORDER = Color.decode("#D7817E");
    public static final Color CLOSE_BG = Color.decode("#EF4444");

    public static final Font FONT = new Font("SansSerif", Font.PLAIN, scale(15));
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, scale(15));
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, scale(18));
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, scale(13));

    private static float computeScale() {
        try {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            float s = dpi / 96.0f;
            if (s < 1.0f) return 1.0f;
            return Math.min(s, 1.5f);
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static int scale(int value) {
        return Math.round(value * SCALE);
    }

    public static void styleButton(JButton btn) {
        btn.setBackground(PANEL);
        btn.setForeground(TEXT);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT));
        btn.setFocusPainted(false);
        btn.setFont(FONT_BOLD);
        btn.setMargin(new Insets(scale(6), scale(12), scale(6), scale(12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(HOVER);
                btn.setForeground(BACKGROUND);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PANEL);
                btn.setForeground(TEXT);
            }
        });
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(PANEL);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(scale(6), scale(8), scale(6), scale(8))));
        field.setFont(FONT);
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(PANEL);
        combo.setForeground(TEXT);
        combo.setFont(FONT);
        combo.setFocusable(false);
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT);
        label.setFont(FONT);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL);
    }

    public static void styleScrollPane(JScrollPane scroll) {
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static Border createBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT),
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            FONT_BOLD,
            ACCENT
        );
    }

    public static void applyGlobalStyle() {
        UIManager.put("defaultFont", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("Button.background", PANEL);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.font", FONT);
    }
}