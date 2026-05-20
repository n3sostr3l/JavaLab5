package com.akira.client.gui;

import com.akira.general.datas.LabWork;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Панель визуализации объектов коллекции.
 * Рисует объекты с помощью Graphics2D с кастомной отрисовкой:
 * оси с центра, крестики по углам, плоские фигуры без обводок.
 */
public class VisualizationPanel extends JPanel {
    private List<LabWork> items = new ArrayList<>();
    private LabWork selectedItem = null;
    private Runnable onItemSelected;
    private LabWork hoverItem = null;
    private Consumer<LabWork> onItemEdit;
    private Consumer<LabWork> onItemDelete;
    private String currentLogin;

    private boolean isPanning = false;
    private Point lastDragPoint;
    private double panX = 0.0;
    private double panY = 0.0;
    private double zoom = 1.0;

    private final Map<Long, Float> appearanceScales = new HashMap<>();
    private javax.swing.Timer animationTimer;

    private final java.util.Map<String, Color> userColors = new HashMap<>();

    private final JPopupMenu contextMenu = new JPopupMenu();
    private final JMenuItem editItem = new JMenuItem("Edit");
    private final JMenuItem deleteItem = new JMenuItem("Delete");
    private LabWork contextItem;

    public VisualizationPanel() {
        setBackground(UIStyle.PANEL);
        setBorder(BorderFactory.createLineBorder(UIStyle.BORDER));
        setToolTipText("");

        editItem.addActionListener(e -> {
            if (contextItem != null && onItemEdit != null) onItemEdit.accept(contextItem);
        });
        deleteItem.addActionListener(e -> {
            if (contextItem != null && onItemDelete != null) onItemDelete.accept(contextItem);
        });
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LabWork clicked = findItemAt(e.getX(), e.getY());
                if (clicked != null) {
                    selectedItem = clicked;
                    repaint();
                    if (onItemSelected != null) onItemSelected.run();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    LabWork hit = findItemAt(e.getX(), e.getY());
                    if (hit == null) {
                        isPanning = true;
                        lastDragPoint = e.getPoint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
                isPanning = false;
                lastDragPoint = null;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                LabWork h = findItemAt(e.getX(), e.getY());
                if (h != hoverItem) {
                    hoverItem = h;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isPanning || lastDragPoint == null) return;
                Point p = e.getPoint();
                panX += (p.x - lastDragPoint.x);
                panY += (p.y - lastDragPoint.y);
                lastDragPoint = p;
                repaint();
            }
        });

        addMouseWheelListener(this::handleZoom);

