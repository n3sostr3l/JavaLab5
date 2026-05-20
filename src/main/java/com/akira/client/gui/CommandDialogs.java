package com.akira.client.gui;

import com.akira.client.NetworkManager;
import com.akira.client.PasswordEncryptor;
import com.akira.client.UserRegisty;
import com.akira.client.security.Sha224HashStrategy;
import com.akira.client.reader.DateParser;
import com.akira.general.datas.Coordinates;
import com.akira.general.datas.LabWork;
import com.akira.general.datas.Location;
import com.akira.general.datas.Person;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Диалоговые окна для всех команд приложения.
 * Использует DialogUtil.modal() для единого стиля модальных окон.
 */
public class CommandDialogs {

    private static final Set<String> SCRIPT_STACK = new HashSet<>();

    private static String getMsg(String key) {
        return LocalizationManager.getInstance().getString(key);
    }

    private static void attachAuth(Request req) {
        UserRegisty ur = UserRegisty.getInstance();
        if (ur.getUserLogin() != null && ur.getPasswordHash() != null) {
            req.setLogin(ur.getUserLogin());
            req.setPasswordHash(ur.getPasswordHash());
        }
    }

    private static Response sendCheck(String cmd, String ident, NetworkManager network) {
        Request req = new Request("check", new ArrayList<>(Arrays.asList(cmd, ident)), true);
        attachAuth(req);
        return network.sendAndReceive(req);
    }

    private static void showCheckError(JFrame parent, String cmd, String ident, Response resp) {
        String detail = resp != null ? resp.getMessage() : getMsg("msg.no.connection");
        String msg = LocalizationManager.getInstance().format("msg.check.failed", cmd, ident, detail);
        DialogUtil.showError(parent, msg, getMsg("msg.error"));
    }

    // ======================== OBJECT INFO DIALOG (Labwork #id) ========================

