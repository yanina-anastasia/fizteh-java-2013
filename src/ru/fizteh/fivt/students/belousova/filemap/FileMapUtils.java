package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

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
            BufferedInputStream bis = new BufferedInputStream(is, 4096);
            DataInputStream dis = new DataInputStream(bis);

            int fileLength = (int) file.length();

            try {
                int position = 0;
                String key1 = readKey(dis);
                position += key1.length();
                int offset1 = dis.readInt();
                int firstOffset = offset1;
                position += 5;

                while (position != firstOffset) {
                    String key2 = readKey(dis);
                    position += key2.length();
                    int offset2 = dis.readInt();
                    position += 5;
                    String value = readValue(dis, offset1, offset2, position, fileLength);
                    map.put(key1, value);
                    offset1 = offset2;
                    key1 = key2;
                }
                String value = readValue(dis, offset1, fileLength, position, fileLength);
                map.put(key1, value);
            } finally {
                FileUtils.closeStream(dis);
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
        String key = convertBytesToString(buf, "UTF_8");
        return key;
    }

    private static String readValue(DataInputStream dis, int offset1,
                                    int offset2, int position, int length) throws IOException {
        dis.mark(length);
        dis.skip(offset1 - position);
        byte[] buffer = new byte[offset2 - offset1];
        dis.read(buffer);
        String value = new String(buffer, "UTF_8");
        dis.reset();
        return value;
    }

    public static void write(File file, Map<String, String> map) throws IOException {
        try {
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os, 4096);
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                long offset = 0;
                for (String key : map.keySet()) {
                    offset += key.getBytes("UTF_8").length + 5;
                }

                List<String> values = new ArrayList<String>(map.keySet().size());
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    values.add(value);
                    dos.write(key.getBytes("UTF_8"));
                    dos.writeByte(0);
                    dos.writeInt((int) offset);
                    offset += value.getBytes("UTF_8").length;
                }

                for (String value : values) {
                    dos.write(value.getBytes());
                }
            } finally {
                FileUtils.closeStream(dos);
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
            buf[i] = b;
            i++;
        }
        String s = new String(buf, Encoding);
        return s;
    }
}
