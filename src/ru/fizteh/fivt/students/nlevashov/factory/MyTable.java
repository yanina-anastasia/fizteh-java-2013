package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MyTable implements Table {

    Path addr;
    HashMap<String, String> oldMap;
    HashMap<String, String> map;
    String tableName;

    /**
     * Конструктор. Открывает и читают базу.
     *
     * @param address Адрес таблицы.
     */
    public MyTable(Path address) {
        addr = address;
        tableName = addr.getFileName().toString();
        map = new HashMap<String, String>();

        for (int dirNum = 0; dirNum < 16; dirNum++) {
            Path dir = addr.resolve(Integer.toString(dirNum) + ".dir");
            for (int fileNum = 0; fileNum < 16; fileNum++) {
                Path file = dir.resolve(Integer.toString(fileNum) + ".dat");
                if (Files.exists(file)) {
                    try (BufferedInputStream i = new BufferedInputStream(Files.newInputStream(file))) {
                        int c = i.read();
                        if (c != -1) {
                            int pos = 1;
                            Vector<String> keys = new Vector<String>();
                            Vector<Integer> offsets = new Vector<Integer>();
                            Vector<Byte> key = new Vector<Byte>();
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
                                int valueLength = offsets.get(j + 1).intValue() - offsets.get(j).intValue();
                                byte[] buf = new byte[valueLength];
                                for (int t = 0; t < valueLength; t++) {
                                    buf[t] = (byte) i.read();
                                    if (buf[t] == -1) {
                                        throw new IOException("EOF too early");
                                    }
                                }
                                map.put(keys.get(j), new String(buf, "UTF8"));
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Table.constructor: reading error with message \"" + e.getMessage() + "\"");
                    }
                }
            }
        }
        oldMap = new HashMap<String, String>();
        oldMap.putAll(map);
    }

    /**
     * Возвращает название базы данных.
     */
    @Override
    public String getName() {
        return tableName;
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public String get(String key) {
        if ((key == null) || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Table.get: key is null");
        }
        return map.get(key);
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
    @Override
    public String put(String key, String value) {
        if ((key == null) || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Table.put: key is null");
        }
        if ((value == null) || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Table.put: value is null");
        }
        return map.put(key, value);
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public String remove(String key) {
        if ((key == null) || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Table.remove: key is null");
        }
        return map.remove(key);
    }

    /**
     * Возвращает количество ключей в таблице.
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
     * @return Количество сохранённых ключей.
     */
    @Override
    public int commit() {
        int oldSize = oldMap.size();
        oldMap.clear();
        oldMap.putAll(map);
        try {
            refreshDiskData();
        } catch (Exception e) {
            throw new RuntimeException("Table.commit: writing on disk error with message \"" + e.getMessage() + "\"");
        }
        return (map.size() - oldSize);
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    @Override
    public int rollback() {
        int difference = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!oldMap.containsKey(entry.getKey())) {
                difference++;
            } else if (!oldMap.get(entry.getKey()).equals(entry.getValue())) {
                difference++;
            }

        }
        for (Map.Entry<String, String> entry : oldMap.entrySet()) {
            if (!oldMap.containsKey(entry.getKey())) {
                difference++;
            }
        }

        map.clear();
        map.putAll(oldMap);
        //System.out.println(tableName + " --" + oldMap.size() + " | " + newSize);
        return difference;
    }

    /**
     * Записывает изменения в базу
     */
    void refreshDiskData() throws Exception {
        Vector<Vector<HashMap<String, String>>> parts = new Vector<Vector<HashMap<String, String>>>();
        for (int dirNum = 0; dirNum < 16; dirNum++) {
            parts.add(new Vector<HashMap<String, String>>());
            for (int fileNum = 0; fileNum < 16; fileNum++) {
                parts.get(dirNum).add(new HashMap<String, String>());
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
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
                            Vector<Integer> valuesLengthSum = new Vector<Integer>();
                            valuesLengthSum.add(0);
                            int head = 0;

                            int i = 0;
                            for (Map.Entry<String, String> entry : parts.get(dirNum).get(fileNum).entrySet()) {
                                keys[i] = entry.getKey().getBytes("UTF8");
                                head += keys[i].length;
                                values[i] = entry.getValue().getBytes("UTF8");
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
