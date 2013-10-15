package ru.fizteh.fivt.students.valentinbarishev.multifilehashmap;

import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseException;
import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseFile;
import java.io.File;
import java.io.IOException;

public final class MultiDataBase {
    private String dataBaseDirectory;

    public MultiDataBase(final String dbDirectory) throws IOException {
        dataBaseDirectory = dbDirectory;
    }

    private String getNDirectory(final int key) {
        return Integer.toString(key % 16) + ".dir";
    }

    private String getNFile(final int key) {
        return Integer.toString((key / 16) % 16) + ".dat";
    }

    private void tryAddDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new DataBaseException("Cannot create a directory!");
            }
        }
    }

    private void tryDeleteDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new DataBaseException("Cannot delete a directory!");
                }
            }
        }
    }

    private String getFullName(final int key) {
        return dataBaseDirectory + File.separator + getNDirectory(key) + File.separator + getNFile(key);
    }

    public String put(final String keyStr, final String valueStr) {
        byte key = (byte) keyStr.charAt(0);
        tryAddDirectory(getNDirectory(key));
        DataBaseFile file = new DataBaseFile(getFullName(key));
        String result = file.put(keyStr, valueStr);
        file.save();
        return result;
    }

    public String get(final String keyStr) {
        byte key = (byte) keyStr.charAt(0);
        tryAddDirectory(getNDirectory(key));
        DataBaseFile file = new DataBaseFile(getFullName(key));
        return file.get(keyStr);
    }

    public boolean remove(final String keyStr) {
        byte key = (byte) keyStr.charAt(0);
        tryAddDirectory(getNDirectory(key));
        DataBaseFile file = new DataBaseFile(getFullName(key));
        boolean result = file.remove(keyStr);
        file.save();
        tryDeleteDirectory(getNDirectory(key));
        return result;
    }

    public void drop() {
        for (byte i = 0; i < 16; ++i) {
            for (byte j = 0; j < 16; ++j) {
                File file = new File(getFullName(i + j * 16));
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new DataBaseException("Cannot delete a file!");
                    }
                }
            }
            tryDeleteDirectory(getNDirectory(i));
        }
    }

    public String getBaseDirectory() {
        return dataBaseDirectory;
    }
}
