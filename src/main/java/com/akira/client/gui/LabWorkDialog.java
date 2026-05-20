package com.akira.client.gui;

import com.akira.general.datas.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Диалог для создания/редактирования объекта LabWork.
 */
public class LabWorkDialog extends JDialog {
    private LabWork result;
    private final boolean edit;

    private JTextField nameField;
    private JTextField coordXField;
    private JTextField coordYField;
    private JTextField minPointField;
    private JTextField maxPointField;
    private JTextField descField;
    private JComboBox<String> difficultyCombo;
    private JTextField authorNameField;
    private JTextField birthdayField;
    private JTextField locXField;
    private JTextField locYField;
    private JTextField locZField;
    private JCheckBox addLocationCheck;

    public LabWorkDialog(JFrame parent, String title) {
        super(parent, title, true);
        this.edit = false;
        initComponents();
        pack();
        setMinimumSize(new Dimension(UIStyle.scale(520), UIStyle.scale(540)));
        setLocationRelativeTo(parent);
    }

    public LabWorkDialog(JFrame parent, String title, LabWork existing) {
        super(parent, title, true);
        this.edit = true;
        initComponents();
        populateFields(existing);
        pack();
        setMinimumSize(new Dimension(UIStyle.scale(520), UIStyle.scale(540)));
        setLocationRelativeTo(parent);
    }

