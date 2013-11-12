package ru.fizteh.fivt.students.chernigovsky.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTable<ValueType> {
    private HashMap<String, ValueType> hashMap;
    private HashMap<String, ValueType> newEntries;
    private HashMap<String, ValueType> changedEntries;
    private HashMap<String, ValueType> removedEntries;
    private boolean autoCommit;
    private String tableName;

    public Set<Map.Entry<String, ValueType>> getEntrySet() {
        return hashMap.entrySet();
    }

    public AbstractTable(String name, boolean flag) {
        tableName = name;
        autoCommit = flag;
        hashMap = new HashMap<String, ValueType>();
        newEntries = new HashMap<String, ValueType>();
        changedEntries = new HashMap<String, ValueType>();
        removedEntries = new HashMap<String, ValueType>();
    }

    public String getName(){
        return tableName;
    }

    public int getDiffCount() {
        return newEntries.size() + changedEntries.size() + removedEntries.size();
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    public ValueType get(String key) {
        if (key == null || key.trim().isEmpty()) {
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
        return hashMap.get(key);
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

    private ValueType putting(String key, ValueType value) {
        if (hashMap.get(key) == null) {
            return newEntries.put(key, value);
        } else {
            if (hashMap.get(key).equals(value)) {
                if (changedEntries.get(key) != null) {
                    return changedEntries.remove(key);
                }
                if (removedEntries.get(key) != null) {
                    removedEntries.remove(key);
                    return null;
                }
                return value;
            }
            if (removedEntries.get(key) != null) {
                removedEntries.remove(key);
                changedEntries.put(key, value);
                return null;
            }
            if (changedEntries.get(key) == null) {
                changedEntries.put(key, value);
                return hashMap.get(key);
            } else {
                return changedEntries.put(key, value);
            }
        }

    }

    public ValueType put(String key, ValueType value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) { // maybe need to check: value.trim().isEmpty()
            throw new IllegalArgumentException("value is null");
        }

        ValueType ans = putting(key, value);
        if (autoCommit) {
            commit();
        }
        return ans;

    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */

    private ValueType removing(String key) {
        if (removedEntries.get(key) != null) {
            return null;
        }
        if (newEntries.get(key) != null) {
            return newEntries.remove(key);
        }
        if (changedEntries.get(key) != null) {
            removedEntries.put(key, hashMap.get(key));
            return changedEntries.remove(key);
        }
        if (hashMap.get(key) != null) {
            removedEntries.put(key, hashMap.get(key));
            return hashMap.get(key);
        }
        return null;
    }

    public ValueType remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        ValueType ans = removing(key);
        if (autoCommit) {
            commit();
        }
        return ans;

    }

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    public int size() {
        return hashMap.size() - removedEntries.size() + newEntries.size();
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    public int commit() {
        int changed = getDiffCount();

        for (Map.Entry<String, ValueType> entry : newEntries.entrySet()) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        newEntries.clear();

        for (Map.Entry<String, ValueType> entry : changedEntries.entrySet()) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        changedEntries.clear();

        for (Map.Entry<String, ValueType> entry : removedEntries.entrySet()) {
            hashMap.remove(entry.getKey());
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
        int changed = getDiffCount();

        newEntries.clear();
        changedEntries.clear();
        removedEntries.clear();

        return changed;
    }
}
