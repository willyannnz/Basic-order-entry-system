package ui;

import model.Item;
import model.Tab;
import model.Table;
import service.TabManager;

import java.util.Scanner;

public class ConsoleMenu {
    private final TabManager manager;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(TabManager manager) {
        this.manager = manager;
    }

    public void run() {
        while (true) {
            System.out.println("\n1) New tab  2) Open tabs  3) Edit tab  4) Close tab  0) Exit");
            String option = readText("> ");
            try {
                switch (option) {
                    case "1" -> newTab();
                    case "2" -> listTabs();
                    case "3" -> editTab();
                    case "4" -> {
                        manager.close(readInt("Tab id: "));
                        System.out.println("Tab closed.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void newTab() {
        String label = readText("Customer name/description: ");
        String choice = readText("Table (1-Left, 2-Right, 3-No table): ");
        Table table = switch (choice) {
            case "1" -> Table.LEFT;
            case "2" -> Table.RIGHT;
            default -> Table.NO_TABLE;
        };
        Tab tab = manager.open(label, table);
        System.out.println("Created tab #" + tab.getId());
    }

    private void listTabs() {
        if (manager.listOpen().isEmpty()) {
            System.out.println("No open tabs.");
            return;
        }
        for (Tab tab : manager.listOpen()) {
            System.out.printf("#%d - %s [%s] -> R$ %.2f%n",
                    tab.getId(), tab.getLabel(), tab.getTable(), tab.getTotal());
        }
    }

    private void editTab() {
        Tab tab = manager.find(readInt("Tab id: "));
        while (true) {
            printTab(tab);
            System.out.println("\n1) Add item  2) +/- quantity  3) Remove item  0) Back");
            String option = readText("> ");
            try {
                switch (option) {
                    case "1" -> tab.addItem(readText("Product: "), readPrice("Price: "), readInt("Qty: "));
                    case "2" -> tab.changeQuantity(readText("Product: "), readInt("Change (e.g. 1 or -1): "));
                    case "3" -> tab.removeItem(readText("Product: "));
                    case "0" -> { return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printTab(Tab tab) {
        System.out.println("\n#" + tab.getId() + " - " + tab.getLabel() + " [" + tab.getTable() + "]");
        for (Item item : tab.getItems()) {
            System.out.printf("  %dx %s (R$ %.2f) = R$ %.2f%n",
                    item.getQuantity(), item.getName(), item.getPrice(), item.getSubtotal());
        }
        System.out.printf("  TOTAL: R$ %.2f%n", tab.getTotal());
    }

    private String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private int readInt(String message) {
        return Integer.parseInt(readText(message));
    }

    private double readPrice(String message) {
        return Double.parseDouble(readText(message).replace(",", ".")); // accepts 9,50
    }
}
