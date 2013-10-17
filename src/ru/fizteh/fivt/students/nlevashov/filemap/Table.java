package ru.fizteh.fivt.students.nlevashov.DbMain;

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
                while (!(offsets.get(0) + k * 4 == pos)) {
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

                offsets.add((int) (Files.size(addr) - k * 4));
                for (int j = 0; j < keys.size(); j++) {
                    int valueLength = offsets.get(j + 1).intValue() - offsets.get(j).intValue();
                    byte[] buf = new byte[valueLength];
                    i.read(buf, 0, valueLength);
                    map.put(keys.get(j), new String(buf, "UTF8"));
                }
            }

            i.close();
        } else {
            throw new IOException("File with database doesn't exist");
        }
    }

    public void refresh() throws IOException {
        Files.deleteIfExists(addr);
        Files.createFile(addr);
        if (!map.isEmpty()) {
            BufferedOutputStream o = new BufferedOutputStream(Files.newOutputStream(addr));
            Vector<Integer> valuesLengthSum = new Vector<Integer>();
            valuesLengthSum.add(0);
            int i = 1;
            int head = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                head += entry.getKey().getBytes("UTF8").length;
                valuesLengthSum.add(entry.getValue().getBytes("UTF8").length + valuesLengthSum.get(i - 1));
                i++;
            }
            head += map.size();

            i = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                o.write(entry.getKey().getBytes("UTF8"), 0, entry.getKey().getBytes("UTF8").length);
                o.write('\0');

                int offset = head + valuesLengthSum.get(i);
                o.write((byte) (offset / 256 / 256 / 256));
                o.write((byte) (offset / 256 / 256 % 256));
                o.write((byte) (offset / 256 % 256));
                o.write((byte) (offset % 256));

                i++;
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                o.write(entry.getValue().getBytes("UTF8"), 0, entry.getValue().getBytes("UTF8").length);
            }

            o.close();
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
