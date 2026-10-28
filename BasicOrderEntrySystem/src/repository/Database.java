package repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private final Path file;
    private final String url;

    public Database(Path file) {
        this.file = file.toAbsolutePath();
        try {
            Files.createDirectories(this.file.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta do banco: " + e.getMessage(), e);
        }
        this.url = "jdbc:sqlite:" + this.file;
    }

    // Fixed location in the user's home folder, so the data does not depend on where the program is run
    public static Path defaultFile() {
        return Paths.get(System.getProperty("user.home"), "SistemaComandas", "comandas.db");
    }

    public Path getFile() { return file; }

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public void createTables() {
        String tabTable = """
                CREATE TABLE IF NOT EXISTS tab (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    label      TEXT NOT NULL,
                    table_side TEXT NOT NULL,
                    note       TEXT NOT NULL DEFAULT '',
                    status     TEXT NOT NULL DEFAULT 'OPEN',
                    opened_at  TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                    closed_at  TEXT
                )""";
        String itemTable = """
                CREATE TABLE IF NOT EXISTS tab_item (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    tab_id   INTEGER NOT NULL,
                    name     TEXT NOT NULL,
                    price    REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    FOREIGN KEY (tab_id) REFERENCES tab(id)
                )""";
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute(tabTable);
            statement.execute(itemTable);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao preparar o banco de dados: " + e.getMessage(), e);
        }
    }
}
