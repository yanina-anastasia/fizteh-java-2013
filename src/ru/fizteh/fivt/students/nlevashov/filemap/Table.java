package ru.fizteh.fivt.students.nlevashov.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class Table {
    Path addr;
    HashMap<String, String> map;

    Table(Path address) throws IOException {
        addr = address;
        map = new HashMap<String, String>();
        if (Files.exists(addr)) {
            BufferedInputStream i = new BufferedInputStream(Files.newInputStream(addr));
            
            try {
                Vector<String> keys = new Vector<String>();
                Vector<Integer> offsets = new Vector<Integer>();

                int c = i.read();
                int pos = 1;
                if (c != -1) {
                    byte[] key = new byte[1024];
                    int keyLength = 0;
                    do {
                        key[keyLength] = (byte) c;
                        keyLength++;
                        c = i.read();
                        pos++;
                    } while (c != '\0');
                    byte[] shortKey = new byte[keyLength];
                    System.arraycopy(key, 0, shortKey, 0, keyLength);
                    keys.add(new String(shortKey, "UTF8"));

                    offsets.add(i.read() * 256 * 256 * 256 + i.read() * 256 * 256 + i.read() * 256 + i.read());
                    pos += 4;

                    int k = 1;
                    while (!(offsets.get(0) == pos)) {
                        keyLength = 0;
                        c = i.read();
                        pos++;
                        do {
                            key[keyLength] = (byte) c;
                            keyLength++;
                            c = i.read();
                            pos++;
                        } while (c != '\0');
                        shortKey = new byte[keyLength];
                        System.arraycopy(key, 0, shortKey, 0, keyLength);
                        keys.add(new String(shortKey, "UTF8"));

                        offsets.add(i.read() * 256 * 256 * 256 + i.read() * 256 * 256 + i.read() * 256 + i.read());
                        pos += 4;

                        k++;
                    }

                    offsets.add((int) Files.size(addr));
                    for (int j = 0; j < keys.size(); j++) {
                        int valueLength = offsets.get(j + 1).intValue() - offsets.get(j).intValue();
                        byte[] buf = new byte[valueLength];
                        i.read(buf, 0, valueLength);
                        map.put(keys.get(j), new String(buf, "UTF8"));
                    }
                }
            } catch (IOException e) {
                throw e;
            } finally {
                i.close();
            }
        }
    }

    public void refresh() throws IOException {
        Files.deleteIfExists(addr);
        Files.createFile(addr);
        if (!map.isEmpty()) {
            BufferedOutputStream o = new BufferedOutputStream(Files.newOutputStream(addr));
            try {
                byte[][] keys = new byte[map.size()][];
                byte[][] values = new byte[map.size()][];
                Vector<Integer> valuesLengthSum = new Vector<Integer>();
                valuesLengthSum.add(0);
                int head = 0;

                int i = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    keys[i] = entry.getKey().getBytes("UTF8");
                    head += keys[i].length;
                    values[i] = entry.getValue().getBytes("UTF8");
                    valuesLengthSum.add(values[i].length + valuesLengthSum.get(i));
                    i++;
                }
                head += map.size() * 5;

                i = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    o.write(keys[i], 0, keys[i].length);
                    o.write('\0');

                    int offset = head + valuesLengthSum.get(i);
                    o.write((byte) (offset / 256 / 256 / 256));
                    o.write((byte) (offset / 256 / 256 % 256));
                    o.write((byte) (offset / 256 % 256));
                    o.write((byte) (offset % 256));

                    i++;
                }

                i = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    o.write(values[i], 0, values[i].length);
                    i++;
                }
            } catch (IOException e) {
                throw e;
            } finally {
                o.close();
            }
        }
    }

    public String put(String key, String value) {
        return map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public String remove(String key) {
        return map.remove(key);
    }
}
