package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Tab {
    private static int nextId = 1;

    private final int id = nextId++;
    private final String label;
    private final Table table;
    private final List<Item> items = new ArrayList<>();

    public Tab(String label, Table table) {
        this.label = label;
        this.table = table;
    }

    public void addItem(String name, double price, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Invalid quantity");
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.changeQuantity(quantity);
                return;
            }
        }
        items.add(new Item(name, price, quantity));
    }

    public void changeQuantity(String name, int delta) {
        Item item = findItem(name);
        if (item.getQuantity() + delta <= 0) {
            items.remove(item); // reached zero, drop it from the list
        } else {
            item.changeQuantity(delta);
        }
    }

    public void removeItem(String name) {
        items.remove(findItem(name));
    }

    private Item findItem(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) return item;
        }
        throw new NoSuchElementException("Item not found: " + name);
    }

    public double getTotal() {
        double sum = 0;
        for (Item item : items) sum += item.getSubtotal();
        return sum;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public Table getTable() { return table; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
}
