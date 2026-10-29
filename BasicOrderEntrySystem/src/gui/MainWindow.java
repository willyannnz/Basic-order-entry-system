package gui;

import model.Item;
import model.Tab;
import model.Table;
import service.TabManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private final TabManager manager;

    private final DefaultListModel<Tab> tabListModel = new DefaultListModel<>();
    private final JList<Tab> tabList = new JList<>(tabListModel);
    private final ItemTableModel itemTableModel = new ItemTableModel();
    private final JTable itemTable = new JTable(itemTableModel);
    private final JLabel titleLabel = new JLabel();
    private final JLabel noteLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final List<JButton> tabButtons = new ArrayList<>(); // buttons that need a selected tab

    public MainWindow(TabManager manager) {
        super("Sistema de Comandas");
        this.manager = manager;
        buildLayout();
        reloadTabList(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // everything is saved after each change
        setSize(1150, 680);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
    }

    // ---------- layout ----------

    private void buildLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildLeftPanel(), BorderLayout.WEST);
        getContentPane().add(buildRightPanel(), BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        tabList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabList.setCellRenderer(new TabListRenderer());
        tabList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedTab();
        });

        JButton newButton = new JButton("Nova comanda");
        newButton.addActionListener(e -> run(this::newTab));
        JButton historyButton = new JButton("Histórico");
        historyButton.addActionListener(e -> run(this::showHistory));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.add(newButton);
        buttons.add(historyButton);

        JLabel title = new JLabel("Comandas abertas");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));
        panel.setPreferredSize(new Dimension(360, 0));
        panel.add(title, BorderLayout.NORTH);
        JScrollPane listScroll = new JScrollPane(tabList);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(listScroll, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRightPanel() {
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 26f));
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.add(titleLabel);
        header.add(noteLabel);

        itemTable.setRowHeight(34);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.getTableHeader().setFont(itemTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        itemTable.getColumnModel().getColumn(0).setMaxWidth(90);
        itemTable.getColumnModel().getColumn(3).setMaxWidth(80);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int column : new int[]{0, 2, 3, 4}) {
            itemTable.getColumnModel().getColumn(column).setCellRenderer(right);
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(tabButton("Adicionar produto", () -> addProducts(selectedTab())));
        actions.add(tabButton("Somar", () -> changeQuantity(true)));
        actions.add(tabButton("Tirar", () -> changeQuantity(false)));
        actions.add(tabButton("Excluir item", this::deleteItem));
        actions.add(tabButton("Observação", this::editNote));

        JButton closeButton = tabButton("Fechar comanda", this::closeTab);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 30f));
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(closeButton, BorderLayout.WEST);
        footer.add(totalLabel, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(footer, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));
        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JButton tabButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> run(action));
        tabButtons.add(button);
        return button;
    }

    // ---------- showing data ----------

    private void reloadTabList(Integer selectId) {
        Tab current = tabList.getSelectedValue();
        int keepId = selectId != null ? selectId : (current != null ? current.getId() : -1);

        tabListModel.clear();
        int indexToSelect = -1;
        int index = 0;
        for (Tab tab : manager.listOpen()) {
            tabListModel.addElement(tab);
            if (tab.getId() == keepId) indexToSelect = index;
            index++;
        }
        if (indexToSelect >= 0) {
            tabList.setSelectedIndex(indexToSelect);
        } else {
            tabList.clearSelection();
        }
        showSelectedTab();
    }

    private void showSelectedTab() {
        Tab tab = tabList.getSelectedValue();
        boolean hasTab = tab != null;
        for (JButton button : tabButtons) button.setEnabled(hasTab);

        if (!hasTab) {
            titleLabel.setText("Selecione uma comanda");
            noteLabel.setText(" ");
            itemTableModel.setItems(List.of());
            totalLabel.setText("Total: " + Format.money(0));
            return;
        }
        titleLabel.setText("#" + tab.getId() + " - " + tab.getLabel() + "  [" + tab.getTable().getDisplayName() + "]");
        noteLabel.setText(tab.getNote().isEmpty() ? " " : "Obs: " + tab.getNote());
        itemTableModel.setItems(tab.getItems());
        totalLabel.setText("Total: " + Format.money(tab.getTotal()));
    }

    // Refreshes the screen after a change, keeping the same item row selected
    private void afterChange(Tab tab) {
        int row = itemTable.getSelectedRow();
        reloadTabList(tab.getId());
        if (row >= 0 && row < itemTableModel.getRowCount()) {
            itemTable.setRowSelectionInterval(row, row);
        }
    }

    // ---------- actions ----------

    private void newTab() {
        JTextField labelField = new JTextField(22);
        JComboBox<Table> tableBox = new JComboBox<>(Table.values());
        tableBox.setSelectedItem(Table.NO_TABLE);
        tableBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Table table) setText(table.getDisplayName());
                return this;
            }
        });
        JPanel form = form("Cliente (nome ou descrição):", labelField, "Mesa:", tableBox);
        if (showForm("Nova comanda", form, new Object[]{"OK", "Cancelar"}, labelField) != 0) return;

        String label = labelField.getText().trim();
        if (label.isEmpty()) throw new IllegalArgumentException("Informe o nome ou uma descrição do cliente");
        Tab tab = manager.open(label, (Table) tableBox.getSelectedItem());
        reloadTabList(tab.getId());
        addProducts(tab); // already opens the product form; Cancel = "ainda não pegou nada"
    }

    private void addProducts(Tab tab) {
        JTextField nameField = new JTextField(22);
        JTextField priceField = new JTextField(10);
        JSpinner quantityField = quantitySpinner();
        JPanel form = form("Produto:", nameField, "Preço (ex: 9,50):", priceField, "Quantidade:", quantityField);
        Object[] options = {"Adicionar", "Adicionar e outro", "Cancelar"};

        while (true) {
            int choice = showForm("Adicionar produto", form, options, nameField);
            if (choice != 0 && choice != 1) return; // Cancelar or closed the window

            try {
                tab.addItem(nameField.getText().trim(), parsePrice(priceField.getText()), readQuantity(quantityField));
                manager.save(tab);
                reloadTabList(tab.getId());
            } catch (RuntimeException e) {
                showError(e);
                continue; // keeps what was typed so it can be fixed
            }
            if (choice == 0) return;
            nameField.setText("");
            priceField.setText("");
            quantityField.setValue(1);
        }
    }

    private void changeQuantity(boolean add) {
        Tab tab = selectedTab();
        Item item = selectedItem();
        JSpinner spinner = quantitySpinner();
        String question = (add ? "Quantos somar em " : "Quantos tirar de ") + item.getName() + "?";
        String title = add ? "Somar quantidade" : "Tirar quantidade";
        if (showForm(title, form(question, spinner), new Object[]{"OK", "Cancelar"}, spinnerField(spinner)) != 0) {
            return;
        }

        int amount = readQuantity(spinner);
        tab.changeQuantity(item.getName(), add ? amount : -amount);
        manager.save(tab);
        afterChange(tab);
    }

    private void deleteItem() {
        Tab tab = selectedTab();
        Item item = selectedItem();
        int result = JOptionPane.showConfirmDialog(this, "Excluir " + item.getName() + " da comanda?",
                "Excluir item", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.YES_OPTION) return;

        tab.removeItem(item.getName());
        manager.save(tab);
        afterChange(tab);
    }

    private void editNote() {
        Tab tab = selectedTab();
        JTextField noteField = new JTextField(tab.getNote(), 30);
        JPanel form = form("Observação (deixe vazio para apagar):", noteField);
        if (showForm("Observação", form, new Object[]{"OK", "Cancelar"}, noteField) != 0) return;

        tab.setNote(noteField.getText());
        manager.save(tab);
        afterChange(tab);
    }

    private void closeTab() {
        Tab tab = selectedTab();
        String message = "Fechar a comanda de " + tab.getLabel() + "?\nTotal: " + Format.money(tab.getTotal());
        int result = JOptionPane.showConfirmDialog(this, message, "Fechar comanda",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.YES_OPTION) return;

        manager.close(tab.getId());
        reloadTabList(null);
    }

    private void showHistory() {
        new HistoryDialog(this, manager).setVisible(true);
    }

    // ---------- helpers ----------

    private Tab selectedTab() {
        Tab tab = tabList.getSelectedValue();
        if (tab == null) throw new IllegalStateException("Selecione uma comanda na lista");
        return tab;
    }

    private Item selectedItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0) throw new IllegalStateException("Selecione um item na tabela");
        return itemTableModel.getItem(row);
    }

    private JSpinner quantitySpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        // By default Enter only confirms the field; this lets it reach the dialog's OK button
        spinnerField(spinner).getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        return spinner;
    }

    private int readQuantity(JSpinner spinner) {
        try {
            spinner.commitEdit(); // takes what was typed even if Enter was not pressed
        } catch (ParseException e) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        return (Integer) spinner.getValue();
    }

    private double parsePrice(String text) {
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido: " + text.trim());
        }
    }

    // Shows a form in a dialog with the cursor already in the right field.
    // Returns the index of the button that was clicked, or -1 if the window was closed.
    private int showForm(String title, JPanel form, Object[] options, JTextField focusField) {
        // JOptionPane calls selectInitialValue() when the window opens to decide who gets the focus.
        // Overriding it makes the chosen field get the focus (and the first button stays the Enter button).
        JOptionPane pane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
                null, options, options[0]) {
            @Override
            public void selectInitialValue() {
                focusField.requestFocusInWindow();
                JButton defaultButton = findButton(this, options[0].toString());
                JRootPane root = SwingUtilities.getRootPane(this);
                if (defaultButton != null && root != null) root.setDefaultButton(defaultButton);
            }
        };
        FocusAdapter selectOnFocus = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(focusField::selectAll); // typing replaces what was there
            }
        };
        focusField.addFocusListener(selectOnFocus);

        JDialog dialog = pane.createDialog(this, title);
        dialog.setVisible(true);
        dialog.dispose();
        focusField.removeFocusListener(selectOnFocus);

        Object chosen = pane.getValue();
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(chosen)) return i;
        }
        return -1;
    }

    private JButton findButton(Container root, String text) {
        for (Component component : root.getComponents()) {
            if (component instanceof JButton button && text.equals(button.getText())) return button;
            if (component instanceof Container child) {
                JButton found = findButton(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private JTextField spinnerField(JSpinner spinner) {
        return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
    }

    // Builds a vertical form: Strings become labels, components are added as they are
    private JPanel form(Object... parts) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        for (Object part : parts) {
            if (part instanceof String text) {
                panel.add(new JLabel(text));
            } else {
                panel.add((Component) part);
            }
        }
        return panel;
    }

    private void run(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void showError(RuntimeException e) {
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // ---------- how each tab looks in the list ----------

    private static class TabListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tab tab) {
                String note = tab.getNote().isEmpty()
                        ? ""
                        : "<br><i>Obs: " + Format.html(Format.shorten(tab.getNote(), 32)) + "</i>";
                setText("<html><b>#" + tab.getId() + " " + Format.html(Format.shorten(tab.getLabel(), 20)) + "</b> · "
                        + tab.getTable().getDisplayName() + "<br>" + Format.money(tab.getTotal()) + note + "</html>");
                setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            }
            return this;
        }
    }
}
