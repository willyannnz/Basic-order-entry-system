import repository.Database;
import repository.TabRepository;
import service.TabManager;
import ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        try {
            Database database = new Database(Database.defaultFile());
            database.createTables();
            System.out.println("Dados salvos em: " + database.getFile());
            TabManager manager = new TabManager(new TabRepository(database));
            new ConsoleMenu(manager).run();
        } catch (RuntimeException e) {
            System.out.println("Não foi possível iniciar o sistema: " + e.getMessage());
        }
    }
}
