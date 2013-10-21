package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

public class TableController {
    private final long MAX_FILE_SIZE = 100000000;

    public TableController(Path databasePath, String tableName) {
        File newTable = databasePath.resolve(tableName).toFile();
        if (newTable.exists()) {
            newTable.mkdir();
        }
    }

    public void readTable(MultiFileHashMapState state) throws IOException {
        File[] files = new File[16];
        for (int i = 0; i < 16; ++i) {
            files[i] = state.getWorkingPath().resolve(i + ".dat").toFile();
            if (!files[i].exists()) {
                files[i].createNewFile();
            }
        }
        RandomAccessFile[] table = new RandomAccessFile[16];
        int inputLength = 0;
        for (int i = 0; i < 16; ++i) {
            table[i] = new RandomAccessFile(files[i], "r");
            inputLength += table[i].length();
        }
        if (inputLength > MAX_FILE_SIZE) {
            Set<String> keySet = state.getMap().keySet();
            for (String key : keySet) {
                state.delValue(key);
            }
            closeDescriptors(table);
            deleteUnnecessaryFiles(files);
            throw new IOException("Too big database file.");
        }
        for (int i = 0; i < 16; ++i) {
            readFile(state, table[i]);
        }
        closeDescriptors(table);
        deleteUnnecessaryFiles(files);
    }

    private void readFile(MultiFileHashMapState state, RandomAccessFile database) throws IOException {
        int keyLength;
        int valueLength;
        String key;
        String value;
        while (database.getFilePointer() != database.length()) {
            keyLength = database.readInt();
            if (keyLength < 1 || keyLength > database.length() - database.getFilePointer() + 4) {
                database.close();
                throw new IOException("Incorrect key length in input.");
            }
            valueLength = database.readInt();
            if (valueLength < 1 || valueLength > database.length() - database.getFilePointer() + 4) {
                database.close();
                throw new IOException("Incorrect value length in input.");
            }

            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = new String(keySymbols, StandardCharsets.UTF_8);
            value = new String(valueSymbols, StandardCharsets.UTF_8);
            state.putValue(key, value);
        }
        database.close();
    }

    public void fillTable(MultiFileHashMapState state) throws IOException {
        RandomAccessFile[] database = new RandomAccessFile[16];
        File[] files = new File[16];
        for (int i = 0; i < 16; ++i) {
            files[i] = state.getWorkingPath().resolve(i + ".dat").toFile();
            if (!files[i].exists()) {
                files[i].createNewFile();
            }
            database[i] = new RandomAccessFile(files[i], "rw");
            database[i].setLength(0);
        }
        Set<String> keySet = state.getMap().keySet();
        for (String key : keySet) {
            byte b = key.getBytes()[0];
            int fileNumber = b / 16 % 16;
            if (database[fileNumber].getFilePointer() > MAX_FILE_SIZE) {
                closeDescriptors(database);
                deleteUnnecessaryFiles(files);
                throw new IOException("Too big database file.");
            }
            database[fileNumber].writeInt(key.getBytes(StandardCharsets.UTF_8).length);
            database[fileNumber].writeInt(state.getValue(key).getBytes(StandardCharsets.UTF_8).length);
            database[fileNumber].write(key.getBytes(StandardCharsets.UTF_8));
            database[fileNumber].write(state.getValue(key).getBytes(StandardCharsets.UTF_8));
        }
        closeDescriptors(database);
        deleteUnnecessaryFiles(files);
        File dir = state.getWorkingPath().toFile();
        if (dir.listFiles().length == 0) {
            dir.delete();
        }
    }

    private void deleteUnnecessaryFiles(File[] files) throws IOException {
        for (int i = 0; i < 16; ++i) {
            RandomAccessFile f = new RandomAccessFile(files[i], "rw");
            if (f.length() == 0) {
                f.close();
                files[i].delete();
            }
        }
    }

    private void closeDescriptors(RandomAccessFile[] files) throws IOException {
        for (int i = 0; i < 16; ++i) {
            files[i].close();
        }
    }
}