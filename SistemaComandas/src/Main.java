import java.util.*;

public class Main {

    enum Mesa { ESQUERDA, DIREITA, SEM_MESA }

    static class Item {
        String nome;
        double preco;
        int quantidade;

        Item(String nome, double preco, int quantidade) {
            this.nome = nome;
            this.preco = preco;
            this.quantidade = quantidade;
        }

        double subtotal() { return preco * quantidade; }
    }

    static class Comanda {
        private static int proximoId = 1;
        final int id = proximoId++;
        final String rotulo;
        final Mesa mesa;
        final List<Item> itens = new ArrayList<>();

        Comanda(String rotulo, Mesa mesa) {
            this.rotulo = rotulo;
            this.mesa = mesa;
        }

        void adicionar(String nome, double preco, int qtd) {
            for (Item i : itens) {
                if (i.nome.equalsIgnoreCase(nome)) { i.quantidade += qtd; return; }
            }
            itens.add(new Item(nome, preco, qtd));
        }

        Item buscar(String nome) {
            for (Item i : itens) {
                if (i.nome.equalsIgnoreCase(nome)) return i;
            }
            throw new NoSuchElementException("Item não encontrado: " + nome);
        }

        void alterarQuantidade(String nome, int delta) {
            Item i = buscar(nome);
            i.quantidade += delta;
            if (i.quantidade <= 0) itens.remove(i); // zerou, sai da lista
        }

        void remover(String nome) { itens.remove(buscar(nome)); }

        double total() {
            double soma = 0;
            for (Item i : itens) soma += i.subtotal();
            return soma;
        }

        void imprimir() {
            System.out.println("\n#" + id + " - " + rotulo + " [" + mesa + "]");
            for (Item i : itens) {
                System.out.printf("  %dx %s (R$ %.2f) = R$ %.2f%n",
                        i.quantidade, i.nome, i.preco, i.subtotal());
            }
            System.out.printf("  TOTAL: R$ %.2f%n", total());
        }
    }

    static Scanner sc = new Scanner(System.in);
    static Map<Integer, Comanda> abertas = new LinkedHashMap<>();

    static String lerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    static int lerInt(String msg) {
        return Integer.parseInt(lerTexto(msg));
    }

    static double lerPreco(String msg) {
        return Double.parseDouble(lerTexto(msg).replace(",", ".")); // aceita 9,50
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1) Nova comanda  2) Ver abertas  3) Abrir comanda  4) Fechar comanda  0) Sair");
            String op = lerTexto("> ");
            try {
                switch (op) {
                    case "1" -> novaComanda();
                    case "2" -> listar();
                    case "3" -> menuComanda();
                    case "4" -> {
                        int id = lerInt("Id da comanda: ");
                        if (abertas.remove(id) == null) System.out.println("Não existe.");
                        else System.out.println("Comanda fechada.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    static void novaComanda() {
        String rotulo = lerTexto("Nome/descrição do cliente: ");
        String m = lerTexto("Mesa (1-Esquerda, 2-Direita, 3-Sem mesa): ");
        Mesa mesa = switch (m) {
            case "1" -> Mesa.ESQUERDA;
            case "2" -> Mesa.DIREITA;
            default -> Mesa.SEM_MESA;
        };
        Comanda c = new Comanda(rotulo, mesa);
        abertas.put(c.id, c);
        System.out.println("Criada comanda #" + c.id);
    }

    static void listar() {
        if (abertas.isEmpty()) { System.out.println("Nenhuma comanda aberta."); return; }
        for (Comanda c : abertas.values()) {
            System.out.printf("#%d - %s [%s] -> R$ %.2f%n", c.id, c.rotulo, c.mesa, c.total());
        }
    }

    static void menuComanda() {
        Comanda c = abertas.get(lerInt("Id da comanda: "));
        if (c == null) { System.out.println("Não existe."); return; }

        while (true) {
            c.imprimir();
            System.out.println("\n1) Add item  2) +/- quantidade  3) Remover item  0) Voltar");
            String op = lerTexto("> ");
            try {
                switch (op) {
                    case "1" -> c.adicionar(lerTexto("Produto: "), lerPreco("Preço: "), lerInt("Qtd: "));
                    case "2" -> c.alterarQuantidade(lerTexto("Produto: "), lerInt("Variação (ex: 1 ou -1): "));
                    case "3" -> c.remover(lerTexto("Produto: "));
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}