    /**
     * Opens the "Labwork #id" dialog with properties, Edit (YES) and Delete (NO) buttons.
     */
    public static void showObjectInfoDialog(JFrame parent, NetworkManager network, LabWork lw, Integer key) {
        StringBuilder sb = new StringBuilder("<html><div style='text-align:left;'>");
        sb.append("<b>ID:</b> ").append(lw.getId()).append("<br>");
        sb.append("<b>Name:</b> ").append(lw.getName() != null ? lw.getName() : "").append("<br>");
        if (lw.getCoordinates() != null) {
            sb.append("<b>X:</b> ").append(lw.getCoordinates().getX())
              .append(", <b>Y:</b> ").append(lw.getCoordinates().getY()).append("<br>");
        }
        sb.append("<b>Max Point:</b> ").append(lw.getMaximumPoint()).append("<br>");
        if (lw.getMinimalPoint() != null)
            sb.append("<b>Min Point:</b> ").append(lw.getMinimalPoint()).append("<br>");
        if (lw.getDifficulty() != null)
            sb.append("<b>Difficulty:</b> ").append(lw.getDifficulty()).append("<br>");
        if (lw.getDescription() != null)
            sb.append("<b>Description:</b> ").append(lw.getDescription()).append("<br>");
        if (lw.getAuthor() != null) {
            sb.append("<b>Author:</b> ").append(lw.getAuthor().getName()).append("<br>");
        }
        if (lw.getOwnerLogin() != null)
            sb.append("<b>Owner:</b> ").append(lw.getOwnerLogin());
        sb.append("</div></html>");

        JLabel body = new JLabel(sb.toString());
        body.setFont(UIStyle.FONT);
        body.setForeground(UIStyle.TEXT);

        String title = LocalizationManager.getInstance().format("dialog.object.title", lw.getId());

        JButton editBtn = DialogUtil.createYesButton(getMsg("dialog.edit"));
        JButton delBtn = DialogUtil.createNoButton(getMsg("dialog.delete"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(UIStyle.ACCENT)
            .body(body)
            .buttons(editBtn, delBtn)
            .size(440, 360)
            .build();

        editBtn.addActionListener(e -> {
            dialog.dispose();
            editSelected(parent, network, lw);
            if (parent instanceof MainFrame) ((MainFrame) parent).loadCollection();
        });

        delBtn.addActionListener(e -> {
            dialog.dispose();
            // Show delete confirmation dialog
            showDeleteConfirmDialog(parent, network, lw, key);
            if (parent instanceof MainFrame) ((MainFrame) parent).loadCollection();
        });

        dialog.setVisible(true);
    }

    // ======================== DELETE CONFIRMATION DIALOG ========================

    /**
     * "Удаление объекта" — red title, centered red text, Keep(YES)/Delete(NO).
     */
    private static void showDeleteConfirmDialog(JFrame parent, NetworkManager network, LabWork lw, Integer key) {
        JLabel msgLabel = new JLabel("<html><div style='text-align:center;color:#D7817E;font-size:14px;'>"
            + getMsg("msg.confirm.delete") + "</div></html>");
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton keepBtn = DialogUtil.createYesButton(getMsg("dialog.delete.keep"));
        JButton delBtn = DialogUtil.createNoButton(getMsg("dialog.delete.delete"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(getMsg("dialog.delete.title")).titleColor(UIStyle.NO_BORDER)
            .body(msgLabel)
            .buttons(keepBtn, delBtn)
            .size(420, 240)
            .build();

        keepBtn.addActionListener(e -> dialog.dispose());

        delBtn.addActionListener(e -> {
            dialog.dispose();
            Request req = new Request("remove_key", new ArrayList<>(Arrays.asList(String.valueOf(key))));
            attachAuth(req);
            showResult(parent, network.sendAndReceive(req));
        });

        dialog.setVisible(true);
    }

    // ======================== COLLECTION INFO DIALOG ========================

    /**
     * "Информация о коллекции" — green title, centered text, OK button.
     */
    public static void showCollectionInfo(JFrame parent, NetworkManager network) {
        Request req = new Request("info");
        attachAuth(req);
        Response resp = network.sendAndReceive(req);

        String text = resp != null && resp.getMessage() != null
            ? resp.getMessage() : getMsg("msg.no.connection");

        JLabel msgLabel = new JLabel("<html><div style='text-align:center;'>" + text + "</div></html>");
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setForeground(UIStyle.TEXT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton okBtn = DialogUtil.createYesButton(getMsg("dialog.info.ok"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(getMsg("dialog.info.title")).titleColor(UIStyle.YES_BORDER)
            .body(msgLabel)
            .buttons(okBtn)
            .size(440, 280)
            .build();

        okBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ======================== EXECUTE SCRIPT DIALOG ========================

    /**
     * "Исполнение файла" — cyan title, centered prompt + input, Cancel(YES)/Execute(NO).
     */
    public static void executeScriptDialog(JFrame parent, NetworkManager network) {
        JPanel inputPanel = new JPanel(new BorderLayout(0, UIStyle.scale(12)));
        inputPanel.setBackground(UIStyle.BACKGROUND);

        JLabel promptLabel = new JLabel(getMsg("dialog.file.prompt"), SwingConstants.CENTER);
        promptLabel.setForeground(UIStyle.TEXT);
        promptLabel.setFont(UIStyle.FONT);
        inputPanel.add(promptLabel, BorderLayout.NORTH);

        JTextField fileField = new JTextField(20);
        fileField.setBackground(UIStyle.PANEL);
        fileField.setForeground(UIStyle.TEXT);
        fileField.setCaretColor(UIStyle.TEXT);
        fileField.setFont(UIStyle.FONT);
        fileField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.BORDER),
            BorderFactory.createEmptyBorder(UIStyle.scale(6), UIStyle.scale(8),
                UIStyle.scale(6), UIStyle.scale(8))));
        fileField.putClientProperty("JTextField.placeholderText", getMsg("dialog.file.placeholder"));
        inputPanel.add(fileField, BorderLayout.SOUTH);

        JButton cancelBtn = DialogUtil.createNoButton(getMsg("dialog.cancel"));
        JButton execBtn = DialogUtil.createYesButton(getMsg("dialog.file.title"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(getMsg("dialog.file.title")).titleColor(UIStyle.ACCENT)
            .body(inputPanel)
            .buttons(cancelBtn, execBtn)
            .size(420, 240)
            .build();

        cancelBtn.addActionListener(e -> dialog.dispose());
        execBtn.addActionListener(e -> {
            String fileName = fileField.getText().trim();
            if (!fileName.isEmpty()) {
                dialog.dispose();
                runScript(parent, network, new File(fileName));
            }
        });

        SwingUtilities.invokeLater(fileField::requestFocus);
        dialog.setVisible(true);
    }

    // ======================== ADD RANDOM DIALOG ========================

    /**
     * "Добавить случайные лабораторные" — light title, centered prompt + input.
     */
    public static void addRandom(JFrame parent, NetworkManager network) {
        JPanel inputPanel = new JPanel(new BorderLayout(0, UIStyle.scale(12)));
        inputPanel.setBackground(UIStyle.BACKGROUND);

        JLabel promptLabel = new JLabel(getMsg("dialog.random.prompt"), SwingConstants.CENTER);
        promptLabel.setForeground(UIStyle.TEXT);
        promptLabel.setFont(UIStyle.FONT);
        inputPanel.add(promptLabel, BorderLayout.NORTH);

        JTextField countField = new JTextField(20);
        countField.setBackground(UIStyle.PANEL);
        countField.setForeground(UIStyle.TEXT);
        countField.setCaretColor(UIStyle.TEXT);
        countField.setFont(UIStyle.FONT);
        countField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyle.BORDER),
            BorderFactory.createEmptyBorder(UIStyle.scale(6), UIStyle.scale(8),
                UIStyle.scale(6), UIStyle.scale(8))));
        countField.putClientProperty("JTextField.placeholderText", getMsg("dialog.random.placeholder"));
        inputPanel.add(countField, BorderLayout.SOUTH);

        JButton cancelBtn = DialogUtil.createNoButton(getMsg("dialog.cancel"));
        JButton addBtn = DialogUtil.createYesButton(getMsg("dialog.add"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(getMsg("dialog.random.title")).titleColor(UIStyle.TEXT)
            .body(inputPanel)
            .buttons(cancelBtn, addBtn)
            .size(420, 240)
            .build();

        cancelBtn.addActionListener(e -> dialog.dispose());
        addBtn.addActionListener(e -> {
            String val = countField.getText().trim();
            dialog.dispose();
            if (val.isEmpty()) return;
            try {
                int count = Integer.parseInt(val);
                if (count < 0) {
                    DialogUtil.showError(parent, getMsg("msg.invalid.input"), getMsg("msg.error"));
                    return;
                }
                Request req = new Request("add_random", new ArrayList<>(Arrays.asList(String.valueOf(count))));
                attachAuth(req);
                showResult(parent, network.sendAndReceive(req));
            } catch (NumberFormatException ex) {
                DialogUtil.showError(parent, getMsg("msg.invalid.input"), getMsg("msg.error"));
            }
        });

        SwingUtilities.invokeLater(countField::requestFocus);
        dialog.setVisible(true);
    }

    // ======================== INSERT ========================

    public static LabWork insertElement(JFrame parent, NetworkManager network) {
        String key = DialogUtil.showInput(parent, getMsg("dialog.key"), getMsg("cmd.insert"));
        if (key == null || key.trim().isEmpty()) return null;

        Response checkResp = sendCheck("insert", key.trim(), network);
        if (checkResp == null) {
            showCheckError(parent, "insert", key.trim(), null);
            return null;
        }
        if (checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "insert", key.trim(), checkResp);
            return null;
        }
        if (!checkResp.isSuccess()) {
            showCheckError(parent, "insert", key.trim(), checkResp);
            return null;
        }
        if (checkResp.getMessage().contains("занят")) {
            if (!DialogUtil.showConfirm(parent, "Ключ " + key + " уже занят. Перезаписать?", getMsg("cmd.insert"))) {
                DialogUtil.showMessage(parent, getMsg("dialog.cancel"), getMsg("cmd.insert"));
                return null;
            }
        }

        LabWorkDialog dialog = new LabWorkDialog(parent, getMsg("cmd.insert"));
        dialog.setVisible(true);
        LabWork lw = dialog.getResult();
        if (lw == null) return null;

        Request req = new Request("insert", new ArrayList<>(Arrays.asList(key.trim())), lw);
        attachAuth(req);
        Response resp = network.sendAndReceive(req);
        showResult(parent, resp);
        return (resp != null && resp.isSuccess()) ? lw : null;
    }

    // ======================== UPDATE ========================

    public static void updateElement(JFrame parent, NetworkManager network) {
        String id = DialogUtil.showInput(parent, "ID элемента:", getMsg("cmd.update"));
        if (id == null || id.trim().isEmpty()) return;

        Response checkResp = sendCheck("update", id.trim(), network);
        if (checkResp == null) {
            showCheckError(parent, "update", id.trim(), null);
            return;
        }
        if (!checkResp.isSuccess()) {
            showCheckError(parent, "update", id.trim(), checkResp);
            return;
        }
        if (checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "update", id.trim(), checkResp);
            return;
        }
        if (checkResp.getMessage().contains("свободен")) {
            showCheckError(parent, "update", id.trim(), checkResp);
            return;
        }

        LabWorkDialog dialog = new LabWorkDialog(parent, getMsg("cmd.update"));
        dialog.setVisible(true);
        LabWork lw = dialog.getResult();
        if (lw == null) return;

        Request req = new Request("update", new ArrayList<>(Arrays.asList(id.trim())), lw);
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== REMOVE ========================

    public static void removeElement(JFrame parent, NetworkManager network) {
        String key = DialogUtil.showInput(parent, getMsg("dialog.key"), getMsg("cmd.remove"));
        if (key == null || key.trim().isEmpty()) return;

        Response checkResp = sendCheck("remove_key", key.trim(), network);
        if (checkResp == null) {
            showCheckError(parent, "remove_key", key.trim(), null);
            return;
        }
        if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "remove_key", key.trim(), checkResp);
            return;
        }
        if (checkResp.getMessage().contains("свободен")) {
            showCheckError(parent, "remove_key", key.trim(), checkResp);
            return;
        }

        // Use styled delete confirmation
        showDeleteKeyConfirm(parent, key.trim(), network);
    }

    private static void showDeleteKeyConfirm(JFrame parent, String key, NetworkManager network) {
        JLabel msgLabel = new JLabel("<html><div style='text-align:center;color:#D7817E;font-size:14px;'>"
            + getMsg("msg.confirm.delete") + "</div></html>");
        msgLabel.setFont(UIStyle.FONT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton keepBtn = DialogUtil.createYesButton(getMsg("dialog.delete.keep"));
        JButton delBtn = DialogUtil.createNoButton(getMsg("dialog.delete.delete"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(getMsg("dialog.delete.title")).titleColor(UIStyle.NO_BORDER)
            .body(msgLabel)
            .buttons(keepBtn, delBtn)
            .size(420, 240)
            .build();

        keepBtn.addActionListener(e -> dialog.dispose());
        delBtn.addActionListener(e -> {
            dialog.dispose();
            Request req = new Request("remove_key", new ArrayList<>(Arrays.asList(key)));
            attachAuth(req);
            showResult(parent, network.sendAndReceive(req));
        });

        dialog.setVisible(true);
    }

    // ======================== REMOVE SELECTED ========================

    public static void removeSelected(JFrame parent, NetworkManager network, LabWork selected, Integer key) {
        if (selected == null || key == null) {
            DialogUtil.showMessage(parent, getMsg("msg.select.item"), getMsg("msg.error"));
            return;
        }

        Response checkResp = sendCheck("remove_key", String.valueOf(key), network);
        if (checkResp == null) {
            showCheckError(parent, "remove_key", String.valueOf(key), null);
            return;
        }
        if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "remove_key", String.valueOf(key), checkResp);
            return;
        }

        showDeleteKeyConfirm(parent, String.valueOf(key), network);
    }

    // ======================== EDIT SELECTED ========================

    public static void editSelected(JFrame parent, NetworkManager network, LabWork selected) {
        if (selected == null) {
            JOptionPane.showMessageDialog(parent, getMsg("msg.select.item"));
            return;
        }

        String id = String.valueOf(selected.getId());
        Response checkResp = sendCheck("update", id, network);
        if (checkResp == null) {
            showCheckError(parent, "update", id, null);
            return;
        }
        if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "update", id, checkResp);
            return;
        }

        LabWorkDialog dialog = new LabWorkDialog(parent, getMsg("cmd.update"), selected);
        dialog.setVisible(true);
        LabWork lw = dialog.getResult();
        if (lw == null) return;

        Request req = new Request("update", new ArrayList<>(Arrays.asList(id)), lw);
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== CLEAR ========================

    public static void clearCollection(JFrame parent, NetworkManager network) {
        if (!DialogUtil.showConfirm(parent, getMsg("msg.confirm.clear"), getMsg("cmd.clear"))) return;
        Request req = new Request("clear");
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== REMOVE LOWER ========================

    public static void removeLowerElements(JFrame parent, NetworkManager network) {
        String key = DialogUtil.showInput(parent, getMsg("dialog.key"), getMsg("cmd.remove_lower"));
        if (key == null || key.trim().isEmpty()) return;

        Request req = new Request("remove_lower_key", new ArrayList<>(Arrays.asList(key.trim())));
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== REPLACE IF GREATER ========================

    public static void replaceIfGreater(JFrame parent, NetworkManager network) {
        String key = DialogUtil.showInput(parent, getMsg("dialog.key"), getMsg("cmd.replace_greater"));
        if (key == null || key.trim().isEmpty()) return;

        Response checkResp = sendCheck("replace_if_greater", key.trim(), network);
        if (checkResp == null) {
            showCheckError(parent, "replace_if_greater", key.trim(), null);
            return;
        }
        if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "replace_if_greater", key.trim(), checkResp);
            return;
        }
        if (checkResp.getMessage().contains("свободен")) {
            showCheckError(parent, "replace_if_greater", key.trim(), checkResp);
            return;
        }

        LabWorkDialog dialog = new LabWorkDialog(parent, getMsg("cmd.replace_greater"));
        dialog.setVisible(true);
        LabWork lw = dialog.getResult();
        if (lw == null) return;

        Request req = new Request("replace_if_greater", new ArrayList<>(Arrays.asList(key.trim())), lw);
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== REPLACE IF LOWER ========================

    public static void replaceIfLower(JFrame parent, NetworkManager network) {
        String key = DialogUtil.showInput(parent, getMsg("dialog.key"), getMsg("cmd.replace_lower"));
        if (key == null || key.trim().isEmpty()) return;

        Response checkResp = sendCheck("replace_if_lower", key.trim(), network);
        if (checkResp == null) {
            showCheckError(parent, "replace_if_lower", key.trim(), null);
            return;
        }
        if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
            showCheckError(parent, "replace_if_lower", key.trim(), checkResp);
            return;
        }
        if (checkResp.getMessage().contains("свободен")) {
            showCheckError(parent, "replace_if_lower", key.trim(), checkResp);
            return;
        }

        LabWorkDialog dialog = new LabWorkDialog(parent, getMsg("cmd.replace_lower"));
        dialog.setVisible(true);
        LabWork lw = dialog.getResult();
        if (lw == null) return;

        Request req = new Request("replace_if_lower", new ArrayList<>(Arrays.asList(key.trim())), lw);
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== GROUP COUNTING ========================

    public static void groupCountingByMaximumPoint(JFrame parent, NetworkManager network) {
        Request req = new Request("group_counting_by_maximum_point");
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== PRINT DIFFICULTY ========================

    public static void printFieldDescendingDifficulty(JFrame parent, NetworkManager network) {
        Request req = new Request("print_field_descending_difficulty");
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== UNIQUE AUTHOR ========================

    public static void uniqueAuthor(JFrame parent, NetworkManager network) {
        Request req = new Request("print_unique_author");
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== EXECUTE SCRIPT (file chooser path) ========================

    public static void executeScript(JFrame parent, NetworkManager network) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(getMsg("cmd.execute_script"));
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        runScript(parent, network, file);
    }

    private static void runScript(JFrame parent, NetworkManager network, File file) {
        String absPath;
        try {
            absPath = file.getAbsolutePath();
        } catch (Exception e) {
            DialogUtil.showError(parent, getMsg("msg.script.not_found"), getMsg("msg.error"));
            return;
        }

        if (SCRIPT_STACK.contains(absPath)) {
            DialogUtil.showError(parent, getMsg("msg.script.recursion"), getMsg("msg.error"));
            return;
        }
        SCRIPT_STACK.add(absPath);

        StringBuilder log = new StringBuilder();
        int executed = 0;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] tokens = line.split("\\s+");
                String cmd = tokens[0].toLowerCase();
                ArrayList<String> args = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

                if (cmd.equals("exit")) {
                    log.append("exit: skipped in script\n");
                    continue;
                }
                if (cmd.equals("execute_script")) {
                    if (args.isEmpty()) {
                        log.append("execute_script: no file specified\n");
                        continue;
                    }
                    runScript(parent, network, new File(args.get(0)));
                    continue;
                }

                Request req = buildScriptRequest(cmd, args, sc, log, network);
                if (req == null) continue;

                attachAuth(req);
                Response resp = network.sendAndReceive(req);
                if (resp != null) {
                    executed++;
                    log.append(cmd).append(": ").append(resp.getMessage()).append("\n");
                } else {
                    log.append(cmd).append(": ").append(getMsg("msg.no.connection")).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            DialogUtil.showError(parent, getMsg("msg.script.not_found"), getMsg("msg.error"));
        } finally {
            SCRIPT_STACK.remove(absPath);
        }

        String finalMsg = getMsg("msg.script.done") + " " + executed + "\n\n" + log.toString();
        DialogUtil.showMessage(parent, finalMsg, getMsg("cmd.execute_script"));
    }

    private static Request buildScriptRequest(String cmd, ArrayList<String> args, Scanner sc,
                                              StringBuilder log, NetworkManager network) {
        Set<String> objectCommands = new HashSet<>(Arrays.asList(
            "insert", "update", "replace_if_greater", "replace_if_lower"));

        if (objectCommands.contains(cmd)) {
            if (args.size() != 1) {
                log.append(cmd).append(": invalid argument count\n");
                return null;
            }
            Response checkResp = sendCheck(cmd, args.get(0), network);
            if (checkResp == null) {
                log.append(cmd).append(": ").append(getMsg("msg.no.connection")).append("\n");
                return null;
            }
            if (!checkResp.isSuccess() || checkResp.getMessage().contains("Элемент не принадлежит вашему логину")) {
                log.append(cmd).append(": ").append(checkResp.getMessage()).append("\n");
                return null;
            }
            if (!cmd.equals("insert") && checkResp.getMessage().contains("свободен")) {
                log.append(cmd).append(": ").append(checkResp.getMessage()).append("\n");
                return null;
            }
            if (cmd.equals("insert") && checkResp.getMessage().contains("занят")) {
                log.append(cmd).append(": key already exists, skipped\n");
                return null;
            }

            LabWork lw = readLabWorkFromScript(sc, log);
            if (lw == null) return null;
            return new Request(cmd, args, lw);
        }

        Request req = new Request(cmd, args);
        if (cmd.equals("login") || cmd.equals("reg")) {
            if (args.size() < 2) {
                log.append(cmd).append(": login or password missing\n");
                return null;
            }
            String login = args.get(0);
            String pass = args.get(1);
            String hash = PasswordEncryptor.getInstance().getPasswordHash(pass);
            req.setLogin(login);
            req.setPasswordHash(hash);
            UserRegisty.getInstance().setUserLogin(login).setPasswordHash(hash);
        }

        return req;
    }

    private static LabWork readLabWorkFromScript(Scanner sc, StringBuilder log) {
        try {
            LabWork lw = new LabWork();
            String name = nextLine(sc);
            if (name == null || name.trim().isEmpty()) {
                log.append("LabWork: name missing\n");
                return null;
            }
            lw.setName(name.trim());

            String xLine = nextLine(sc);
            String yLine = nextLine(sc);
            if (xLine == null || yLine == null) {
                log.append("LabWork: coordinates missing\n");
                return null;
            }
            Coordinates coords = new Coordinates();
            coords.setX(Integer.parseInt(xLine.trim()));
            coords.setY(Long.parseLong(yLine.trim()));
            lw.setCoordinates(coords);

            String minLine = nextLine(sc);
            if (minLine != null && !minLine.trim().isEmpty()) {
                float minVal = Float.parseFloat(minLine.trim());
                if (minVal <= 0) {
                    log.append("LabWork: minimalPoint invalid\n");
                    return null;
                }
                lw.setMinimalPoint(minVal);
            }

            String maxLine = nextLine(sc);
            if (maxLine == null || maxLine.trim().isEmpty()) {
                log.append("LabWork: maximumPoint missing\n");
                return null;
            }
            long maxVal = Long.parseLong(maxLine.trim());
            if (maxVal <= 0) {
                log.append("LabWork: maximumPoint invalid\n");
                return null;
            }
            lw.setMaximumPoint(maxVal);

            String desc = nextLine(sc);
            if (desc != null && !desc.trim().isEmpty()) lw.setDescription(desc.trim());

            String diff = nextLine(sc);
            if (diff != null && !diff.trim().isEmpty()) {
                lw.setDifficulty(com.akira.general.datas.Difficulty.valueOf(diff.trim().toUpperCase()));
            }

            Person author = new Person();
            String authorName = nextLine(sc);
            if (authorName == null || authorName.trim().isEmpty()) {
                log.append("LabWork: author name missing\n");
                return null;
            }
            author.setName(authorName.trim());

            String birthdayLine = nextLine(sc);
            if (birthdayLine != null && !birthdayLine.trim().isEmpty()) {
                author.setBirthday(DateParser.parseAndValidate(birthdayLine.trim()));
            }

            String locAnswer = nextLine(sc);
            if (locAnswer != null && (locAnswer.equalsIgnoreCase("y") || locAnswer.equalsIgnoreCase("yes"))) {
                String locX = nextLine(sc);
                String locY = nextLine(sc);
                String locZ = nextLine(sc);
                if (locX == null || locY == null || locZ == null) {
                    log.append("LabWork: location incomplete\n");
                    return null;
                }
                Location loc = new Location();
                loc.setX(Integer.parseInt(locX.trim()));
                loc.setY(Float.parseFloat(locY.trim()));
                loc.setZ(Double.parseDouble(locZ.trim()));
                author.setLocation(loc);
            }

            lw.setAuthor(author);
            return lw;
        } catch (Exception e) {
            log.append("LabWork: parse error\n");
            return null;
        }
    }

    private static String nextLine(Scanner sc) {
        if (!sc.hasNextLine()) return null;
        return sc.nextLine();
    }

    // ======================== HELP ========================

    public static void showHelp(JFrame parent, NetworkManager network) {
        Request req = new Request("help");
        attachAuth(req);
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== CHANGE PASSWORD ========================

    public static void changePassword(JFrame parent, NetworkManager network) {
        String login = UserRegisty.getInstance().getUserLogin();
        if (login == null) return;

        String newPass = DialogUtil.showInput(parent, "Введите новый пароль:", getMsg("cmd.change_password"));
        if (newPass == null || newPass.trim().isEmpty()) return;

        String confirm = DialogUtil.showInput(parent, "Подтвердите новый пароль:", getMsg("cmd.change_password"));
        if (!newPass.equals(confirm)) {
            DialogUtil.showError(parent, "Пароли не совпадают.", getMsg("msg.error"));
            return;
        }

        String newHash = new Sha224HashStrategy().hash(newPass);
        Request req = new Request("reset_pwd", new ArrayList<>(Arrays.asList(login, newHash)));
        req.setLogin(login);
        req.setPasswordHash(UserRegisty.getInstance().getPasswordHash());
        showResult(parent, network.sendAndReceive(req));
    }

    // ======================== HELPER: showResult ========================

    static void showResult(JFrame parent, Response resp) {
        if (resp != null && resp.getMessage() != null && resp.getMessage().length() > 200) {
            showScrollableResult(parent, resp.getMessage(),
                resp.isSuccess() ? getMsg("msg.success") : getMsg("msg.error"),
                resp.isSuccess());
        } else if (resp != null) {
            if (resp.isSuccess()) {
                DialogUtil.showMessage(parent, resp.getMessage(), getMsg("msg.success"));
            } else {
                DialogUtil.showError(parent, resp.getMessage(), getMsg("msg.error"));
            }
        } else {
            DialogUtil.showError(parent, getMsg("msg.no.connection"), getMsg("msg.error"));
        }
    }

    private static void showScrollableResult(JFrame parent, String message, String title, boolean isSuccess) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(UIStyle.BACKGROUND);
        textArea.setForeground(UIStyle.TEXT);
        textArea.setFont(UIStyle.FONT);
        textArea.setMargin(new Insets(UIStyle.scale(8), UIStyle.scale(8), UIStyle.scale(8), UIStyle.scale(8)));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.getViewport().setBackground(UIStyle.BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));

        JButton closeBtn = DialogUtil.createYesButton(getMsg("dialog.close"));

        JDialog dialog = DialogUtil.modal(parent)
            .title(title).titleColor(isSuccess ? UIStyle.YES_BORDER : UIStyle.NO_BORDER)
            .body(scrollPane)
            .buttons(closeBtn)
            .size(600, 400)
            .build();

        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}