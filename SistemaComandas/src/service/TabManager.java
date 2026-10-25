package service;

import model.Tab;
import model.Table;
import repository.TabRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TabManager {
    private final TabRepository repository;
    private final Map<Integer, Tab> openTabs = new LinkedHashMap<>();

    // On startup, loads the tabs that were still open from the database
    public TabManager(TabRepository repository) {
        this.repository = repository;
        for (Tab tab : repository.findOpen()) {
            openTabs.put(tab.getId(), tab);
        }
    }

    public Tab open(String label, Table table) {
        Tab tab = repository.insert(label, table);
        openTabs.put(tab.getId(), tab);
        return tab;
    }

    public Tab find(int id) {
        Tab tab = openTabs.get(id);
        if (tab == null) throw new NoSuchElementException("Não existe comanda aberta com o id " + id);
        return tab;
    }

    // Must be called after every change made to a tab
    public void save(Tab tab) {
        repository.save(tab);
    }

    public void close(int id) {
        find(id);
        repository.close(id);
        openTabs.remove(id);
    }

    public Collection<Tab> listOpen() {
        return Collections.unmodifiableCollection(openTabs.values());
    }
}
