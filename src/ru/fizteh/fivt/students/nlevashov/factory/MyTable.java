package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

/**
 * Представляет интерфейс для работы с таблицей, содержащей ключи-значения. Ключи должны быть уникальными.
 *
 * Транзакционность: изменения фиксируются или откатываются с помощью методов {@link #commit()} или {@link #rollback()},
 * соответственно. Предполагается, что между вызовами этих методов никаких операций ввода-вывода не происходит.
 *
 * Данный интерфейс не является потокобезопасным.
 */
public class MyTable implements Table {

    Path addr;
    HashMap<String, Storeable> oldMap;
    HashMap<String, Storeable> map;
    String tableName;
    List<Class<?>> types;
    TableProvider provider;

    /**
     * Конструктор. Открывает и читают базу.
     *
     * @param address Адрес таблицы.
     * @param selfProvider Provider текущей таблицы
     *
     * @throws IOException - Ошибка при чтении
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException -
     *                                                          В файле "signature.tsv" встречается неразрешенный тип.
     */
    public MyTable(Path address, TableProvider selfProvider) throws ColumnFormatException, IOException {
        addr = address;
        tableName = addr.getFileName().toString();
        provider = selfProvider;

        String s;
        try (BufferedInputStream i = new BufferedInputStream(Files.newInputStream(addr.resolve("signature.tsv")))) {
            StringBuilder sb = new StringBuilder();
            int c = i.read();
            while (c != -1) {
                sb.append((char) c);
                c = i.read();
            }
            s = sb.toString();
        } catch (IOException e) {
            throw new IOException("Table.constructor: reading error with message \""
                    + e.getMessage() + "\"", e);
        }

        types = new ArrayList<>();
        String[] tokens = s.trim().split(" ");
        for (int j = 0; j < tokens.length; ++j) {
            switch (tokens[j]) {
                case "int":
                    types.add(Integer.class);
                    break;
                case "long":
                    types.add(Long.class);
                    break;
                case "byte":
                    types.add(Byte.class);
                    break;
                case "float":
                    types.add(Float.class);
                    break;
                case "double":
                    types.add(Double.class);
                    break;
                case "boolean":
                    types.add(Boolean.class);
                    break;
                case "String":
                    types.add(String.class);
                    break;
                default:
                    throw new ColumnFormatException("Table.constructor: Illegal type \"" + tokens[j]
                                                    + "\" in \"signature.tsv\"");
            }
        }

        map = new HashMap<>();
        for (int dirNum = 0; dirNum < 16; dirNum++) {
            Path dir = addr.resolve(Integer.toString(dirNum) + ".dir");
            for (int fileNum = 0; fileNum < 16; fileNum++) {
                Path file = dir.resolve(Integer.toString(fileNum) + ".dat");
                if (Files.exists(file)) {
                    try (BufferedInputStream i = new BufferedInputStream(Files.newInputStream(file))) {
                        int c = i.read();
                        if (c != -1) {
                            int pos = 1;
                            Vector<String> keys = new Vector<>();
                            Vector<Integer> offsets = new Vector<>();
                            Vector<Byte> key = new Vector<>();
                            do {
                                key.add((byte) c);
                                c = i.read();
                                pos++;
                            } while (c != '\0');
                            byte[] shortKey = new byte[key.size()];
                            for (int j = 0; j < key.size(); j++) {
                                shortKey[j] = key.get(j);
                            }
                            keys.add(new String(shortKey, "UTF8"));

                            offsets.add((i.read() << 24) + (i.read() << 16) + (i.read() << 8) + i.read());
                            if ((offsets.get(0) >= Files.size(file)) || (offsets.get(0) <= 0)) {
                                throw new IOException("bad offset");
                            }
                            pos += 4;

                            while (!(offsets.get(0) == pos)) {
                                key.clear();
                                c = i.read();
                                pos++;
                                do {
                                    key.add((byte) c);
                                    c = i.read();
                                    pos++;
                                } while (c != '\0');
                                shortKey = new byte[key.size()];
                                for (int j = 0; j < key.size(); j++) {
                                    shortKey[j] = key.get(j);
                                }
                                keys.add(new String(shortKey, "UTF8"));

                                offsets.add((i.read() << 24) + (i.read() << 16) + (i.read() << 8) + i.read());
                                if ((offsets.get(offsets.size() - 1) >= Files.size(file))
                                        || (offsets.get(offsets.size() - 1) <= 0)
                                        || (offsets.get(offsets.size() - 2) >= offsets.get(offsets.size() - 1))) {
                                    throw new IOException("bad offset");
                                }
                                pos += 4;
                            }

                            offsets.add((int) Files.size(file));
                            for (int j = 0; j < keys.size(); j++) {
                                int valueLength = offsets.get(j + 1) - offsets.get(j);
                                byte[] buf = new byte[valueLength];
                                for (int t = 0; t < valueLength; t++) {
                                    buf[t] = (byte) i.read();
                                    if (buf[t] == -1) {
                                        throw new IOException("EOF too early");
                                    }
                                }
                                map.put(keys.get(j), provider.deserialize(this, (new String(buf, "UTF8"))));
                            }
                        }
                    } catch (ParseException e) {
                        throw new IOException("Table.constructor: reading error with message \" value parsing error:"
                                + e.getMessage() + " at " + e.getErrorOffset() + " symbol\"", e);
                    } catch (IOException e) {
                        throw new IOException("Table.constructor: reading error with message \""
                                + e.getMessage() + "\"", e);
                    }
                }
            }
        }
        oldMap = new HashMap<>();
        oldMap.putAll(map);
    }

