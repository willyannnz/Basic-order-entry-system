package model;

public enum Table {
    LEFT("Esquerda"),
    RIGHT("Direita"),
    NO_TABLE("Sem mesa");

    private final String displayName;

    Table(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
