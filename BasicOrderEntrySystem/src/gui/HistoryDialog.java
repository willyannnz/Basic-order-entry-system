package gui;

import model.ClosedTab;
import model.Item;
import service.TabManager;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.util.List;

class HistoryDialog extends JDialog {
    private static final int LIMIT = 100;

    private final List<ClosedTab> closed;
    private final JTextArea details = new JTextArea();

    HistoryDialog(Frame owner, TabManager manager) {
        super(owner, "Histórico de comandas fechadas", true);
        this.closed = manager.listClosed(LIMIT);

        JTable table = new JTable(new ClosedTabsTableModel());
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(0).setCellRenderer(right);
        table.getColumnModel().getColumn(4).setCellRenderer(right);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails(table.getSelectedRow());
        });

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        details.setMargin(new Insets(10, 10, 10, 10));
        details.setText(closed.isEmpty()
                ? "Nenhuma comanda fechada ainda."
                : "Selecione uma comanda para ver os itens.");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), new JScrollPane(details));
        split.setResizeWeight(0.4);
        split.setDividerLocation(260); // list on top, details (items) with more room below
        add(split);
        setSize(820, 640);
        setLocationRelativeTo(owner);
    }

    private void showDetails(int row) {
        if (row < 0) return;
        ClosedTab tab = closed.get(row);
        StringBuilder text = new StringBuilder();
        text.append("#").append(tab.id()).append(" - ").append(tab.label())
                .append(" [").append(tab.table().getDisplayName()).append("]\n");
        text.append("Aberta em ").append(Format.date(tab.openedAt()))
                .append(" | fechada em ").append(Format.date(tab.closedAt())).append("\n");
        if (!tab.note().isEmpty()) {
            text.append("Obs: ").append(tab.note()).append("\n");
        }
        text.append("\n");
        for (Item item : tab.items()) {
            text.append(String.format("%dx %s (%s) = %s%n", item.getQuantity(), item.getName(),
                    Format.money(item.getPrice()), Format.money(item.getSubtotal())));
        }
        text.append("\nTOTAL: ").append(Format.money(tab.total()));
        details.setText(text.toString());
        details.setCaretPosition(0);
    }

    private class ClosedTabsTableModel extends AbstractTableModel {
        private final String[] columns = {"Id", "Cliente", "Mesa", "Fechada em", "Total"};

        @Override
        public int getRowCount() {
            return closed.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            ClosedTab tab = closed.get(row);
            return switch (column) {
                case 0 -> tab.id();
                case 1 -> tab.label();
                case 2 -> tab.table().getDisplayName();
                case 3 -> Format.date(tab.closedAt());
                default -> Format.money(tab.total());
            };
        }
    }
}
