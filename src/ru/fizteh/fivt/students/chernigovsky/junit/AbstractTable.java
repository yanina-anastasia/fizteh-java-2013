package ru.fizteh.fivt.students.chernigovsky.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTable<ValueType> {
    private HashMap<String, ValueType> hashMap;
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
        changedEntries = new HashMap<String, ValueType>();
        removedEntries = new HashMap<String, ValueType>();
    }

    public String getName(){
        return tableName;
    }

    public int getDiffCount() {
        int diffCount = 0;

        for (String string : changedEntries.keySet()) {
            if (hashMap.get(string) == null || hashMap.get(string) != changedEntries.get(string)) {
                ++diffCount;
            }
        }

        for (String string : removedEntries.keySet()) {
            if (hashMap.get(string) != null) {
                ++diffCount;
            }
        }

        return diffCount;
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
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(key);
        if (key.isEmpty() || matcher.find()) {
            throw new IllegalArgumentException("key is wrong");
        }

        if (removedEntries.get(key) != null) {
            return null;
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
        ValueType oldValue = get(key);
        ValueType commitedValue = hashMap.get(key);

        changedEntries.put(key, value);
        removedEntries.remove(key);

        return oldValue;
    }

    public ValueType put(String key, ValueType value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(key);
        if (key.isEmpty() || matcher.find()) {
            throw new IllegalArgumentException("key is wrong");
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
        ValueType oldValue = get(key);

        if (oldValue != null) {
            removedEntries.put(key, oldValue);
        }
        changedEntries.remove(key);

        return oldValue;
    }

    public ValueType remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(key);
        if (key.isEmpty() || matcher.find()) {
            throw new IllegalArgumentException("key is wrong");
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
        int size = hashMap.size();

        for (String string : changedEntries.keySet()) {
            if (hashMap.get(string) == null || hashMap.get(string) != changedEntries.get(string)) {
                ++size;
            }
        }

        for (String string : removedEntries.keySet()) {
            if (hashMap.get(string) != null) {
                --size;
            }
        }

        return size;
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    public int commit() {
        int changed = getDiffCount();

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

        changedEntries.clear();
        removedEntries.clear();

        return changed;
    }
}
