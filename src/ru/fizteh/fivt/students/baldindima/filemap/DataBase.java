package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.baldindima.shell.Shell;
import ru.fizteh.fivt.students.baldindima.shell.FileFunctions;

public class DataBase {
    public Map<String, String> getCurrentTable() {
        return currentTable;
    }

    private final Map<String, String> currentTable = new HashMap<String, String>();
    private RandomAccessFile dataBaseFile;

    public void open(Shell shell, FileFunctions fileFunctions) throws IOException {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            throw new IOException("problem with property");
        }
        fileFunctions.toFile(path + File.separator + "db.dat");
        File curDir = fileFunctions.pwd;
        if (!curDir.exists()) {
            if (!curDir.createNewFile()) {
                throw new IOException("can't create base");
            }
        }
        dataBaseFile = new RandomAccessFile(curDir, "rw");
    }

    public void read(Shell shell, FileFunctions fileFunctions) throws IOException {
        open(shell, fileFunctions);
        if (dataBaseFile.length() == 0) {
            return;
        }

        while (dataBaseFile.getFilePointer() < dataBaseFile.length() - 1) {
            int keyLength = dataBaseFile.readInt();
            int valueLength = dataBaseFile.readInt();
            if ((keyLength <= 0) || (valueLength <= 0))
                throw new IOException("wrong format");
            byte[] key;
            byte[] value;
            try {
                key = new byte[keyLength];
                value = new byte[valueLength];
            } catch (OutOfMemoryError e) {
                throw new IOException("too large key or value");
            }
            dataBaseFile.read(key);
            dataBaseFile.read(value);
            String keyString = new String(key, "UTF-8");
            String valueString = new String(value, "UTF-8");
            currentTable.put(keyString, valueString);
        }

    }

    public void write(Shell shell, FileFunctions fileFunctions) throws IOException {
        open(shell, fileFunctions);
        dataBaseFile.getChannel().truncate(0);
        for (Map.Entry<String, String> curPair : getCurrentTable().entrySet()) {
            dataBaseFile.writeInt(curPair.getKey().getBytes("UTF-8").length);
            dataBaseFile.writeInt(curPair.getValue().getBytes("UTF-8").length);
            dataBaseFile.write(curPair.getKey().getBytes("UTF-8"));
            dataBaseFile.write(curPair.getValue().getBytes("UTF-8"));
        }
        dataBaseFile.close();
    }

    public String put(final String keyString, final String valueString) {
        return getCurrentTable().put(keyString, valueString);

    }

    public String get(final String keyString) {
        return getCurrentTable().get(keyString);
    }

    public String remove(final String keyString) {
        return getCurrentTable().remove(keyString);
    }


}
