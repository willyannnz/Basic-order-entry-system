# Basic-order-entry-system

A simple order-entry system to keep track of what customers are consuming at my beverage store before they pay.

Each customer gets a **tab** (identified by name or by a short description, like "customer with the red cap"), optionally linked to one of the two tables in the store. Items are added with name, price and quantity, quantities can be increased or decreased, items can be removed, and the total is always up to date. When the customer pays, the tab is closed.

## Current status

- Console version in Java, working.
- Data is saved in a local SQLite database (open tabs survive closing the program; closed tabs are kept as history).

## Planned

- History screen for closed tabs
- Graphical interface

## Project structure

```
SistemaComandas/src
├── Main.java
├── model/       Table, Item, Tab        (the data and its rules)
├── repository/  Database, TabRepository (SQLite access)
├── service/     TabManager              (which tabs are open)
└── ui/          ConsoleMenu             (talks to the user)
```

The database file is created automatically at `~/SistemaComandas/comandas.db`.

## How to run

Open the inner `SistemaComandas` folder in IntelliJ IDEA (JDK 17 or newer).

The SQLite driver is not committed to the repository. Download `sqlite-jdbc-3.41.2.2.jar` from the [sqlite-jdbc releases](https://github.com/xerial/sqlite-jdbc/releases/tag/3.41.2.2), put it in `SistemaComandas/lib/` and add it as a library (right-click the jar > Add as Library). Then run `Main`.
