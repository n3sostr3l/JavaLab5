package com.akira.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Утилита для отображения кастомных диалогов с единой стилистикой.
 * Строит модальные окна по шаблону: Header → Body → Buttons → Error footer.
 */
public class DialogUtil {

    static {
        UIManager.put("OptionPane.background", UIStyle.BACKGROUND);
        UIManager.put("Panel.background", UIStyle.BACKGROUND);
        UIManager.put("OptionPane.messageForeground", UIStyle.TEXT);
        UIManager.put("Label.foreground", UIStyle.TEXT);
        UIManager.put("TextField.background", UIStyle.PANEL);
        UIManager.put("TextField.foreground", UIStyle.TEXT);
        UIManager.put("TextField.caretForeground", UIStyle.TEXT);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(UIStyle.BORDER));
        UIManager.put("Button.background", UIStyle.PANEL);
        UIManager.put("Button.foreground", UIStyle.TEXT);
        UIManager.put("Button.border", BorderFactory.createLineBorder(UIStyle.ACCENT));
    }

    // ======================== BUTTON FACTORY ========================

    /** Create a styled YES/Confirm button (green). */
    public static JButton createYesButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIStyle.YES_BG);
        btn.setForeground(UIStyle.TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.YES_BORDER, 2),
            BorderFactory.createEmptyBorder(UIStyle.scale(8), UIStyle.scale(20), UIStyle.scale(8), UIStyle.scale(20))));
        btn.setFocusPainted(false);
        btn.setFont(UIStyle.FONT_BOLD);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.YES_BORDER);
                btn.setForeground(UIStyle.BACKGROUND);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.YES_BG);
                btn.setForeground(UIStyle.TEXT);
            }
        });
        return btn;
    }

    /** Create a styled NO/Cancel button (red). */
    public static JButton createNoButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIStyle.NO_BG);
        btn.setForeground(UIStyle.TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.NO_BORDER, 2),
            BorderFactory.createEmptyBorder(UIStyle.scale(8), UIStyle.scale(20), UIStyle.scale(8), UIStyle.scale(20))));
        btn.setFocusPainted(false);
        btn.setFont(UIStyle.FONT_BOLD);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.NO_BORDER);
                btn.setForeground(UIStyle.BACKGROUND);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.NO_BG);
                btn.setForeground(UIStyle.TEXT);
            }
        });
        return btn;
    }

    /** Create a standard styled button (cyan accent). */
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIStyle.PANEL);
        btn.setForeground(UIStyle.TEXT);
        btn.setFont(UIStyle.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(UIStyle.ACCENT, 2));
        btn.setMargin(new Insets(UIStyle.scale(8), UIStyle.scale(20), UIStyle.scale(8), UIStyle.scale(20)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.HOVER);
                btn.setForeground(UIStyle.BACKGROUND);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UIStyle.PANEL);
                btn.setForeground(UIStyle.TEXT);
            }
        });
        return btn;
    }

    /** Create a close [X] button (red square). */
    public static JButton createCloseButton() {
        JButton btn = new JButton("✖");
        btn.setBackground(UIStyle.CLOSE_BG);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, UIStyle.scale(14)));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(UIStyle.scale(4), UIStyle.scale(8), UIStyle.scale(4), UIStyle.scale(8)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        return btn;
    }

    // ======================== MODAL BUILDER ========================

    /**
     * Build a modal dialog with: Header (title + [X]) → Body → Buttons → Error footer.
     * @param parent parent frame
     * @return a ModalBuilder for fluent construction
     */
    public static ModalBuilder modal(JFrame parent) {
        return new ModalBuilder(parent);
    }

    public static class ModalBuilder {
        private final JFrame parent;
        private String title = "";
        private Color titleColor = UIStyle.TEXT;
        private JComponent body;
        private Component[] buttons;
        private String errorText;
        private int width = 420;
        private int height = 260;
        private boolean resizable = false;

        ModalBuilder(JFrame parent) {
            this.parent = parent;
        }

        public ModalBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ModalBuilder titleColor(Color color) {
            this.titleColor = color;
            return this;
        }

        public ModalBuilder body(JComponent body) {
            this.body = body;
            return this;
        }

        public ModalBuilder buttons(Component... buttons) {
            this.buttons = buttons;
            return this;
        }

        public ModalBuilder errorText(String text) {
            this.errorText = text;
            return this;
        }

        public ModalBuilder size(int w, int h) {
            this.width = w;
            this.height = h;
            return this;
        }

        public ModalBuilder resizable(boolean r) {
            this.resizable = r;
            return this;
        }

        /** Build and show the dialog (modal). Returns the built JDialog. */
        public JDialog build() {
            JDialog dialog = new JDialog(parent, "", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(resizable);
            dialog.setUndecorated(true);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(UIStyle.BACKGROUND);
            root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER),
                BorderFactory.createEmptyBorder(UIStyle.scale(4), UIStyle.scale(4),
                    UIStyle.scale(4), UIStyle.scale(4))));

            // === Header + separator ===
            JPanel headerWrapper = new JPanel(new BorderLayout());
            headerWrapper.setOpaque(false);

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(UIStyle.PANEL);
            header.setBorder(BorderFactory.createEmptyBorder(
                UIStyle.scale(8), UIStyle.scale(12), UIStyle.scale(8), UIStyle.scale(4)));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(UIStyle.FONT_TITLE);
            titleLabel.setForeground(titleColor);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, UIStyle.scale(24), 0, 0));
            header.add(titleLabel, BorderLayout.CENTER);

            JButton closeBtn = createCloseButton();
            closeBtn.addActionListener(e -> dialog.dispose());
            JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            closePanel.setOpaque(false);
            closePanel.add(closeBtn);
            header.add(closePanel, BorderLayout.EAST);

            headerWrapper.add(header, BorderLayout.CENTER);

            JSeparator sep = new JSeparator();
            sep.setForeground(UIStyle.BORDER);
            sep.setBackground(UIStyle.BORDER);
            headerWrapper.add(sep, BorderLayout.SOUTH);

            root.add(headerWrapper, BorderLayout.NORTH);

            // === Body ===
            JPanel bodyPanel = new JPanel(new BorderLayout());
            bodyPanel.setBackground(UIStyle.BACKGROUND);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(
                UIStyle.scale(20), UIStyle.scale(20), UIStyle.scale(20), UIStyle.scale(20)));
            if (body != null) {
                bodyPanel.add(body, BorderLayout.CENTER);
            }
            root.add(bodyPanel, BorderLayout.CENTER);

            // === Buttons ===
            if (buttons != null && buttons.length > 0) {
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIStyle.scale(16), 0));
                btnPanel.setBackground(UIStyle.BACKGROUND);
                btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIStyle.scale(16), 0));
                for (Component b : buttons) {
                    btnPanel.add(b);
                }
                root.add(btnPanel, BorderLayout.SOUTH);
            }

            // === Error footer ===
            if (errorText != null && !errorText.isEmpty()) {
                JPanel errorPanel = new JPanel(new BorderLayout());
                errorPanel.setBackground(UIStyle.BACKGROUND_LIGHT);
                errorPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIStyle.BORDER),
                    BorderFactory.createEmptyBorder(UIStyle.scale(4), UIStyle.scale(8), UIStyle.scale(4), UIStyle.scale(8))));
                JLabel errorLabel = new JLabel(errorText, SwingConstants.CENTER);
                errorLabel.setForeground(UIStyle.TEXT_SECONDARY);
                errorLabel.setFont(UIStyle.FONT_SMALL);
                errorPanel.add(errorLabel, BorderLayout.CENTER);

                JPanel footerWrapper = new JPanel(new BorderLayout());
                footerWrapper.setBackground(UIStyle.BACKGROUND);
                footerWrapper.setBorder(BorderFactory.createEmptyBorder(
                    UIStyle.scale(16), 0, 0, 0));
                footerWrapper.add(errorPanel, BorderLayout.CENTER);
                root.add(footerWrapper, BorderLayout.SOUTH);
            }

            dialog.setContentPane(root);
            dialog.setSize(UIStyle.scale(width), UIStyle.scale(height));
            dialog.setLocationRelativeTo(parent);
            return dialog;
        }
    }

    // ======================== SIMPLE DIALOG HELPERS ========================

    /** Show an informational message. */
    public static void showMessage(JFrame parent, String message, String title) {
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        msgLabel.setForeground(UIStyle.TEXT);
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton okBtn = createYesButton(LocalizationManager.getInstance().getString("dialog.confirm"));
        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(UIStyle.ACCENT)
            .body(msgLabel)
            .buttons(okBtn)
            .build();
        okBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /** Show an error message. */
    public static void showError(JFrame parent, String message, String title) {
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;color:#D7817E;'>" + message + "</div></html>");
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton okBtn = createYesButton("OK");
        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(UIStyle.NO_BORDER)
            .body(msgLabel)
            .buttons(okBtn)
            .build();
        okBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /** Show a YES/NO confirmation dialog. */
    public static boolean showConfirm(JFrame parent, String message, String title) {
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        msgLabel.setForeground(UIStyle.TEXT);
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        final boolean[] result = {false};
        JButton yesBtn = createYesButton(LocalizationManager.getInstance().getString("dialog.confirm"));
        JButton noBtn = createNoButton(LocalizationManager.getInstance().getString("dialog.cancel"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(UIStyle.ACCENT)
            .body(msgLabel)
            .buttons(yesBtn, noBtn)
            .build();
        yesBtn.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        noBtn.addActionListener(e -> { result[0] = false; dialog.dispose(); });
        dialog.setVisible(true);
        return result[0];
    }

    /** Show an input dialog. */
    public static String showInput(JFrame parent, String prompt, String title) {
        JPanel inputPanel = new JPanel(new BorderLayout(UIStyle.scale(8), UIStyle.scale(8)));
        inputPanel.setBackground(UIStyle.BACKGROUND);

        JLabel promptLabel = new JLabel(prompt);
        promptLabel.setForeground(UIStyle.TEXT);
        promptLabel.setFont(UIStyle.FONT);
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(promptLabel, BorderLayout.NORTH);

        JTextField inputField = new JTextField(20);
        inputField.setBackground(UIStyle.PANEL);
        inputField.setForeground(UIStyle.TEXT);
        inputField.setCaretColor(UIStyle.TEXT);
        inputField.setFont(UIStyle.FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.BORDER),
            BorderFactory.createEmptyBorder(UIStyle.scale(6), UIStyle.scale(8), UIStyle.scale(6), UIStyle.scale(8))));
        inputPanel.add(inputField, BorderLayout.SOUTH);

        final String[] result = {null};
        JButton okBtn = createYesButton(LocalizationManager.getInstance().getString("dialog.confirm"));
        JButton cancelBtn = createNoButton(LocalizationManager.getInstance().getString("dialog.cancel"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(UIStyle.ACCENT)
            .body(inputPanel)
            .buttons(okBtn, cancelBtn)
            .build();
        okBtn.addActionListener(e -> {
            String val = inputField.getText();
            result[0] = val.isEmpty() ? null : val;
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> { result[0] = null; dialog.dispose(); });
        SwingUtilities.invokeLater(inputField::requestFocus);
        dialog.setVisible(true);
        return result[0];
    }
}