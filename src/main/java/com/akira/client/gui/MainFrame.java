package com.akira.client.gui;

import com.akira.client.NetworkManager;
import com.akira.client.UserRegisty;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Главное окно приложения.
 * Содержит: Header (user badge | error output | server status),
 * Center (vertical split: table top, canvas bottom),
 * Footer (3-column command buttons).
 */
public class MainFrame extends JFrame {
    private final NetworkManager network;
    private final LabWorkTableModel tableModel;
    private JTable table;
    private VisualizationPanel visualizationPanel;
    private JTextField filterField;
    private javax.swing.Timer refreshTimer;

    // Локализуемые компоненты
    private JLabel userLabel;
    private JLabel statusLabel;
    private JLabel errorOutputLabel;
    private JLabel langLabel;
    private JButton logoutBtn;
    private JButton changePwdBtn;
    private JComboBox<String> langCombo;

    private java.util.Map<String, JButton> commandButtons = new HashMap<>();
    private java.util.Map<String, String> commandKeys;

    // Additional localizable components (stored for updateTexts)
    private JLabel filterLabel;
    private JLabel sortLabel;
    private JCheckBox ascCheck;
    private JButton addBtn;
    private JButton editBtn;
    private JButton delBtn;
    private JButton refreshBtn;
    private JButton execBtn;
    private JButton infoBtn;
    private JButton groupBtn;
    private JButton diffBtn;
    private JButton authorBtn;
    private JButton addRandomBtn;

