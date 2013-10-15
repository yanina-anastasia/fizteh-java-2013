package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;

public final class DataBase implements SimpleDataBase {
    private String dataBaseDirectory;
    private static String dataBaseFileName = "db.dat";
    private DataBaseFile fileDb;

    public DataBase(final String dbDirectory) throws IOException {
        dataBaseDirectory = dbDirectory;
        fileDb = new DataBaseFile(dataBaseDirectory + File.separator + dataBaseFileName);
    }

    @Override
    public String put(final String keyStr, final String valueStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory + File.separator + dataBaseFileName);
        String result = file.put(keyStr, valueStr);
        file.save();
        return result;
    }

    @Override
    public String get(final String keyStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory + File.separator + dataBaseFileName);
        return file.get(keyStr);
    }

    @Override
    public boolean remove(final String keyStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory + File.separator + dataBaseFileName);
        boolean result = file.remove(keyStr);
        file.save();
        return result;
    }

    @Override
    public boolean exist() {
        return true;
    }

    public String getBaseDirectory() {
        return dataBaseDirectory;
    }
}
