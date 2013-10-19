package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class State {
    private HashMap<String, String> hashMap;
    public State() {
        hashMap = new HashMap<String, String>();
    }
    public String put(String key, String value) {
        return hashMap.put(key, value);
    }
    public String get(String key) {
        return hashMap.get(key);
    }
    public String remove(String key) {
        return hashMap.remove(key);
    }

    public void readTable(File dbName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(dbName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        try {
            while (true) {
                int keyLength;
                int valueLength;

                try {
                    keyLength = dataInputStream.readInt();
                } catch (EOFException ex) {
                    break;
                }
                valueLength = dataInputStream.readInt();

                if (keyLength <= 0 || valueLength <= 0 || keyLength > 1048576 || valueLength > 1048576) {
                    throw new IOException("Wrong string size");
                }
                byte[] keyBytes = new byte[keyLength];
                byte[] valueBytes = new byte[valueLength];

                dataInputStream.readFully(keyBytes);
                dataInputStream.readFully(valueBytes);

                if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                    throw new IOException("Corrupted database");
                }
                String key = new String(keyBytes, "UTF-8");
                String value = new String(valueBytes, "UTF-8");
                this.put(key, value);
            }
        } finally {
            dataInputStream.close();
        }
    }

    public void writeTable(File dbName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbName);
        fileOutputStream.getChannel().truncate(0); // Clear file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        try {
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
            }
        } finally {
            dataOutputStream.close();
        }

    }

}