        animationTimer = new javax.swing.Timer(30, ev -> {
            boolean anyRunning = false;
            Iterator<Map.Entry<Long, Float>> it = appearanceScales.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Float> en = it.next();
                float v = en.getValue() + 0.08f;
                if (v >= 1.0f) {
                    it.remove();
                } else {
                    en.setValue(v);
                    anyRunning = true;
                }
            }
            repaint();
            if (!anyRunning && appearanceScales.isEmpty()) animationTimer.stop();
        });
    }

    public void setItems(List<LabWork> items) {
        this.items = new ArrayList<>(items);
        if (selectedItem != null && !containsItem(selectedItem)) {
            selectedItem = null;
        }
        if (hoverItem != null && !containsItem(hoverItem)) {
            hoverItem = null;
        }
        repaint();
    }

    public void setCurrentLogin(String login) {
        this.currentLogin = login;
    }

    public void setOnItemEdit(Consumer<LabWork> onItemEdit) {
        this.onItemEdit = onItemEdit;
    }

    public void setOnItemDelete(Consumer<LabWork> onItemDelete) {
        this.onItemDelete = onItemDelete;
    }

    private boolean containsItem(LabWork item) {
        if (item == null || item.getId() == null) return false;
        for (LabWork lw : items) {
            if (lw != null && lw.getId() != null && lw.getId().equals(item.getId())) return true;
        }
        return false;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        LabWork item = findItemAt(event.getX(), event.getY());
        if (item == null) return null;
        String ownerLabel = LocalizationManager.getInstance().getString("main.owner");
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("ID: ").append(item.getId());
        if (item.getName() != null) sb.append("<br>").append(item.getName());
        if (item.getOwnerLogin() != null) sb.append("<br>").append(ownerLabel).append(" ").append(item.getOwnerLogin());
        sb.append("</html>");
        return sb.toString();
    }

    public void setOnItemSelected(Runnable r) {
        this.onItemSelected = r;
    }

    public LabWork getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(LabWork item) {
        this.selectedItem = item;
        repaint();
    }

    private Color getColorForUser(String login) {
        if (login == null) login = "default";
        return userColors.computeIfAbsent(login, VisualizationPanel::generateUserColor);
    }

    private static Color generateUserColor(String login) {
        int hash = login.hashCode();
        float hue = (hash & 0xFFFF) / 65535f;
        float saturation = 0.65f;
        float brightness = 0.85f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    /** Get shape type based on owner: 0=pink circle(own), 1=green rect, 2=blue diamond */
    private int getShapeType(String login) {
        if (login == null) return 1;
        if (currentLogin != null && login.equals(currentLogin)) return 0;
        int hash = Math.abs(login.hashCode());
        return 1 + (hash % 2);
    }

    private void showContextMenu(MouseEvent e) {
        LabWork item = findItemAt(e.getX(), e.getY());
        if (item == null) return;
        if (currentLogin == null || item.getOwnerLogin() == null || !currentLogin.equals(item.getOwnerLogin())) {
            return;
        }
        contextItem = item;
        editItem.setText(LocalizationManager.getInstance().getString("dialog.edit"));
        deleteItem.setText(LocalizationManager.getInstance().getString("dialog.delete"));
        contextMenu.show(this, e.getX(), e.getY());
    }

    private void handleZoom(MouseWheelEvent e) {
        double oldZoom = zoom;
        double factor = Math.pow(1.1, -e.getWheelRotation());
        zoom = Math.max(0.5, Math.min(3.0, zoom * factor));
        if (Math.abs(zoom - oldZoom) < 0.001) return;
        double wx = (e.getX() - panX) / oldZoom;
        double wy = (e.getY() - panY) / oldZoom;
        panX = e.getX() - wx * zoom;
        panY = e.getY() - wy * zoom;
        repaint();
    }

    private LabWork findItemAt(int x, int y) {
        for (LabWork item : items) {
            if (item.getCoordinates() == null) continue;
            int cx = mapX(item);
            int cy = mapY(item);
            int size = mapSize(item);
            int pad = 6;
            int scaled = Math.max(5, (int) Math.round(size * zoom));
            Rectangle r = new Rectangle(cx - scaled / 2 - pad, cy - scaled / 2 - pad, scaled + pad * 2, scaled + pad * 2);
            if (r.contains(x, y)) return item;
        }
        return null;
    }

    private double getCoordScale() {
        int size = Math.min(getWidth(), getHeight());
        return Math.max(0.3, size / 2000.0);
    }

    private double baseX(LabWork item) {
        int cx = getWidth() / 2;
        int x = item.getCoordinates().getX();
        return cx + x * getCoordScale();
    }

    private double baseY(LabWork item) {
        int cy = getHeight() / 2;
        long y = item.getCoordinates().getY();
        return cy - y * getCoordScale(); // negate so +Y is up
    }

    private int mapX(LabWork item) {
        return (int) Math.round(baseX(item) * zoom + panX);
    }

    private int mapY(LabWork item) {
        return (int) Math.round(baseY(item) * zoom + panY);
    }

    private int mapSize(LabWork item) {
        long mp = item.getMaximumPoint();
        return Math.max(20, Math.min(80, (int) (mp / 10.0)));
    }

    public void animateAppearance(LabWork item, Runnable onFinish) {
        if (item.getCoordinates() == null) {
            if (onFinish != null) onFinish.run();
            return;
        }
        Long id = item.getId();
        if (id == null) {
            if (onFinish != null) onFinish.run();
            return;
        }
        appearanceScales.put(id, 0.0f);
        if (!animationTimer.isRunning()) animationTimer.start();
        if (onFinish != null) {
            new javax.swing.Timer(500, ev -> {
                ((javax.swing.Timer) ev.getSource()).stop();
                onFinish.run();
            }).start();
        }
    }

    // ======================== DRAWING ========================

    private void drawAxes(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int arrowSize = 10;

        // Origin at static pixel center (not affected by pan/zoom)
        int ox = w / 2;
        int oy = h / 2;

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(180, 190, 210));

        // Horizontal axis through origin
        g2.drawLine(0, oy, w, oy);
        // Arrow right
        g2.drawLine(w, oy, w - arrowSize, oy - arrowSize / 2);
        g2.drawLine(w, oy, w - arrowSize, oy + arrowSize / 2);

        // Vertical axis through origin
        g2.drawLine(ox, 0, ox, h);
        // Arrow up
        g2.drawLine(ox, 0, ox - arrowSize / 2, arrowSize);
        g2.drawLine(ox, 0, ox + arrowSize / 2, arrowSize);

        // Labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, UIStyle.scale(11)));
        g2.drawString("X", w - UIStyle.scale(14), oy - UIStyle.scale(6));
        g2.drawString("Y", ox + UIStyle.scale(6), UIStyle.scale(14));
    }

    private void drawCrossMarkers(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int margin = 12;
        int size = 8;
        g2.setColor(UIStyle.ACCENT);
        g2.setStroke(new BasicStroke(1.5f));

        // Corners
        int[][] positions = {
            {margin, margin}, {w - margin, margin},
            {margin, h - margin}, {w - margin, h - margin},
            {w / 2, margin}, {w / 2, h - margin},
            {margin, h / 2}, {w - margin, h / 2}
        };

        for (int[] pos : positions) {
            int px = pos[0];
            int py = pos[1];
            g2.drawLine(px - size, py, px + size, py);
            g2.drawLine(px, py - size, px, py + size);
        }
    }

    private void drawItem(Graphics2D g2, LabWork item, int cx, int cy, int size, float scale) {
        int scaledSize = Math.max(5, (int) Math.round(size * scale * zoom));
        int sx = cx - scaledSize / 2;
        int sy = cy - scaledSize / 2;

        Color color = getColorForUser(item.getOwnerLogin());
        int shapeType = getShapeType(item.getOwnerLogin());

        boolean isSelected = (selectedItem != null && selectedItem.getId() != null
            && selectedItem.getId().equals(item.getId()));
        boolean isHover = (hoverItem != null && hoverItem.getId() != null
            && hoverItem.getId().equals(item.getId()));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 40));

        switch (shapeType) {
            case 0 -> { // Pink circle for own objects
                g2.setColor(isHover ? color.brighter() : color);
                g2.fillOval(sx + 2, sy + 2, scaledSize, scaledSize);
            }
            case 1 -> { // Green square for other users
                g2.setColor(isHover ? color.brighter() : color);
                g2.fillRect(sx + 2, sy + 2, scaledSize, scaledSize);
            }
            case 2 -> { // Blue diamond
                g2.setColor(isHover ? color.brighter() : color);
                int[] xPoints = {sx + scaledSize / 2, sx + scaledSize + 2, sx + scaledSize / 2, sx + 2};
                int[] yPoints = {sy + 2, sy + scaledSize / 2, sy + scaledSize + 2, sy + scaledSize / 2};
                g2.fillPolygon(xPoints, yPoints, 4);
            }
        }

        // Selection ring (no stroke on shape, but a ring around selected)
        if (isSelected) {
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(UIStyle.ACCENT);
            g2.drawOval(sx - 3, sy - 3, scaledSize + 6, scaledSize + 6);
        }

        // Hover glow
        if (isHover && !isSelected) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(UIStyle.HOVER);
            g2.drawOval(sx - 2, sy - 2, scaledSize + 4, scaledSize + 4);
        }

        // ID label inside
        g2.setColor(Color.WHITE);
        g2.setFont(UIStyle.FONT_SMALL);
        String label = item.getId().toString();
        FontMetrics fm = g2.getFontMetrics();
        int tx = sx + (scaledSize - fm.stringWidth(label)) / 2;
        int ty = sy + scaledSize / 2 + fm.getAscent() / 2;
        g2.drawString(label, tx, ty);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(UIStyle.PANEL);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw axes
        drawAxes(g2);

        // Draw cross markers
        drawCrossMarkers(g2);

        if (items.isEmpty()) {
            g2.setColor(UIStyle.TEXT_SECONDARY);
            g2.setFont(UIStyle.FONT);
            String msg = LocalizationManager.getInstance().getString("msg.no.items");
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            return;
        }

        Rectangle view = getVisibleRect();
        int margin = 40;
        Rectangle expanded = new Rectangle(view.x - margin, view.y - margin,
            view.width + margin * 2, view.height + margin * 2);

        for (LabWork item : items) {
            if (item.getCoordinates() == null) continue;
            int cx = mapX(item);
            int cy = mapY(item);
            int size = mapSize(item);
            int scaled = Math.max(5, (int) Math.round(size * zoom));
            Rectangle itemRect = new Rectangle(cx - scaled / 2, cy - scaled / 2, scaled, scaled);
            if (!expanded.intersects(itemRect)) continue;
            float scale = 1.0f;
            Long id = item.getId();
            if (id != null && appearanceScales.containsKey(id)) scale = appearanceScales.get(id);
            drawItem(g2, item, cx, cy, size, scale);
        }
    }
}