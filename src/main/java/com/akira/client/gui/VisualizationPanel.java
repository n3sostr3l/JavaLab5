package com.akira.client.gui;

import com.akira.general.datas.LabWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    // --- СИСТЕМА АНИМАЦИЙ ---
    private static class AnimData {
        float progress;
        boolean appearing;
        LabWork item;
        Runnable onFinish;

        AnimData(float progress, boolean appearing, LabWork item, Runnable onFinish) {
            this.progress = progress;
            this.appearing = appearing;
            this.item = item;
            this.onFinish = onFinish;
        }
    }

    private final Map<Long, AnimData> animations = new HashMap<>();
    private javax.swing.Timer animationTimer;
    // ------------------------

    private final Map<String, Color> userColors = new HashMap<>();
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
                if (e.isPopupTrigger()) { showContextMenu(e); return; }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    LabWork hit = findItemAt(e.getX(), e.getY());
                    if (hit == null) { isPanning = true; lastDragPoint = e.getPoint(); }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                isPanning = false; lastDragPoint = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                LabWork h = findItemAt(e.getX(), e.getY());
                if (h != hoverItem) { hoverItem = h; repaint(); }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isPanning || lastDragPoint == null) return;
                panX += (e.getX() - lastDragPoint.x);
                panY += (e.getY() - lastDragPoint.y);
                lastDragPoint = e.getPoint();
                repaint();
            }
        });

        addMouseWheelListener(this::handleZoom);

        // Таймер анимации (~60 FPS)
        animationTimer = new javax.swing.Timer(16, ev -> {
            boolean anyRunning = false;
            Iterator<Map.Entry<Long, AnimData>> it = animations.entrySet().iterator();

            while (it.hasNext()) {
                AnimData data = it.next().getValue();
                float step = 0.04f;

                if (data.appearing) {
                    data.progress += step;
                    if (data.progress >= 1.0f) {
                        data.progress = 1.0f;
                        if (data.onFinish != null) data.onFinish.run();
                        it.remove();
                    } else anyRunning = true;
                } else { // Исчезновение
                    data.progress -= step;
                    if (data.progress <= 0.0f) {
                        data.progress = 0.0f;
                        if (data.onFinish != null) data.onFinish.run();
                        it.remove();
                    } else anyRunning = true;
                }
            }
            repaint();
            if (!anyRunning && animations.isEmpty()) {
                ((javax.swing.Timer) ev.getSource()).stop();
            }
        });
    }

    public void setItems(List<LabWork> newItems) {
        try {
            if (newItems == null) newItems = new ArrayList<>();

            // Безопасно фильтруем любые null значения
            List<LabWork> safeNewItems = new ArrayList<>();
            for (LabWork lw : newItems) {
                if (lw != null && lw.getId() != null) {
                    safeNewItems.add(lw);
                }
            }

            // 1. Ищем удаленные элементы для анимации исчезновения
            for (LabWork oldItem : this.items) {
                if (oldItem == null || oldItem.getId() == null) continue;
                if (!containsItemById(safeNewItems, oldItem.getId())) {
                    animateDisappearance(oldItem, null);
                }
            }

            // 2. Ищем новые элементы для анимации появления
            for (LabWork newItem : safeNewItems) {
                if (!containsItemById(this.items, newItem.getId())) {
                    animateAppearance(newItem, null);
                }
            }

            // 3. Обновляем локальный список
            this.items = safeNewItems;

            if (selectedItem != null && !containsItemById(this.items, selectedItem.getId())) selectedItem = null;
            if (hoverItem != null && !containsItemById(this.items, hoverItem.getId())) hoverItem = null;

            repaint();

        } catch (Exception e) {
            System.err.println("Ошибка при обновлении списка анимаций: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean containsItemById(List<LabWork> list, Long id) {
        if (id == null) return false;
        for (LabWork item : list) {
            if (item != null && item.getId() != null && id.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setCurrentLogin(String login) { this.currentLogin = login; }
    public void setOnItemEdit(Consumer<LabWork> onItemEdit) { this.onItemEdit = onItemEdit; }
    public void setOnItemDelete(Consumer<LabWork> onItemDelete) { this.onItemDelete = onItemDelete; }
    public void setOnItemSelected(Runnable r) { this.onItemSelected = r; }
    public LabWork getSelectedItem() { return selectedItem; }
    public void setSelectedItem(LabWork item) { this.selectedItem = item; repaint(); }

    @Override
    public String getToolTipText(MouseEvent event) {
        LabWork item = findItemAt(event.getX(), event.getY());
        if (item == null) return null;
        String ownerLabel = LocalizationManager.getInstance().getString("main.owner");
        return "<html>ID: " + item.getId() +
                (item.getName() != null ? "<br>" + item.getName() : "") +
                (item.getOwnerLogin() != null ? "<br>" + ownerLabel + " " + item.getOwnerLogin() : "") +
                "</html>";
    }

    private Color getColorForUser(String login) {
        if (login == null) login = "default";
        return userColors.computeIfAbsent(login, l -> {
            float hue = (l.hashCode() & 0xFFFF) / 65535f;
            return Color.getHSBColor(hue, 0.65f, 0.85f);
        });
    }

    private int getShapeType(String login) {
        if (login == null) return 1;
        if (currentLogin != null && login.equals(currentLogin)) return 0;
        return 1 + (Math.abs(login.hashCode()) % 2);
    }

    private void showContextMenu(MouseEvent e) {
        LabWork item = findItemAt(e.getX(), e.getY());
        if (item == null || currentLogin == null || !currentLogin.equals(item.getOwnerLogin())) return;
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
        panX = e.getX() - (e.getX() - panX) / oldZoom * zoom;
        panY = e.getY() - (e.getY() - panY) / oldZoom * zoom;
        repaint();
    }

    // --- Математически точные координаты (Double) ---
    private double getCoordScale() { return Math.max(0.3, Math.min(getWidth(), getHeight()) / 2000.0); }
    private double getDoubleX(LabWork item) { return (getWidth() / 2.0 + item.getCoordinates().getX() * getCoordScale()) * zoom + panX; }
    private double getDoubleY(LabWork item) { return (getHeight() / 2.0 - item.getCoordinates().getY() * getCoordScale()) * zoom + panY; }
    private double mapSize(LabWork item) { return Math.max(20.0, Math.min(80.0, item.getMaximumPoint() / 10.0)); }

    private LabWork findItemAt(int x, int y) {
        // Проверяем элементы в обратном порядке (сверху вниз)
        for (int i = items.size() - 1; i >= 0; i--) {
            LabWork item = items.get(i);
            if (animations.containsKey(item.getId()) && !animations.get(item.getId()).appearing) continue;
            if (item.getCoordinates() == null) continue;

            double cx = getDoubleX(item);
            double cy = getDoubleY(item);
            double scaledSize = Math.max(5.0, mapSize(item) * zoom);

            // Идеально точная зона клика (Double)
            Rectangle2D.Double bounds = new Rectangle2D.Double(cx - scaledSize / 2 - 6, cy - scaledSize / 2 - 6, scaledSize + 12, scaledSize + 12);
            if (bounds.contains(x, y)) return item;
        }
        return null;
    }

    public void animateAppearance(LabWork item, Runnable onFinish) {
        if (item == null || item.getCoordinates() == null || item.getId() == null) return;
        // Если уже появляется - не трогаем
        if (animations.containsKey(item.getId()) && animations.get(item.getId()).appearing) return;

        animations.put(item.getId(), new AnimData(0.0f, true, item, onFinish));
        if (!animationTimer.isRunning()) animationTimer.start();
    }

    public void animateDisappearance(LabWork item, Runnable onFinish) {
        if (item == null || item.getCoordinates() == null || item.getId() == null) return;
        // Если уже исчезает - не трогаем, чтобы не сбить анимацию!
        if (animations.containsKey(item.getId()) && !animations.get(item.getId()).appearing) return;

        // Если фигура не успела появиться до конца, начинаем исчезать с текущей прозрачности
        float startProgress = animations.containsKey(item.getId()) ? animations.get(item.getId()).progress : 1.0f;

        animations.put(item.getId(), new AnimData(startProgress, false, item, onFinish));
        if (!animationTimer.isRunning()) animationTimer.start();
    }

    private void drawAxes(Graphics2D g2) {
        int w = getWidth(), h = getHeight(), arrowSize = 10;
        int ox = (int) Math.round((w / 2.0) * zoom + panX);
        int oy = (int) Math.round((h / 2.0) * zoom + panY);

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(180, 190, 210));
        g2.drawLine(0, oy, w, oy); g2.drawLine(w, oy, w - arrowSize, oy - arrowSize / 2); g2.drawLine(w, oy, w - arrowSize, oy + arrowSize / 2);
        g2.drawLine(ox, 0, ox, h); g2.drawLine(ox, 0, ox - arrowSize / 2, arrowSize); g2.drawLine(ox, 0, ox + arrowSize / 2, arrowSize);
        g2.setFont(new Font("SansSerif", Font.PLAIN, UIStyle.scale(11)));
        g2.drawString("X", w - UIStyle.scale(14), oy - UIStyle.scale(6));
        g2.drawString("Y", ox + UIStyle.scale(6), UIStyle.scale(14));
    }

    private void drawCrossMarkers(Graphics2D g2) {
        int w = getWidth(), h = getHeight(), margin = 12, size = 8;
        g2.setColor(UIStyle.ACCENT);
        g2.setStroke(new BasicStroke(1.5f));
        for (int[] pos : new int[][]{{margin, margin}, {w - margin, margin}, {margin, h - margin}, {w - margin, h - margin}, {w / 2, margin}, {w / 2, h - margin}, {margin, h / 2}, {w - margin, h / 2}}) {
            g2.drawLine(pos[0] - size, pos[1], pos[0] + size, pos[1]);
            g2.drawLine(pos[0], pos[1] - size, pos[0], pos[1] + size);
        }
    }

    private void drawItem(Graphics2D g2, LabWork item, double cx, double cy, double size, float progress) {
        double scaledSize = Math.max(5.0, size * zoom);
        double halfSize = scaledSize / 2.0;

        Color color = getColorForUser(item.getOwnerLogin());
        boolean isSelected = (selectedItem != null && selectedItem.getId().equals(item.getId()));
        boolean isHover = (hoverItem != null && hoverItem.getId().equals(item.getId()));

        Composite originalComposite = g2.getComposite();
        AffineTransform originalTransform = g2.getTransform();

        try {
            // Эффект плавности (Кубический Ease)
            float ease = progress * progress * (3.0f - 2.0f * progress);

            // 1. Fade-in (Прозрачность)
            float alpha = Math.max(0.0f, Math.min(1.0f, progress));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // 2. Идеальное центрирование (Смещение в координату объекта)
            g2.translate(cx, cy);

            // Сохраняем состояние ДО вращения и масштабирования (чтобы текст оставался прямым)
            AffineTransform beforeRotationAndScale = g2.getTransform();

            // 3. Зум (Масштабирование от 0 до 100%) - Фигура ВЫРАСТАЕТ из центра
            double scaleFactor = Math.max(0.01, ease); // Чтобы не было 0
            g2.scale(scaleFactor, scaleFactor);

            // 4. Вращение влево на 180 градусов
            double angle = Math.toRadians(180.0 * (1.0 - ease));
            g2.rotate(angle);

            Color renderColor = isHover ? color.brighter() : color;
            g2.setColor(renderColor);

            // 5. Отрисовка фигуры строго вокруг (0,0). Это ИСКЛЮЧАЕТ любые смещения!
            Shape shape;
            switch (getShapeType(item.getOwnerLogin())) {
                case 0 -> shape = new Ellipse2D.Double(-halfSize, -halfSize, scaledSize, scaledSize);
                case 1 -> shape = new Rectangle2D.Double(-halfSize, -halfSize, scaledSize, scaledSize);
                default -> {
                    Path2D.Double diamond = new Path2D.Double();
                    diamond.moveTo(0, -halfSize);
                    diamond.lineTo(halfSize, 0);
                    diamond.lineTo(0, halfSize);
                    diamond.lineTo(-halfSize, 0);
                    diamond.closePath();
                    shape = diamond;
                }
            }
            g2.fill(shape);

            // 6. Овал выбора/наведения (Компенсируем толщину линии при масштабировании)
            if (isSelected) {
                g2.setStroke(new BasicStroke((float)(2.5 / scaleFactor)));
                g2.setColor(UIStyle.ACCENT);
                g2.draw(new Ellipse2D.Double(-halfSize - 3, -halfSize - 3, scaledSize + 6, scaledSize + 6));
            }
            if (isHover && !isSelected) {
                g2.setStroke(new BasicStroke((float)(2.0 / scaleFactor)));
                g2.setColor(UIStyle.HOVER);
                g2.draw(new Ellipse2D.Double(-halfSize - 2, -halfSize - 2, scaledSize + 4, scaledSize + 4));
            }

            // 7. Сбрасываем Вращение и Зум, оставляем только Центровку
            g2.setTransform(beforeRotationAndScale);

            // 8. Отрисовка текста (Всегда прямой и отцентрированный)
            g2.setColor(Color.WHITE);
            g2.setFont(UIStyle.FONT_SMALL);
            String label = item.getId().toString();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, -fm.stringWidth(label) / 2, fm.getAscent() / 2 - 1);

        } finally {
            // Восстанавливаем оригинальные настройки холста
            g2.setTransform(originalTransform);
            g2.setComposite(originalComposite);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(UIStyle.PANEL);
        g2.fillRect(0, 0, getWidth(), getHeight());
        drawAxes(g2);
        drawCrossMarkers(g2);

        // Если список пуст, рисуем текст "Нет элементов".
        // ВНИМАНИЕ: Мы убрали 'return;' отсюда, поэтому анимация удаляющихся объектов (призраков)
        // будет гарантированно отрисована поверх этого текста!
        if (items.isEmpty()) {
            g2.setColor(UIStyle.TEXT_SECONDARY);
            g2.setFont(UIStyle.FONT);
            String msg = LocalizationManager.getInstance().getString("msg.no.items");
            if(msg == null) msg = "No items";
            g2.drawString(msg, (getWidth() - g2.getFontMetrics().stringWidth(msg)) / 2, getHeight() / 2);
        }

        Rectangle expanded = new Rectangle(getVisibleRect());
        expanded.grow(40, 40);

        // 1. Отрисовка "призраков" (удаляемых объектов). Рисуем их первыми, чтобы они уходили на задний план.
        for (AnimData data : animations.values()) {
            if (!data.appearing && !containsItemById(items, data.item.getId())) {
                drawItem(g2, data.item, getDoubleX(data.item), getDoubleY(data.item), mapSize(data.item), data.progress);
            }
        }

        // 2. Отрисовка существующих и появляющихся объектов
        for (LabWork item : items) {
            if (item.getCoordinates() == null) continue;

            double cx = getDoubleX(item);
            double cy = getDoubleY(item);
            double size = mapSize(item);
            double scaled = Math.max(5.0, size * zoom);

            // Если объект полностью за границами экрана - не рисуем его (оптимизация)
            if (!expanded.intersects(cx - scaled / 2, cy - scaled / 2, scaled, scaled)) continue;

            float progress = (animations.containsKey(item.getId())) ? animations.get(item.getId()).progress : 1.0f;
            drawItem(g2, item, cx, cy, size, progress);
        }
    }
}