    public MainFrame(NetworkManager network) {
        this.network = network;
        this.tableModel = new LabWorkTableModel();

        commandKeys = new LinkedHashMap<>();
        commandKeys.put("cmd.insert", "insert");
        commandKeys.put("cmd.add_random", "add_random");
        commandKeys.put("cmd.update", "update");
        commandKeys.put("cmd.remove", "remove");
        commandKeys.put("cmd.remove_lower", "remove_lower");
        commandKeys.put("cmd.replace_greater", "replace_if_greater");
        commandKeys.put("cmd.replace_lower", "replace_if_lower");
        commandKeys.put("cmd.clear", "clear");
        commandKeys.put("cmd.help", "help");

        initComponents();
        setupRefreshTimer();
        loadCollection();

        setSize(UIStyle.scale(1200), UIStyle.scale(800));
        setMinimumSize(new Dimension(UIStyle.scale(900), UIStyle.scale(600)));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private String getMsg(String key) {
        return LocalizationManager.getInstance().getString(key);
    }

    private String fmt(String key, Object... args) {
        return LocalizationManager.getInstance().format(key, args);
    }

    private void initComponents() {
        setTitle(getMsg("app.title"));
        getContentPane().setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout());

        // === Top header ===
        add(createHeader(), BorderLayout.NORTH);

        // === Center: Vertical split (table top, canvas bottom) ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(UIStyle.BACKGROUND);
        splitPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER));
        splitPane.setResizeWeight(0.55);

        // Top: table panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setBackground(UIStyle.BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 2, 8));
        topPanel.add(createFilterSortPanel(), BorderLayout.NORTH);
        topPanel.add(createTablePanel(), BorderLayout.CENTER);
        topPanel.add(createTableActionPanel(), BorderLayout.SOUTH);
        splitPane.setTopComponent(topPanel);

        // Bottom: visualization canvas
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 4));
        bottomPanel.setBackground(UIStyle.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));

        visualizationPanel = new VisualizationPanel();
        visualizationPanel.setCurrentLogin(UserRegisty.getInstance().getUserLogin());
        visualizationPanel.setOnItemSelected(() -> {
            LabWork selected = visualizationPanel.getSelectedItem();
            if (selected != null) {
                showObjectInfo(selected);
            }
        });
        visualizationPanel.setOnItemEdit(lw -> {
            CommandDialogs.editSelected(this, network, lw);
            loadCollection();
        });
        visualizationPanel.setOnItemDelete(lw -> {
            Integer key = tableModel.getKeyForLabWork(lw);
            CommandDialogs.removeSelected(this, network, lw, key);
            loadCollection();
        });
        bottomPanel.add(visualizationPanel, BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        // === Bottom footer ===
        add(createFooter(), BorderLayout.SOUTH);
    }

    // ======================== HEADER ========================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.BACKGROUND_LIGHT);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        // Left: user badge
        String login = UserRegisty.getInstance().getUserLogin();
        userLabel = new JLabel(fmt("main.user.label", login != null ? login : "?"));
        userLabel.setFont(UIStyle.FONT_BOLD);
        userLabel.setForeground(UIStyle.TEXT);
        userLabel.setOpaque(true);
        userLabel.setBackground(UIStyle.PANEL);
        userLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.BORDER),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        userLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(userLabel);
        header.add(leftPanel, BorderLayout.WEST);

        // Center: error output
        errorOutputLabel = new JLabel(getMsg("dialog.error.output"));
        errorOutputLabel.setFont(UIStyle.FONT);
        errorOutputLabel.setForeground(UIStyle.TEXT_SECONDARY);
        errorOutputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(errorOutputLabel, BorderLayout.CENTER);

        // Right: server status + language
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        statusLabel = new JLabel(fmt("main.server.status", "connected"));
        statusLabel.setFont(UIStyle.FONT_BOLD);
        statusLabel.setForeground(UIStyle.ACCENT);
        rightPanel.add(statusLabel);

        langLabel = new JLabel(getMsg("main.language"));
        langLabel.setForeground(UIStyle.TEXT_SECONDARY);
        langLabel.setFont(UIStyle.FONT);
        rightPanel.add(langLabel);

        langCombo = new JComboBox<>(LocalizationManager.getLocaleNames());
        langCombo.setSelectedIndex(LocalizationManager.getInstance().getLocaleIndex());
        UIStyle.styleComboBox(langCombo);
        langCombo.addActionListener(e -> onLanguageChanged());
        rightPanel.add(langCombo);

        logoutBtn = new JButton(getMsg("main.logout"));
        UIStyle.styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> doLogout());
        rightPanel.add(logoutBtn);

        changePwdBtn = new JButton(getMsg("cmd.change_password"));
        UIStyle.styleButton(changePwdBtn);
        changePwdBtn.addActionListener(e -> CommandDialogs.changePassword(this, network));
        rightPanel.add(changePwdBtn);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ======================== FILTER / SORT ========================

    private JPanel createFilterSortPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBackground(UIStyle.BACKGROUND);

        filterLabel = new JLabel(getMsg("main.filter"));
        UIStyle.styleLabel(filterLabel);
        panel.add(filterLabel);

        filterField = new JTextField(15);
        UIStyle.styleTextField(filterField);
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { onFilterChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { onFilterChanged(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onFilterChanged(); }
        });
        panel.add(filterField);

        sortLabel = new JLabel(getMsg("main.sort"));
        UIStyle.styleLabel(sortLabel);
        panel.add(sortLabel);

        String[] sortOptions = {"key", "id", "name", "x", "y", "creationDate", "minimalPoint",
            "maximumPoint", "description", "difficulty", "authorName", "authorX", "authorY", "authorZ", "owner"};
        JComboBox<String> sortCombo = new JComboBox<>(sortOptions);
        UIStyle.styleComboBox(sortCombo);
        sortCombo.addActionListener(e -> {
            tableModel.setSortColumn((String) sortCombo.getSelectedItem());
        });
        panel.add(sortCombo);

        ascCheck = new JCheckBox("ASC", true);
        ascCheck.setForeground(UIStyle.TEXT);
        ascCheck.setBackground(UIStyle.BACKGROUND);
        ascCheck.addActionListener(e -> tableModel.setSortAscending(ascCheck.isSelected()));
        panel.add(ascCheck);

        return panel;
    }

    // ======================== TABLE ========================

    private JScrollPane createTablePanel() {
        table = new JTable(tableModel);
        table.setBackground(UIStyle.BACKGROUND);
        table.setForeground(UIStyle.TEXT);
        table.setGridColor(UIStyle.BORDER);
        table.setSelectionBackground(UIStyle.PANEL);
        table.setSelectionForeground(UIStyle.ACCENT);
        table.setFont(UIStyle.FONT);
        table.setRowHeight(UIStyle.scale(28));
        table.setAutoCreateRowSorter(false);
        table.getTableHeader().setBackground(UIStyle.PANEL);
        table.getTableHeader().setForeground(UIStyle.ACCENT);
        table.getTableHeader().setFont(UIStyle.FONT_BOLD);

        // Click on row to select in visualization
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    LabWork lw = tableModel.getLabWorkAt(row);
                    if (lw != null) {
                        visualizationPanel.setSelectedItem(lw);
                    }
                }
            }
        });

        // Double-click to show info
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        LabWork lw = tableModel.getLabWorkAt(row);
                        if (lw != null) showObjectInfo(lw);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        UIStyle.styleScrollPane(scroll);
        return scroll;
    }

    private JPanel createTableActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBackground(UIStyle.BACKGROUND);

        addBtn = new JButton(getMsg("cmd.insert"));
        UIStyle.styleButton(addBtn);
        addBtn.addActionListener(e -> {
            LabWork lw = CommandDialogs.insertElement(this, network);
            if (lw != null) {
                loadCollection();
                if (lw.getCoordinates() != null) {
                    visualizationPanel.animateAppearance(lw, null);
                }
            }
        });
        panel.add(addBtn);

        editBtn = new JButton(getMsg("dialog.edit"));
        UIStyle.styleButton(editBtn);
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                CommandDialogs.editSelected(this, network, tableModel.getLabWorkAt(row));
                loadCollection();
            } else {
                JOptionPane.showMessageDialog(this, getMsg("msg.select.item"));
            }
        });
        panel.add(editBtn);

        delBtn = new JButton(getMsg("dialog.delete"));
        UIStyle.styleButton(delBtn);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Integer key = tableModel.getKeyAtRow(row);
                CommandDialogs.removeSelected(this, network, tableModel.getLabWorkAt(row), key);
                visualizationPanel.animateDisappearance(tableModel.getLabWorkAt(row), null);
                loadCollection();
            } else {
                JOptionPane.showMessageDialog(this, getMsg("msg.select.item"));
            }
        });
        panel.add(delBtn);

        refreshBtn = new JButton(getMsg("main.refresh"));
        UIStyle.styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadCollection());
        panel.add(refreshBtn);

        return panel;
    }

    // ======================== FOOTER (3-column) ========================

    private JPanel createFooter() {
        JPanel footer = new JPanel(new GridLayout(1, 3, 4, 0));
        footer.setBackground(UIStyle.BACKGROUND_LIGHT);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        // Left column: command buttons
        JPanel leftCol = new JPanel(new GridBagLayout());
        leftCol.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        for (Map.Entry<String, String> entry : commandKeys.entrySet()) {
            JButton btn = new JButton(getMsg(entry.getKey()));
            styleFooterButton(btn);
            btn.addActionListener(e -> {
                executeCommand(entry.getValue());
                if(entry.getValue().equals("clear")) visualizationPanel.setItems(new ArrayList<>());
            });
            leftCol.add(btn, gbc);
            commandButtons.put(entry.getKey(), btn);
            gbc.gridx++;
            if (gbc.gridx > 2) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }
        footer.add(leftCol);

        // Center column: stacked buttons
        JPanel centerCol = new JPanel(new GridLayout(2, 1, 0, 4));
        centerCol.setOpaque(false);

        execBtn = new JButton(getMsg("cmd.execute_script"));
        styleFooterButton(execBtn);
        execBtn.addActionListener(e -> executeCommand("execute_script"));
        centerCol.add(execBtn);

        infoBtn = new JButton(getMsg("cmd.info"));
        styleFooterButton(infoBtn);
        infoBtn.addActionListener(e -> executeCommand("info"));
        centerCol.add(infoBtn);

        footer.add(centerCol);

        // Right column: other operations
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.insets = new Insets(2, 4, 2, 4);
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.gridx = 0; gbcR.gridy = 0;

        groupBtn = new JButton(getMsg("cmd.group_counting"));
        styleFooterButton(groupBtn);
        groupBtn.addActionListener(e -> executeCommand("group_counting_by_maximum_point"));
        rightCol.add(groupBtn, gbcR);
        gbcR.gridx++;

        diffBtn = new JButton(getMsg("cmd.print_difficulty"));
        styleFooterButton(diffBtn);
        diffBtn.addActionListener(e -> executeCommand("print_field_descending_difficulty"));
        rightCol.add(diffBtn, gbcR);
        gbcR.gridx = 0; gbcR.gridy++;

        authorBtn = new JButton(getMsg("cmd.unique_author"));
        styleFooterButton(authorBtn);
        authorBtn.addActionListener(e -> executeCommand("unique_author"));
        rightCol.add(authorBtn, gbcR);
        gbcR.gridx++;

        addRandomBtn = new JButton(getMsg("cmd.add_random"));
        styleFooterButton(addRandomBtn);
        addRandomBtn.addActionListener(e -> executeCommand("add_random"));
        rightCol.add(addRandomBtn, gbcR);

        footer.add(rightCol);

        return footer;
    }

    private void styleFooterButton(JButton btn) {
        btn.setBackground(new Color(0x1A, 0x2E, 0x4A));
        btn.setForeground(UIStyle.TEXT);
        btn.setFont(UIStyle.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.BORDER),
            BorderFactory.createEmptyBorder(UIStyle.scale(8), UIStyle.scale(12),
                UIStyle.scale(8), UIStyle.scale(12))));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIStyle.HOVER);
                btn.setForeground(UIStyle.BACKGROUND);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x1A, 0x2E, 0x4A));
                btn.setForeground(UIStyle.TEXT);
            }
        });
    }

    // ======================== EVENT HANDLERS ========================

    private void onLanguageChanged() {
        int idx = langCombo.getSelectedIndex();
        Locale[] locales = LocalizationManager.getSupportedLocales();
        if (idx >= 0 && idx < locales.length) {
            LocalizationManager.getInstance().setLocale(locales[idx]);
            updateTexts();
        }
    }

    private void updateTexts() {
        setTitle(getMsg("app.title"));
        String login = UserRegisty.getInstance().getUserLogin();
        userLabel.setText(fmt("main.user.label", login != null ? login : "?"));
        langLabel.setText(getMsg("main.language"));
        errorOutputLabel.setText(getMsg("dialog.error.output"));
        logoutBtn.setText(getMsg("main.logout"));
        changePwdBtn.setText(getMsg("cmd.change_password"));
        statusLabel.setText(fmt("main.server.status", "connected"));

        for (Map.Entry<String, JButton> entry : commandButtons.entrySet()) {
            entry.getValue().setText(getMsg(entry.getKey()));
        }

        // Filter / sort labels
        filterLabel.setText(getMsg("main.filter"));
        sortLabel.setText(getMsg("main.sort"));

        // Table action buttons
        addBtn.setText(getMsg("cmd.insert"));
        editBtn.setText(getMsg("dialog.edit"));
        delBtn.setText(getMsg("dialog.delete"));
        refreshBtn.setText(getMsg("main.refresh"));

        // Footer center column
        execBtn.setText(getMsg("cmd.execute_script"));
        infoBtn.setText(getMsg("cmd.info"));

        // Footer right column
        groupBtn.setText(getMsg("cmd.group_counting"));
        diffBtn.setText(getMsg("cmd.print_difficulty"));
        authorBtn.setText(getMsg("cmd.unique_author"));
        addRandomBtn.setText(getMsg("cmd.add_random"));

        tableModel.fireTableStructureChanged();
    }

    private void executeCommand(String cmd) {
        switch (cmd) {
            case "info" -> CommandDialogs.showCollectionInfo(this, network);
            case "clear" -> {
                CommandDialogs.clearCollection(this, network);
                loadCollection();
            }
            case "insert" -> {
                LabWork lw = CommandDialogs.insertElement(this, network);
                if (lw != null) loadCollection();
            }
            case "add_random" -> {
                CommandDialogs.addRandom(this, network);
                loadCollection();
            }
            case "update" -> {
                CommandDialogs.updateElement(this, network);
                loadCollection();
            }
            case "remove" -> {
                CommandDialogs.removeElement(this, network);
                loadCollection();
            }
            case "remove_lower" -> {
                CommandDialogs.removeLowerElements(this, network);
                loadCollection();
            }
            case "replace_if_greater" -> {
                CommandDialogs.replaceIfGreater(this, network);
                loadCollection();
            }
            case "replace_if_lower" -> {
                CommandDialogs.replaceIfLower(this, network);
                loadCollection();
            }
            case "group_counting_by_maximum_point" ->
                CommandDialogs.groupCountingByMaximumPoint(this, network);
            case "print_field_descending_difficulty" ->
                CommandDialogs.printFieldDescendingDifficulty(this, network);
            case "unique_author" -> CommandDialogs.uniqueAuthor(this, network);
            case "execute_script" -> {
                CommandDialogs.executeScriptDialog(this, network);
                loadCollection();
            }
            case "help" -> CommandDialogs.showHelp(this, network);
        }
    }

    private void onFilterChanged() {
        tableModel.setFilterText(filterField.getText().trim());
    }

    void loadCollection() {
        setStatus("status.loading");
        Request req = new Request("show");
        UserRegisty ur = UserRegisty.getInstance();
        if (ur.getUserLogin() != null && ur.getPasswordHash() != null) {
            req.setLogin(ur.getUserLogin());
            req.setPasswordHash(ur.getPasswordHash());
        }
        Response resp = network.sendAndReceive(req);
        if (resp != null) {
            Hashtable<Integer, LabWork> collection = resp.getCollection();
            tableModel.setData(collection);
            if (collection != null) {
                visualizationPanel.setItems(new ArrayList<>(collection.values()));
            }
            int count = collection != null ? collection.size() : 0;
            setStatus("status.loaded", count);
        } else {
            setStatus("status.no_connection");
            statusLabel.setText(fmt("main.server.status", "disconnected"));
            statusLabel.setForeground(UIStyle.NO_BORDER);
        }
    }

    private void setStatus(String key, Object... args) {
        if (errorOutputLabel == null) return;
        String text = LocalizationManager.getInstance().format(key, args);
        errorOutputLabel.setText(text);
        errorOutputLabel.setForeground(UIStyle.TEXT_SECONDARY);
    }

    private void setupRefreshTimer() {
        refreshTimer = new javax.swing.Timer(5000, e -> loadCollection());
        refreshTimer.start();
    }

    private void showObjectInfo(LabWork lw) {
        CommandDialogs.showObjectInfoDialog(this, network, lw, tableModel.getKeyForLabWork(lw));
    }

    private void doLogout() {
        refreshTimer.stop();
        dispose();
        new LoginFrame(network, () -> {
            MainFrame main = new MainFrame(network);
            main.setVisible(true);
        });
    }
}