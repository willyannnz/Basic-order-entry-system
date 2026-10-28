package model;

import java.util.List;

// Read-only snapshot of a tab that was already paid and closed (history)
public record ClosedTab(int id, String label, Table table, String note,
                        String openedAt, String closedAt, List<Item> items) {

    public double total() {
        double sum = 0;
        for (Item item : items) sum += item.getSubtotal();
        return sum;
    }
}
