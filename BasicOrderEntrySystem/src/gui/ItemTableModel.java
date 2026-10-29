package gui;

import model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class ItemTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"N. Item", "Produto", "Preço", "Qtd", "Subtotal"};

    private List<Item> items = new ArrayList<>();

    void setItems(List<Item> newItems) {
        this.items = new ArrayList<>(newItems);
        fireTableDataChanged();
    }

    Item getItem(int row) {
        return items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Item item = items.get(row);
        return switch (column) {
            case 0 -> row + 1;
            case 1 -> item.getName();
            case 2 -> Format.money(item.getPrice());
            case 3 -> item.getQuantity();
            default -> Format.money(item.getSubtotal());
        };
    }
}
