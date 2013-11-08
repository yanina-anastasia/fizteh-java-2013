package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileReader;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileWriter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StoreableTable implements Table {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    private StoreableTableProvider tableProvider;
    private UniversalDataTable<Storeable> dataTable;
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();

    public StoreableTable() throws IOException {
        dataTable = new UniversalDataTable<Storeable>();
    }

    public StoreableTable(String name) throws IOException {
        dataTable = new UniversalDataTable<Storeable>(name);
    }

    public StoreableTable(String name, File dir, List<Class<?>> types, StoreableTableProvider provider) {
        dataTable = new UniversalDataTable<Storeable>(name, dir);
        columnTypes = types;
        tableProvider = provider;

    }

    public String getName() {
        return dataTable.getName();
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (key.matches("\\s") || (key.split("\\s+")).length > 1)) {
            throw new IllegalArgumentException("Not correct key or value");
        }
        for (int i = 0; i < getColumnsCount(); ++i) {

        }
        return dataTable.put(key, value);
    }

    public Set<String> getKeys() {
        return dataTable.getKeys();
    }

    public Storeable get(String key) throws IllegalArgumentException {
        return dataTable.get(key);
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        return dataTable.remove(key);
    }

    public boolean isEmpty() {
        return dataTable.isEmpty();
    }

    public int size() {
        return dataTable.size();
    }

    public int commit() {
        return dataTable.commit();
    }

    public int rollback() {
        return dataTable.rollback();
    }

    public int commitSize() {
        return dataTable.commitSize();
    }

    public File getWorkingDirectory() {
        return dataTable.getWorkingDirectory();
    }

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public void load() throws IOException, ParseException {
        File curTable = new File(dataTable.getWorkingDirectory(), dataTable.getName());
        curTable = curTable.getCanonicalFile();
        File[] dirs = curTable.listFiles();
        if (dirs.length > DIR_COUNT) {
            throw new IOException("The table includes more than " + DIR_COUNT + " directories");
        }
        for (File d : dirs) {
            if (!d.isDirectory()) {
                throw new IOException(dataTable.getName() + " should include only directories");
            }
            File[] files = d.listFiles();
            if (files.length > FILE_COUNT) {
                throw new IOException("The directory includes more than " + FILE_COUNT + " files");
            }
            String dirName = d.getName();
            char firstChar = dirName.charAt(0);
            char secondChar;
            int dirNumber;
            if (dirName.length() > 1) {
                secondChar = dirName.charAt(1);
            } else {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            if (Character.isDigit(firstChar)) {
                if (Character.isDigit(secondChar)) {
                    dirNumber = Integer.parseInt(dirName.substring(0, 2));
                } else {
                    dirNumber = Integer.parseInt(dirName.substring(0, 1));
                }
            } else {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            if (!dirName.equals(new String(dirNumber + ".dir"))) {
                throw new IllegalArgumentException("Not allowed name of directory in table");
            }
            for (File f : files) {
                if (!f.isFile()) {
                    throw new IOException("Unexpected directory");
                }
                String fileName = f.getName();
                char firstFileChar = fileName.charAt(0);
                char secondFileChar;
                int fileNumber;
                if (fileName.length() > 1) {
                    secondFileChar = fileName.charAt(1);
                } else {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                if (Character.isDigit(firstFileChar)) {
                    if (Character.isDigit(secondFileChar)) {
                        fileNumber = Integer.parseInt(fileName.substring(0, 2));
                    } else {
                        fileNumber = Integer.parseInt(fileName.substring(0, 1));
                    }
                } else {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                if (!fileName.equals(new String(fileNumber + ".dat"))) {
                    throw new IllegalArgumentException("Not allowed name of file in table");
                }
                FileReader fileReader = new FileReader(f, this.dataTable);
                while (fileReader.checkingLoadingConditions()) {
                    String key = fileReader.getNextKey();
                    int hashByte = Math.abs(key.getBytes()[0]);
                    int ndirectory = hashByte % DIR_COUNT;
                    int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                    if (ndirectory != dirNumber) {
                        throw new IllegalArgumentException("Wrong key in " + dirName);
                    }
                    if (fileNumber != nfile) {
                        throw new IllegalArgumentException("Wrong key in" + fileName);
                    }
                }
                fileReader.putStoreableValueToTable(tableProvider.deserialize(this, fileReader.getNextValue()));
                fileReader.closeResources();
            }
        }
    }

    public void writeToDataBase() throws IOException {
        dataTable.rollback();
        Set<String> keys = dataTable.getKeys();
        if (!keys.isEmpty()) {
            for (int i = 0; i < DIR_COUNT; ++i) {
                File dir = new File(new File(dataTable.getWorkingDirectory(), dataTable.getName()), new String(i + ".dir"));
                for (int j = 0; j < FILE_COUNT; ++j) {
                    DataTable keysToFile = new DataTable();
                    File file = new File(dir, new String(j + ".dat"));
                    for (String key : keys) {
                        int hashByte = Math.abs(key.getBytes()[0]);
                        int ndirectory = hashByte % DIR_COUNT;
                        int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                        if ((ndirectory == i) && (nfile == j)) {
                            if (!dir.getCanonicalFile().exists()) {
                                dir.getCanonicalFile().mkdir();
                            }

                            if (!file.getCanonicalFile().exists()) {
                                file.getCanonicalFile().createNewFile();
                            }
                            keysToFile.put(key, tableProvider.serialize(this, dataTable.get(key)));
                            keysToFile.commit();
                        }
                    }

                    if (!keysToFile.isEmpty()) {
                        FileWriter fileWriter = new FileWriter();
                        fileWriter.writeDataToFile(file.getCanonicalFile(), keysToFile);
                    } else {
                        if (file.getCanonicalFile().exists()) {
                            file.getCanonicalFile().delete();
                        }
                    }
                }
                if (dir.getCanonicalFile().listFiles() == null) {
                    dir.delete();
                }
            }
        }
    }
}
