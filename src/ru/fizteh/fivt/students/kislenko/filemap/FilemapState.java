package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.State;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class FilemapState extends State {
    private Map<String, String> storage;
    private Path path;

    public FilemapState(Path p) {
        storage = new TreeMap<String, String>();
        path = p;
    }

    public String getValue(String key) {
        return storage.get(key);
    }

    public void putValue(String key, String value) {
        storage.put(key, value);
    }

    public void delValue(String key) {
        storage.remove(key);
    }

    public boolean hasKey(String key) {
        return storage.containsKey(key);
    }

    public Map<String, String> getMap() {
        return storage;
    }

    public void setPath(Path p) {
        path = p;
    }

    public Path getPath() {
        return path;
    }
}
