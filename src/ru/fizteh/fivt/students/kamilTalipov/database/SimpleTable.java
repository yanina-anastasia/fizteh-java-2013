package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.storage.strings.Table;
import static ru.fizteh.fivt.students.kamilTalipov.database.InputStreamUtils.readInt;
import static ru.fizteh.fivt.students.kamilTalipov.database.InputStreamUtils.readString;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class SimpleTable implements Table {
    public SimpleTable(String workingDirectory) throws FileNotFoundException, DatabaseException {
        try {
            dbFile = FileUtils.makeFile(workingDirectory, "db.dat");
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Couldn't open db.dat file");
        }
        table = new HashMap<String, String>();
        readTable();
    }

    @Override
    public String getName() {
        return "SimpleTable";
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int commit() {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    public void exit() {
        try {
            writeTable();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void readTable() throws FileNotFoundException, DatabaseException {
        FileInputStream input = new FileInputStream(dbFile);
        try {
            while (input.available() > 0) {
                int keyLen = readInt(input);
                int valueLen = readInt(input);
                String key = readString(input, keyLen);
                String value = readString(input, valueLen);
                table.put(key, value);
            }
        } catch (IOException e) {
            throw new DatabaseException("Database file have incorrect format");
        } finally {
            try {
                input.close();
            }  catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void writeTable() throws IOException {
        FileOutputStream output = new FileOutputStream(dbFile);
        try {
            for (Map.Entry<String, String> entry : table.entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                output.write(ByteBuffer.allocate(4).putInt(key.length).array());
                output.write(ByteBuffer.allocate(4).putInt(value.length).array());
                output.write(key);
                output.write(value);
            }
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private final File dbFile;
    private HashMap<String, String> table;
}
