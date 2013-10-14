package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;
import ru.fizteh.fivt.students.belousova.utils.ShellUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileMapUtils {
    public static void read(File file, Map<String, String> map) throws IOException {
        if (file.length() == 0) return;
        try {
            InputStream is = new FileInputStream(file);
            try {
                BufferedInputStream bis = new BufferedInputStream(is, 4096);
                try {
                    DataInputStream dis = new DataInputStream(bis);
                    try {
                        long position = 0;
                        String key1 = readKey(dis);
                        position += key1.length();
                        long offset1 = dis.readInt();
                        long firstValue = offset1;
                        position += 5;

                        while (position != firstValue) {
                            String key2 = readKey(dis);
                            position += key2.length();
                            long offset2 = dis.readInt();
                            position += 5;
                            String value = readValue(dis, offset1, offset2, position);
                            map.put(key1, value);
                            offset1 = offset2;
                            key1 = key2;
                        }
                        String value = readValue(dis, offset1, file.length(), position);
                        map.put(key1, value);
                    } finally {
                        FileUtils.closeStream(dis);
                    }
                } finally {
                    FileUtils.closeStream(bis);
                }
            } finally {
                FileUtils.closeStream(is);
            }

        } catch (IOException e) {
            throw new IOException("cannot read '" + file.getName() + "'", e);
        }
    }

    private static String readKey(DataInputStream dis) throws IOException {
        List<Byte> buf = new ArrayList<Byte>();
        byte b = dis.readByte();
        while (b != 0) {
            buf.add(b);
            b = dis.readByte();
        }
        String key = convertBytesToString(buf, "UTF-8");
        return key;
    }

    private static String readValue(DataInputStream dis, long offset1,
                                    long offset2, long position) throws IOException {
        dis.mark(1024 * 1024);
        dis.skip(offset1 - position);
        byte[] buffer = new byte[(int) (offset2 - offset1)];
        dis.read(buffer);
        String value = new String(buffer, "UTF-8");
        dis.reset();
        return value;
    }

    public static void write(File file, Map<String, String> map) throws IOException {
        try {
            OutputStream os = new FileOutputStream(file);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(os, 4096);
                try {
                    DataOutputStream dos = new DataOutputStream(bos);
                    try {
                        long offset = 0;
                        for (String key : map.keySet()) {
                            offset += key.getBytes("UTF-8").length + 5;
                        }

                        List<String> values = new ArrayList<String>(map.keySet().size());
                        for (String key : map.keySet()) {
                            String value = map.get(key);
                            values.add(value);
                            dos.write(key.getBytes("UTF-8"));
                            dos.writeByte(0);
                            dos.writeInt((int) offset);
                            offset += value.getBytes("UTF-8").length;
                        }

                        for (String value : values) {
                            dos.write(value.getBytes());
                        }
                    } finally {
                        FileUtils.closeStream(dos);
                    }
                } finally {
                    FileUtils.closeStream(bos);
                }
            } finally {
                FileUtils.closeStream(os);
            }
        } catch (IOException e) {
            throw new IOException("cannot write '" + file.getName() + "'", e);
        }
    }

    public static String convertBytesToString(Collection<Byte> collection,
                                              String Encoding) throws UnsupportedEncodingException {
        byte[] buf = new byte[collection.size()];
        int i = 0;
        for (Byte b : collection) {
            buf[i] = (byte) b;
            i++;
        }
        String s = new String(buf, Encoding);
        return s;
    }
}
