# Basic-order-entry-system

A simple order-entry system to keep track of what customers are consuming at my beverage store before they pay.

Each customer gets a **tab** (identified by name or by a short description, like "customer with the red cap"), optionally linked to one of the two tables in the store. Items are added with name, price and quantity, quantities can be increased or decreased, items can be removed, and the total is always up to date. When the customer pays, the tab is closed.

## Current status

- Console version in Java, working.
- Data lives in memory only (closing the program clears the tabs).

## Planned

- SQLite database (persistence and history of closed tabs)
- Graphical interface

## Project structure

```
SistemaComandas/src
├── Main.java
├── model/       Table, Item, Tab        (the data and its rules)
├── service/     TabManager              (which tabs are open)
└── ui/          ConsoleMenu             (talks to the user)
```

A `repository/` package will be added when the database arrives.

## How to run

Open the `SistemaComandas` folder in IntelliJ IDEA (JDK 17 or newer) and run `Main`.
