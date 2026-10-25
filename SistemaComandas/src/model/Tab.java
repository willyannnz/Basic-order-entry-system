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
    private String note = "";

    public Tab(String label, Table table) {
        this.label = label;
        this.table = table;
    }

    public void addItem(String name, double price, int quantity) {
        if (name.isBlank()) throw new IllegalArgumentException("Nome do produto vazio");
        if (price < 0) throw new IllegalArgumentException("Preço inválido");
        if (quantity <= 0) throw new IllegalArgumentException("Quantidade inválida");
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
        int newQuantity = item.getQuantity() + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("O item só tem " + item.getQuantity() + " unidade(s)");
        }
        if (newQuantity == 0) {
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
        throw new NoSuchElementException("Item não encontrado: " + name);
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

    public String getNote() { return note; }

    public void setNote(String note) {
        this.note = (note == null) ? "" : note.trim();
    }
}
