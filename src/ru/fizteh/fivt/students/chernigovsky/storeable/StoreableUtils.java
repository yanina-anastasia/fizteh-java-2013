package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.text.ParseException;

public class StoreableUtils {

    public static boolean checkValue(Table table, Storeable value) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                value.getColumnAt(i);
            } catch (IndexOutOfBoundsException ex) {
                return false;
            }
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException ex) {
            return true;
        }
        return false;
    }

    public static void readTable(ExtendedStoreableTable table, StoreableTableProvider tableProvider) throws IOException{

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File tableFolder = new File(tableProvider.getDbDirectory(), table.getName());
            File directory = new File(tableFolder, directoryNumber.toString() + ".dir");
            if (!directory.exists()) {
                continue;
            }
            if (!directory.isDirectory()) {
                throw new IOException("Corrupted database");
            }

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                File file = new File(directory, fileNumber.toString() + ".dat");
                if (!file.exists()) {
                    continue;
                }
                if (!file.isFile()) {
                    throw new IOException("Corrupted database");
                }

                FileInputStream fileInputStream = new FileInputStream(file);
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
                        if (Math.abs(keyBytes[0]) % 16 != directoryNumber || Math.abs(keyBytes[0]) / 16 % 16 != fileNumber) {
                            throw new IOException("Corrupted database");
                        }

                        String key = new String(keyBytes, "UTF-8");
                        String value = new String(valueBytes, "UTF-8");

                        MyStoreable deserializedValue;

                        try {
                            deserializedValue = tableProvider.deserialize(table, value);
                        } catch (ParseException ex) {
                            throw new IOException("Corrupted database");
                        }
                        table.put(key, deserializedValue);
                    }
                } finally {
                    dataInputStream.close();
                }

            }

        }
    }
}
