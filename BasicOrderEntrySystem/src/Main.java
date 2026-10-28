import gui.MainWindow;
import gui.Theme;
import repository.Database;
import repository.TabRepository;
import service.TabManager;
import ui.ConsoleMenu;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    // Opens the window by default; run with the argument --console to use the text menu
    public static void main(String[] args) {
        boolean console = args.length > 0 && args[0].equals("--console");
        try {
            Database database = new Database(Database.defaultFile());
            database.createTables();
            TabManager manager = new TabManager(new TabRepository(database));
            if (console) {
                System.out.println("Dados salvos em: " + database.getFile());
                new ConsoleMenu(manager).run();
            } else {
                SwingUtilities.invokeLater(() -> {
                    Theme.apply();
                    new MainWindow(manager).setVisible(true);
                });
            }
        } catch (RuntimeException e) {
            String message = "Não foi possível iniciar o sistema: " + e.getMessage();
            if (console) {
                System.out.println(message);
            } else {
                JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
