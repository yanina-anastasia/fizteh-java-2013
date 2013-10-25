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

    public Table(Path address) throws IOException {
        addr = address;
        map = new HashMap<String, String>();
        if (Files.exists(addr)) {
            BufferedInputStream i = new BufferedInputStream(Files.newInputStream(addr));
            try {
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
                    if ((offsets.get(0) >= Files.size(addr)) || (offsets.get(0) <= 0)) {
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
                        if ((offsets.get(offsets.size() - 1) >= Files.size(addr))
                                || (offsets.get(offsets.size() - 1) <= 0)
                                || (offsets.get(offsets.size() - 2) >= offsets.get(offsets.size() - 1))) {
                            throw new IOException("bad offset");
                        }
                        pos += 4;
                    }

                    offsets.add((int) Files.size(addr));
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

                for (i = 0; i < map.size(); i++) {
                    o.write(keys[i], 0, keys[i].length);
                    o.write('\0');

                    int offset = head + valuesLengthSum.get(i);
                    o.write((byte) (offset >>> 24));
                    o.write((byte) ((offset >>> 16) % 256));
                    o.write((byte) ((offset >>> 8) % 256));
                    o.write((byte) (offset % 256));
                }

                for (i = 0; i < map.size(); i++) {
                    o.write(values[i], 0, values[i].length);
                }
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

    public boolean isEmpty() {
        return (map.size() == 0);
    }

    public Path getAddress() {
        return addr;
    }
}
