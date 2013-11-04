package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MyTable extends State implements Table {
    private HashMap<String, String> newEntries;
    private HashMap<String, String> changedEntries;
    private HashMap<String, String> removedEntries;

    MyTable(String name) {
        super(name);
        newEntries = new HashMap<String, String>();
        changedEntries = new HashMap<String, String>();
        removedEntries = new HashMap<String, String>();
    }

    public String getName(){
        return super.getTableName();
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (removedEntries.get(key) != null) {
            return null;
        }
        if (newEntries.get(key) != null) {
            return newEntries.get(key);
        }
        if (changedEntries.get(key) != null) {
            return changedEntries.get(key);
        }
        return super.get(key);
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ.
     * @param value Значение.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     */
    public String put(String key, String value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }

        if (super.get(key) == null) {
            return newEntries.put(key, value);
        } else {
            if (super.get(key).equals(value)) {
                changedEntries.remove(key);
                removedEntries.remove(key);
                return value;
            }
            if (removedEntries.get(key) != null) {
                removedEntries.remove(key);
            }
            if (changedEntries.get(key) == null) {
                changedEntries.put(key, value);
                return super.get(key);
            } else {
                return changedEntries.put(key, value);
            }
        }
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (removedEntries.get(key) != null) {
            return null;
        }
        if (newEntries.get(key) != null) {
            return newEntries.remove(key);
        }
        if (changedEntries.get(key) != null) {
            removedEntries.put(key, super.get(key));
            return changedEntries.remove(key);
        }
        if (super.get(key) != null) {
            removedEntries.put(key, super.get(key));
            return super.get(key);
        }
        return null;
    }

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    public int size() {
        return super.getEntrySet().size() - removedEntries.size() + newEntries.size();
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    public int commit() {
        int changed = newEntries.size() + changedEntries.size() + removedEntries.size();

        for (Map.Entry<String, String> entry : newEntries.entrySet()) {
            super.put(entry.getKey(), entry.getValue());
        }
        newEntries.clear();

        for (Map.Entry<String, String> entry : changedEntries.entrySet()) {
            super.put(entry.getKey(), entry.getValue());
        }
        changedEntries.clear();

        for (Map.Entry<String, String> entry : removedEntries.entrySet()) {
            super.remove(entry.getKey());
        }
        removedEntries.clear();

        return changed;
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    public int rollback() {
        int changed = newEntries.size() + changedEntries.size() + removedEntries.size();

        newEntries.clear();
        changedEntries.clear();
        removedEntries.clear();

        return changed;
    }
}