    /**
     * Возвращает название таблицы.
     *
     * @return Название таблицы.
     */
    @Override
    public String getName() {
        return tableName;
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable get(String key) {
        if ((key == null) || key.trim().isEmpty() || key.matches(".*[\\s\\t\\n].*")) {
            throw new IllegalArgumentException("Table.get: key is null or consists illegal symbol/symbols");
        }
        return map.get(key);
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ для нового значения. Не может быть null.
     * @param value Новое значение. Не может быть null.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException -
     *                                                          при попытке передать Storeable с колонками другого типа.
     */
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if ((key == null) || key.trim().isEmpty() || key.matches(".*[\\s\\t\\n].*")) {
            throw new IllegalArgumentException("Table.put: key is null or consists illegal symbol/symbols");
        }
        if (value == null) {
            throw new IllegalArgumentException("Table.put: value is null");
        }
        try {
            value.getColumnAt(types.size());
            throw new ColumnFormatException("Table.put: value has other number of columns");
        } catch (IndexOutOfBoundsException e) {
            try {                                                                    //!!!!ЗДЕСЬ ОГРОМНЕЙШИЙ КОСТЫЛЬ!!!!
                Storeable temp = value;
                for (int i = 0; i < getColumnsCount(); ++i) {
                    Class<?> c = getColumnType(i);
                    Object o = value.getColumnAt(i);
                    if (o == null) throw new ColumnFormatException("Table.put: null column");
                    if (c != o.getClass()) throw new ColumnFormatException("Table.put: wrong type");
                    /*if (c == Integer.class) {
                        //Integer tempValue = value.getIntAt(i);
                        temp.setColumnAt(i, Integer.valueOf(1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Long.class)  {
                        //Long tempValue = value.getLongAt(i);
                        temp.setColumnAt(i, Long.valueOf((long) 1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Byte.class) {
                        //Byte tempValue = value.getByteAt(i);
                        temp.setColumnAt(i, Byte.valueOf((byte) 1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Float.class) {
                        //Float tempValue = value.getFloatAt(i);
                        temp.setColumnAt(i, Float.valueOf((float) 1.5));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Double.class) {
                        //Double tempValue = value.getDoubleAt(i);
                        temp.setColumnAt(i, Double.valueOf(1.5));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Boolean.class) {
                        //Boolean tempValue = value.getBooleanAt(i);
                        temp.setColumnAt(i, Boolean.valueOf(true));
                        //temp.setColumnAt(i, tempValue);
                    } else {
                        String tempValue = value.getStringAt(i);
                        temp.setColumnAt(i, "abc");
                        temp.setColumnAt(i, tempValue);
                    } */
                }
                return map.put(key, value);
            } catch (IndexOutOfBoundsException e1) {
                throw new ColumnFormatException("Table.put: value has other number of columns");
            }
        }
        //это без проверки на вшивость не верно:
        //return map.put(key, value);
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Предыдущее значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable remove(String key) {
        if ((key == null) || key.trim().isEmpty() || key.matches(".*[\\s\\t\\n].*")) {
            throw new IllegalArgumentException("Table.remove: key is null or consists illegal symbol/symbols");
        }
        return map.remove(key);
    }

    /**
     * Возвращает количество ключей в таблице. Возвращает размер текущей версии, с учётом незафиксированных изменений.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Число записанных изменений.
     *
     * @throws java.io.IOException если произошла ошибка ввода/вывода. Целостность таблицы не гарантируется.
     */
    @Override
    public int commit() throws IOException {
        int difference = mapsDifference();
        oldMap.clear();
        oldMap.putAll(map);
        refreshDiskData();
        return difference;
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Число откаченных изменений.
     */
    @Override
    public int rollback() {
        int difference = mapsDifference();
        map.clear();
        map.putAll(oldMap);
        return difference;
    }

    /**
     * Возвращает количество колонок в таблице.
     *
     * @return Количество колонок в таблице.
     */
    @Override
    public int getColumnsCount() {
        return types.size();
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if ((columnIndex < 0) || (columnIndex >= types.size())) {
            throw new IndexOutOfBoundsException("Storable.getColumnAt: Incorrect index");
        }
        return types.get(columnIndex);
    }

    /**
     * Считает "разницу" map и oldMap, то есть минимальное количество опрераций встаки, переименования и удаления,
     *                                              с помощью которых одну коллекцию можно преобразовать к другой.
     */
    int mapsDifference() {
        int difference = 0;
        for (Map.Entry<String, Storeable> entry : map.entrySet()) {
            if (!oldMap.containsKey(entry.getKey())) {
                difference++;
            } else if (!oldMap.get(entry.getKey()).equals(entry.getValue())) {
                difference++;
            }

        }
        for (Map.Entry<String, Storeable> entry : oldMap.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                difference++;
            }
        }
        return difference;
    }

    /**
     * Записывает изменения в базу
     * @throws IOException Сообщения об ошибках
     */
    void refreshDiskData() throws IOException {
        Vector<Vector<HashMap<String, Storeable>>> parts = new Vector<>();
        for (int dirNum = 0; dirNum < 16; dirNum++) {
            parts.add(new Vector<HashMap<String, Storeable>>());
            for (int fileNum = 0; fileNum < 16; fileNum++) {
                parts.get(dirNum).add(new HashMap<String, Storeable>());
            }
        }
        for (Map.Entry<String, Storeable> entry : map.entrySet()) {
            int hash = entry.getKey().hashCode();
            hash *= Integer.signum(hash);
            parts.get(hash % 16).get(hash / 16 % 16).put(entry.getKey(), entry.getValue());
        }
        for (int dirNum = 0; dirNum < 16; dirNum++) {
            String directoryName = Integer.toString(dirNum) + ".dir";
            if (!Files.exists(addr.resolve(directoryName))) {
                Shell.cd(addr.toString());
                Shell.mkdir(directoryName);
            }
            boolean flag = true;
            for (int fileNum = 0; fileNum < 16; fileNum++) {
                Path file = addr.resolve(directoryName).resolve(Integer.toString(fileNum) + ".dat");
                if (parts.get(dirNum).get(fileNum).isEmpty()) {
                    Files.deleteIfExists(file);
                } else {
                    Files.deleteIfExists(file);
                    Files.createFile(file);
                    if (!parts.get(dirNum).get(fileNum).isEmpty()) {
                        try (BufferedOutputStream o = new BufferedOutputStream(Files.newOutputStream(file))) {
                            byte[][] keys = new byte[parts.get(dirNum).get(fileNum).size()][];
                            byte[][] values = new byte[parts.get(dirNum).get(fileNum).size()][];
                            Vector<Integer> valuesLengthSum = new Vector<>();
                            valuesLengthSum.add(0);
                            int head = 0;

                            int i = 0;
                            for (Map.Entry<String, Storeable> entry : parts.get(dirNum).get(fileNum).entrySet()) {
                                keys[i] = entry.getKey().getBytes("UTF8");
                                head += keys[i].length;
                                values[i] = provider.serialize(this, entry.getValue()).getBytes("UTF8");
                                valuesLengthSum.add(values[i].length + valuesLengthSum.get(i));
                                i++;
                            }
                            head += parts.get(dirNum).get(fileNum).size() * 5;

                            for (i = 0; i < parts.get(dirNum).get(fileNum).size(); i++) {
                                o.write(keys[i], 0, keys[i].length);
                                o.write('\0');

                                int offset = head + valuesLengthSum.get(i);
                                o.write((byte) (offset >>> 24));
                                o.write((byte) ((offset >>> 16) % 256));
                                o.write((byte) ((offset >>> 8) % 256));
                                o.write((byte) (offset % 256));
                            }

                            for (i = 0; i < parts.get(dirNum).get(fileNum).size(); i++) {
                                o.write(values[i], 0, values[i].length);
                            }
                        }
                    }
                    flag = false;
                }
            }
            if (flag) {
                Shell.cd(addr.toString());
                Shell.rm(directoryName);
            }
        }
    }
}
