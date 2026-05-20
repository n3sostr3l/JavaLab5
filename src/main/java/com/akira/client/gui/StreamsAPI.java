package com.akira.client.gui;

import com.akira.general.datas.LabWork;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для фильтрации и сортировки коллекции с помощью Streams API.
 */
public class StreamsAPI {

    public static void applyFilterAndSort(LabWorkTableModel model, List<LabWork> items,
                                           String filterText, String sortColumn, boolean ascending) {
        List<LabWork> result = items.stream()
            .filter(item -> matchesFilter(item, filterText))
            .sorted(getComparator(sortColumn, ascending))
            .collect(Collectors.toList());
        model.setFilteredItems(result);
    }

    private static boolean matchesFilter(LabWork item, String filter) {
        if (filter == null || filter.isEmpty()) return true;
        String f = filter.toLowerCase();

        return (item.getName() != null && item.getName().toLowerCase().contains(f))
            || String.valueOf(item.getId()).contains(f)
            || String.valueOf(item.getMaximumPoint()).contains(f)
            || (item.getMinimalPoint() != null && String.valueOf(item.getMinimalPoint()).contains(f))
            || (item.getDifficulty() != null && item.getDifficulty().name().toLowerCase().contains(f))
            || (item.getDescription() != null && item.getDescription().toLowerCase().contains(f))
            || (item.getOwnerLogin() != null && item.getOwnerLogin().toLowerCase().contains(f))
            || (item.getAuthor() != null && item.getAuthor().getName() != null
                && item.getAuthor().getName().toLowerCase().contains(f));
    }

    private static Comparator<LabWork> getComparator(String column, boolean ascending) {
        Comparator<LabWork> comp = switch (column) {
            case "key" -> (a, b) -> 0; // placeholder — overridden in LabWorkTableModel if needed
            case "id" -> Comparator.comparingLong(LabWork::getId);
            case "name" -> Comparator.comparing(LabWork::getName, Comparator.nullsLast(String::compareTo));
            case "x" -> Comparator.comparing(l -> l.getCoordinates() != null ? l.getCoordinates().getX() : 0,
                Comparator.nullsLast(Integer::compare));
            case "y" -> Comparator.comparing(l -> l.getCoordinates() != null ? l.getCoordinates().getY() : 0L,
                Comparator.nullsLast(Long::compare));
            case "creationDate" -> Comparator.comparing(LabWork::getCreationDate,
                Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "minimalPoint" -> Comparator.comparing(LabWork::getMinimalPoint,
                Comparator.nullsLast(Float::compare));
            case "maximumPoint" -> Comparator.comparingLong(LabWork::getMaximumPoint);
            case "description" -> Comparator.comparing(LabWork::getDescription,
                Comparator.nullsLast(String::compareTo));
            case "difficulty" -> Comparator.comparing(l -> l.getDifficulty() != null ? l.getDifficulty().name() : "",
                String::compareTo);
            case "authorName" -> Comparator.comparing(
                l -> l.getAuthor() != null ? l.getAuthor().getName() : "",
                Comparator.nullsLast(String::compareTo));
            case "authorX" -> Comparator.comparing(
                l -> l.getAuthor() != null && l.getAuthor().getLocation() != null
                    ? l.getAuthor().getLocation().getX() : Integer.MAX_VALUE,
                Comparator.nullsLast(Integer::compare));
            case "authorY" -> Comparator.comparing(
                l -> l.getAuthor() != null && l.getAuthor().getLocation() != null
                    ? l.getAuthor().getLocation().getY() : Float.MAX_VALUE,
                Comparator.nullsLast(Float::compare));
            case "authorZ" -> Comparator.comparing(
                l -> l.getAuthor() != null && l.getAuthor().getLocation() != null
                    ? l.getAuthor().getLocation().getZ() : Double.MAX_VALUE,
                Comparator.nullsLast(Double::compare));
            case "owner" -> Comparator.comparing(LabWork::getOwnerLogin,
                Comparator.nullsLast(String::compareTo));
            default -> Comparator.comparingLong(LabWork::getId);
        };
        return ascending ? comp : comp.reversed();
    }
}