    private String getMsg(String key) {
        return LocalizationManager.getInstance().getString(key);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyle.BACKGROUND);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIStyle.scale(3), UIStyle.scale(8), UIStyle.scale(3), UIStyle.scale(8));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        addField(form, gbc, getMsg("field.name"), nameField = new JTextField(15));
        addField(form, gbc, getMsg("field.x"), coordXField = new JTextField(15));
        addField(form, gbc, getMsg("field.y"), coordYField = new JTextField(15));
        addField(form, gbc, getMsg("field.minimalPoint"), minPointField = new JTextField(15));
        addField(form, gbc, getMsg("field.maximumPoint"), maxPointField = new JTextField(15));
        addField(form, gbc, getMsg("field.description"), descField = new JTextField(15));
        addField(form, gbc, getMsg("field.difficulty"), difficultyCombo = new JComboBox<>(
            new String[]{"", "EASY", "HARD", "VERY_HARD", "INSANE"}));
        addField(form, gbc, getMsg("field.authorName"), authorNameField = new JTextField(15));
        addField(form, gbc, getMsg("field.birthday"), birthdayField = new JTextField(15));

        addLocationCheck = new JCheckBox(getMsg("field.location"));
        addLocationCheck.setForeground(UIStyle.TEXT);
        addLocationCheck.setBackground(UIStyle.BACKGROUND);
        addLocationCheck.setFont(UIStyle.FONT);
        addLocationCheck.addActionListener(e -> toggleLocationFields());
        gbc.gridwidth = 2;
        form.add(addLocationCheck, gbc);
        gbc.gridwidth = 1;

        addField(form, gbc, getMsg("field.locX"), locXField = new JTextField(15));
        addField(form, gbc, getMsg("field.locY"), locYField = new JTextField(15));
        addField(form, gbc, getMsg("field.locZ"), locZField = new JTextField(15));
        toggleLocationFields();
        
        initFieldFonts();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIStyle.scale(10), UIStyle.scale(8)));
        btnPanel.setBackground(UIStyle.BACKGROUND);

        JButton okBtn = new JButton(getMsg("dialog.save"));
        UIStyle.styleButton(okBtn);
        okBtn.setFont(UIStyle.FONT_BOLD);
        okBtn.addActionListener(e -> onOk());
        btnPanel.add(okBtn);

        JButton cancelBtn = new JButton(getMsg("dialog.cancel"));
        UIStyle.styleButton(cancelBtn);
        cancelBtn.setFont(UIStyle.FONT_BOLD);
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(cancelBtn);

        add(new JScrollPane(form), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0;
        JLabel jlabel = new JLabel(label + ":");
        UIStyle.styleLabel(jlabel);
        jlabel.setFont(UIStyle.FONT);
        panel.add(jlabel, gbc);
        gbc.gridx = 1;
        if (field instanceof JTextField tf) {
            UIStyle.styleTextField(tf);
            tf.setFont(UIStyle.FONT);
        }
        else if (field instanceof JComboBox cb) {
            UIStyle.styleComboBox(cb);
            cb.setFont(UIStyle.FONT);
        }
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private void toggleLocationFields() {
        boolean enabled = addLocationCheck.isSelected();
        locXField.setEnabled(enabled);
        locYField.setEnabled(enabled);
        locZField.setEnabled(enabled);
    }
    
    private void initFieldFonts() {
        // Ensure all text fields use the scaled font
        nameField.setFont(UIStyle.FONT);
        coordXField.setFont(UIStyle.FONT);
        coordYField.setFont(UIStyle.FONT);
        minPointField.setFont(UIStyle.FONT);
        maxPointField.setFont(UIStyle.FONT);
        descField.setFont(UIStyle.FONT);
        difficultyCombo.setFont(UIStyle.FONT);
        authorNameField.setFont(UIStyle.FONT);
        birthdayField.setFont(UIStyle.FONT);
        locXField.setFont(UIStyle.FONT);
        locYField.setFont(UIStyle.FONT);
        locZField.setFont(UIStyle.FONT);
    }

    private void populateFields(LabWork lw) {
        nameField.setText(lw.getName());
        if (lw.getCoordinates() != null) {
            coordXField.setText(String.valueOf(lw.getCoordinates().getX()));
            coordYField.setText(String.valueOf(lw.getCoordinates().getY()));
        }
        if (lw.getMinimalPoint() != null)
            minPointField.setText(String.valueOf(lw.getMinimalPoint()));
        maxPointField.setText(String.valueOf(lw.getMaximumPoint()));
        if (lw.getDescription() != null) descField.setText(lw.getDescription());
        if (lw.getDifficulty() != null) difficultyCombo.setSelectedItem(lw.getDifficulty().name());
        if (lw.getAuthor() != null) {
            authorNameField.setText(lw.getAuthor().getName());
            if (lw.getAuthor().getBirthday() != null)
                birthdayField.setText(lw.getAuthor().getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            if (lw.getAuthor().getLocation() != null) {
                addLocationCheck.setSelected(true);
                toggleLocationFields();
                locXField.setText(String.valueOf(lw.getAuthor().getLocation().getX()));
                locYField.setText(String.valueOf(lw.getAuthor().getLocation().getY()));
                locZField.setText(String.valueOf(lw.getAuthor().getLocation().getZ()));
            }
        }
    }

    private void onOk() {
        try {
            LabWork lw = new LabWork();
            lw.setName(nameField.getText().trim());
            if (lw.getName().isEmpty()) { showError("field.name"); return; }

            Coordinates coords = new Coordinates();
            coords.setX(Integer.parseInt(coordXField.getText().trim()));
            coords.setY(Long.parseLong(coordYField.getText().trim()));
            lw.setCoordinates(coords);

            String mp = minPointField.getText().trim();
            if (!mp.isEmpty()) {
                float val = Float.parseFloat(mp);
                if (val <= 0) { showError("field.minimalPoint"); return; }
                lw.setMinimalPoint(val);
            }

            long maxP = Long.parseLong(maxPointField.getText().trim());
            if (maxP <= 0) { showError("field.maximumPoint"); return; }
            lw.setMaximumPoint(maxP);

            String desc = descField.getText().trim();
            if (!desc.isEmpty()) lw.setDescription(desc);

            String diff = (String) difficultyCombo.getSelectedItem();
            if (diff != null && !diff.isEmpty()) lw.setDifficulty(Difficulty.valueOf(diff));

            Person author = new Person();
            String an = authorNameField.getText().trim();
            if (an.isEmpty()) { showError("field.authorName"); return; }
            author.setName(an);

            String bd = birthdayField.getText().trim();
            if (!bd.isEmpty()) {
                try {
                    author.setBirthday(LocalDate.parse(bd, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                } catch (Exception e) {
                    showError("field.birthday");
                    return;
                }
            }

            if (addLocationCheck.isSelected()) {
                Location loc = new Location();
                loc.setX(Integer.parseInt(locXField.getText().trim()));
                loc.setY(Float.parseFloat(locYField.getText().trim()));
                loc.setZ(Double.parseDouble(locZField.getText().trim()));
                author.setLocation(loc);
            }

            lw.setAuthor(author);
            this.result = lw;
            dispose();
        } catch (NumberFormatException e) {
            showError("msg.invalid.input");
        }
    }

    private void showError(String key) {
        JOptionPane.showMessageDialog(this,
            LocalizationManager.getInstance().getString(key),
            getMsg("msg.error"), JOptionPane.ERROR_MESSAGE);
    }

    public LabWork getResult() { return result; }
}