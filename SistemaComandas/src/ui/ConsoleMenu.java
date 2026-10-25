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
            System.out.println("\n1) Nova comanda  2) Comandas abertas  3) Editar comanda  4) Fechar comanda  0) Sair");
            String option = readText("> ");
            try {
                switch (option) {
                    case "1" -> newTab();
                    case "2" -> listTabs();
                    case "3" -> editTab();
                    case "4" -> {
                        manager.close(readInt("Id da comanda: "));
                        System.out.println("Comanda fechada.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void newTab() {
        String label = readText("Nome/descrição do cliente: ");
        String choice = readText("Mesa (1-Esquerda, 2-Direita, 3-Sem mesa): ");
        Table table = switch (choice) {
            case "1" -> Table.LEFT;
            case "2" -> Table.RIGHT;
            default -> Table.NO_TABLE;
        };
        Tab tab = manager.open(label, table);
        System.out.println("Comanda #" + tab.getId() + " criada.");
        editTab(tab); // já cai direto no lançamento de itens
    }

    private void listTabs() {
        if (manager.listOpen().isEmpty()) {
            System.out.println("Nenhuma comanda aberta.");
            return;
        }
        for (Tab tab : manager.listOpen()) {
            System.out.printf("#%d - %s [%s] -> R$ %.2f%n",
                    tab.getId(), tab.getLabel(), tab.getTable().getDisplayName(), tab.getTotal());
        }
    }

    private void editTab() {
        editTab(manager.find(readInt("Id da comanda: ")));
    }

    private void editTab(Tab tab) {
        while (true) {
            printTab(tab);
            String back = tab.getItems().isEmpty() ? "0 = voltar (ainda não pegou nada)" : "0 = voltar";
            System.out.println("\nn = novo item | 1+ = soma 1 | 1- = tira 1 | 1x = exclui o item | " + back);
            String command = readText("> ").toLowerCase();
            try {
                if (command.equals("0")) return;
                if (command.equals("n")) {
                    tab.addItem(readText("Produto: "), readPrice("Preço: "), readInt("Qtd: "));
                } else {
                    applyItemCommand(tab, command);
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    // Commands like "2+" (add 1), "2-" (remove 1) and "2x" (delete the whole item)
    private void applyItemCommand(Tab tab, String command) {
        if (!command.matches("\\d{1,3}[+\\-x]")) {
            throw new IllegalArgumentException("Comando inválido. Exemplos: n, 1+, 2-, 3x, 0");
        }
        char action = command.charAt(command.length() - 1);
        int number = Integer.parseInt(command.substring(0, command.length() - 1));
        if (number < 1 || number > tab.getItems().size()) {
            throw new IllegalArgumentException("Não existe o item " + number + " na lista");
        }
        Item item = tab.getItems().get(number - 1);
        switch (action) {
            case '+' -> tab.changeQuantity(item.getName(), 1);
            case '-' -> tab.changeQuantity(item.getName(), -1);
            case 'x' -> tab.removeItem(item.getName());
        }
    }

    private void printTab(Tab tab) {
        System.out.println("\n#" + tab.getId() + " - " + tab.getLabel() + " [" + tab.getTable().getDisplayName() + "]");
        int number = 1;
        for (Item item : tab.getItems()) {
            System.out.printf("  %d) %dx %s (R$ %.2f) = R$ %.2f%n",
                    number++, item.getQuantity(), item.getName(), item.getPrice(), item.getSubtotal());
        }
        System.out.printf("  TOTAL: R$ %.2f%n", tab.getTotal());
    }

    private String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private int readInt(String message) {
        String text = readText(message);
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido: " + text);
        }
    }

    private double readPrice(String message) {
        String text = readText(message);
        try {
            return Double.parseDouble(text.replace(",", ".")); // aceita 9,50
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido: " + text);
        }
    }
}
