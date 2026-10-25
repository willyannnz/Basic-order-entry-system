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
            if (!tab.getNote().isEmpty()) {
                System.out.println("      Obs: " + tab.getNote());
            }
        }
    }

    private void editTab() {
        editTab(manager.find(readInt("Id da comanda: ")));
    }

    private void editTab(Tab tab) {
        while (true) {
            printTab(tab);
            printEditMenu(tab);
            String option = readText("> ");
            try {
                switch (option) {
                    case "1" -> addProduct(tab);
                    case "2" -> addQuantity(tab);
                    case "3" -> removeQuantity(tab);
                    case "4" -> deleteItem(tab);
                    case "5" -> editNote(tab);
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void printEditMenu(Tab tab) {
        System.out.println();
        System.out.println("  1) Adicionar produto");
        System.out.println("  2) Somar quantidade em um item");
        System.out.println("  3) Tirar quantidade de um item");
        System.out.println("  4) Excluir um item");
        System.out.println("  5) Observação");
        System.out.println(tab.getItems().isEmpty()
                ? "  0) Voltar (ainda não pegou nada)"
                : "  0) Voltar");
    }

    private void addProduct(Tab tab) {
        tab.addItem(readText("Produto: "), readPrice("Preço: "), readInt("Qtd: "));
    }

    private void addQuantity(Tab tab) {
        Item item = pickItem(tab, "Qual item? ");
        int amount = readPositiveInt("Quantos somar? ");
        tab.changeQuantity(item.getName(), amount);
    }

    private void removeQuantity(Tab tab) {
        Item item = pickItem(tab, "Qual item? ");
        int amount = readPositiveInt("Quantos tirar? ");
        tab.changeQuantity(item.getName(), -amount);
    }

    private void deleteItem(Tab tab) {
        Item item = pickItem(tab, "Qual item excluir? ");
        tab.removeItem(item.getName());
    }

    private void editNote(Tab tab) {
        if (!tab.getNote().isEmpty()) {
            System.out.println("Observação atual: " + tab.getNote());
        }
        String note = readText("Nova observação (deixe vazio para apagar): ");
        tab.setNote(note);
        System.out.println(note.isEmpty() ? "Observação apagada." : "Observação salva.");
    }

    // Asks for the item number shown on screen and returns that item
    private Item pickItem(Tab tab, String question) {
        if (tab.getItems().isEmpty()) {
            throw new IllegalStateException("A comanda ainda não tem itens.");
        }
        int number = readInt(question);
        if (number < 1 || number > tab.getItems().size()) {
            throw new IllegalArgumentException("Não existe o item " + number + " na lista");
        }
        return tab.getItems().get(number - 1);
    }

    private void printTab(Tab tab) {
        System.out.println("\n#" + tab.getId() + " - " + tab.getLabel() + " [" + tab.getTable().getDisplayName() + "]");
        if (!tab.getNote().isEmpty()) {
            System.out.println("  Obs: " + tab.getNote());
        }
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

    private int readPositiveInt(String message) {
        int value = readInt(message);
        if (value <= 0) throw new IllegalArgumentException("Quantidade inválida");
        return value;
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
