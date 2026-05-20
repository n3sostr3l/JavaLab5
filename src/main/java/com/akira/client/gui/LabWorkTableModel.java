package com.akira.client.gui;

import com.akira.general.datas.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Табличная модель для отображения коллекции LabWork.
 * Поддерживает сортировку и фильтрацию через Streams API.
 */
public class LabWorkTableModel extends AbstractTableModel {
    private List<LabWork> allItems = new ArrayList<>();
    private List<LabWork> filteredItems = new ArrayList<>();
    private final Map<Long, Integer> idToKey = new HashMap<>();
    private String filterText = "";
    private String sortColumn = "id";
    private boolean sortAscending = true;

    private static final String[] COLUMN_KEYS = {
        "col.key", "col.id", "col.name", "col.x", "col.y",
        "col.creationDate", "col.minimalPoint", "col.maximumPoint",
        "col.description", "col.difficulty", "col.authorName",
        "col.authorX", "col.authorY", "col.authorZ", "col.owner"
    };

    public void setData(Hashtable<Integer, LabWork> collection) {
        idToKey.clear();
        if (collection != null) {
            allItems = new ArrayList<>(collection.values());
            for (Map.Entry<Integer, LabWork> entry : collection.entrySet()) {
                LabWork lw = entry.getValue();
                if (lw != null && lw.getId() != null) {
                    idToKey.put(lw.getId(), entry.getKey());
                }
            }
        } else {
            allItems = new ArrayList<>();
        }
        applyFilterAndSort();
    }

    public void applyFilterAndSort() {
        StreamsAPI.applyFilterAndSort(this, allItems, filterText, sortColumn, sortAscending);
    }

    void setFilteredItems(List<LabWork> items) {
        this.filteredItems = items;
        fireTableDataChanged();
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText.toLowerCase();
        applyFilterAndSort();
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
        applyFilterAndSort();
    }

    public void setSortAscending(boolean ascending) {
        this.sortAscending = ascending;
        applyFilterAndSort();
    }

    public LabWork getLabWorkAt(int row) {
        if (row >= 0 && row < filteredItems.size()) {
            return filteredItems.get(row);
        }
        return null;
    }

    public Integer getKeyAtRow(int row) {
        LabWork lw = getLabWorkAt(row);
        return getKeyForLabWork(lw);
    }

    public Integer getKeyForLabWork(LabWork lw) {
        if (lw == null || lw.getId() == null) return null;
        return idToKey.get(lw.getId());
    }

    public List<LabWork> getAllItems() {
        return allItems;
    }

    public List<LabWork> getFilteredItems() {
        return filteredItems;
    }

    @Override
    public int getRowCount() {
        return filteredItems.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_KEYS.length;
    }

    @Override
    public String getColumnName(int column) {
        return LocalizationManager.getInstance().getString(COLUMN_KEYS[column]);
    }

    public String getColumnKey(int column) {
        return COLUMN_KEYS[column].replace("col.", "");
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LabWork lw = filteredItems.get(rowIndex);
        if (lw == null) return "";

        Locale locale = LocalizationManager.getInstance().getCurrentLocale();

        return switch (columnIndex) {
            case 0 -> idToKey.getOrDefault(lw.getId(), -1);
            case 1 -> lw.getId();
            case 2 -> lw.getName();
            case 3 -> lw.getCoordinates() != null
                ? java.text.NumberFormat.getInstance(locale).format(lw.getCoordinates().getX()) : "";
            case 4 -> lw.getCoordinates() != null
                ? java.text.NumberFormat.getInstance(locale).format(lw.getCoordinates().getY()) : "";
            case 5 -> {
                if (lw.getCreationDate() != null) {
                    java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance(
                        java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, locale);
                    yield df.format(lw.getCreationDate());
                }
                yield "";
            }
            case 6 -> {
                if (lw.getMinimalPoint() != null) {
                    yield java.text.NumberFormat.getInstance(locale).format(lw.getMinimalPoint());
                }
                yield "";
            }
            case 7 -> java.text.NumberFormat.getInstance(locale).format(lw.getMaximumPoint());
            case 8 -> lw.getDescription() != null ? lw.getDescription() : "";
            case 9 -> lw.getDifficulty() != null ? lw.getDifficulty().name() : "";
            case 10 -> lw.getAuthor() != null ? lw.getAuthor().getName() : "";
            case 11 -> lw.getAuthor() != null && lw.getAuthor().getLocation() != null
                ? String.valueOf(lw.getAuthor().getLocation().getX()) : "";
            case 12 -> lw.getAuthor() != null && lw.getAuthor().getLocation() != null
                ? String.valueOf(lw.getAuthor().getLocation().getY()) : "";
            case 13 -> lw.getAuthor() != null && lw.getAuthor().getLocation() != null
                ? String.valueOf(lw.getAuthor().getLocation().getZ()) : "";
            case 14 -> {
                String owner = lw.getOwnerLogin();
                yield owner != null ? owner : "(unknown)";
            }
            default -> "";
        };
    }
}