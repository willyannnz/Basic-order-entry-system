import service.TabManager;
import ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        new ConsoleMenu(new TabManager()).run();
    }
}
