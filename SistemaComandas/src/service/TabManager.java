package service;

import model.Tab;
import model.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TabManager {
    private final Map<Integer, Tab> openTabs = new LinkedHashMap<>();

    public Tab open(String label, Table table) {
        Tab tab = new Tab(label, table);
        openTabs.put(tab.getId(), tab);
        return tab;
    }

    public Tab find(int id) {
        Tab tab = openTabs.get(id);
        if (tab == null) throw new NoSuchElementException("Não existe comanda aberta com o id " + id);
        return tab;
    }

    public void close(int id) {
        find(id);
        openTabs.remove(id);
    }

    public Collection<Tab> listOpen() {
        return Collections.unmodifiableCollection(openTabs.values());
    }
}
