package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.PrintWriter;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class DataBase implements Table {
    private String dataBaseDirectory;
    private TableProvider provider;
    private List<Class<?>> types;
    private DataBaseFile[] files;

    private void checkNames(String[] fileList, String extension) throws IOException {
        for (String fileNumber : fileList) {
            if (fileNumber.equals("signature.tsv")) {
                continue;

            }
            String[] nameFile = fileNumber.split("\\.");
            if ((nameFile.length != 2)
                    || !nameFile[1].equals(extension)) {
                throw new IOException(dataBaseDirectory + " wrong file " + fileNumber);
            }
            int intName;
            try {
                intName = Integer.parseInt(nameFile[0]);
            } catch (NumberFormatException e) {
                throw new IOException(dataBaseDirectory + " wrong name of file" + fileNumber);
            }
            if ((intName < 0) || (intName > 15))
                throw new IOException(dataBaseDirectory + " wrong name of file" + fileNumber);
        }
    }

    private void checkCorrectionDirectory(String directoryName) throws IOException {
        File file = new File(directoryName);
        if (file.isFile()) {
            throw new IOException(directoryName + " isn't a directory!");
        }
        if (file.list().length <= 0) {
        	throw new IOException (directoryName + " is empty");
        }
        checkNames(file.list(), "dat");
        for (String fileName : file.list()) {
        	File fileHelp = new File(directoryName, fileName);
            if (fileHelp.isDirectory()) {
                throw new IOException(directoryName + File.separator + fileName + " isn't a file!");
            }
            if (fileHelp.length() <= 0) {
                throw new IOException(directoryName + File.separator + fileName + " is empty!");
            }
        }
    }

    private void checkCorrection() throws IOException {
        File file = new File(dataBaseDirectory);
        if (!file.exists()) {
            throw new IOException(dataBaseDirectory + " isn't exist");
        }
        if (file.isFile()) {
            throw new IOException(dataBaseDirectory + " isn't exist");
        }
        if (file.list().length <= 0) {
        	throw new IOException (dataBaseDirectory + " is empty");
        }
        checkNames(file.list(), "dir");
        for (String fileNumber : file.list()) {
            if (!fileNumber.equals("signature.tsv")) {
                checkCorrectionDirectory(dataBaseDirectory + File.separator + fileNumber);
            }

        }


    }

    public DataBase(String nameDirectory, TableProvider nProvider, List<Class<?>> nTypes) throws IOException {

        dataBaseDirectory = nameDirectory;
        provider = nProvider;

        types = nTypes;
        BaseSignature.setBaseSignature(dataBaseDirectory, types);
        

        checkCorrection();
        files = new DataBaseFile[256];
        loadDataBase();
    }

    public DataBase(String nameDirectory, TableProvider nProvider) throws IOException {

        dataBaseDirectory = nameDirectory;
        provider = nProvider;

        types = BaseSignature.getBaseSignature(dataBaseDirectory);


        checkCorrection();
        files = new DataBaseFile[256];
        loadDataBase();
    }


    private void addDirectory(final String directoryName) throws IOException {
        File file = new File(dataBaseDirectory + File.separator + directoryName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new IOException("Cannot create a directory!");
            }
        }
    }

    private String getFullName(int nDir, int nFile) {
        return dataBaseDirectory + File.separator + Integer.toString(nDir)
                + ".dir" + File.separator + Integer.toString(nFile) + ".dat";
    }

    private void loadDataBase() throws IOException {
        try {
            for (int i = 0; i < 16; ++i) {
                addDirectory(Integer.toString(i) + ".dir");
                for (int j = 0; j < 16; ++j) {
                    int nFile = j;
                    int nDir = i;
                    DataBaseFile file = new DataBaseFile(getFullName(i, j), i, j, provider, this);
                    files[i * 16 + j] = file;
                }

            }
        } catch (IOException e) {
            saveDataBase();
            throw e;
        }

    }

    private void deleteEmptyDirectory(final String name) throws IOException {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new IOException("Cannot delete a directory!");
                }
            }
        }
    }

    public void drop() throws IOException {
        for (byte i = 0; i < 16; ++i) {
            for (byte j = 0; j < 16; ++j) {
                File file = new File(getFullName(i, j));
                if (file.exists()) {
                    if (!file.isFile()) {
                        throw new IOException("It isn't a file!");
                    }
                    if (!file.delete()) {
                        throw new IOException("Cannot delete a file!");
                    }
                }
            }
            deleteEmptyDirectory(Integer.toString(i) + ".dir");
        }
        if (!new File(dataBaseDirectory, "signature.tsv").delete()) {
            throw new IOException("Cannot delete a file!");
        }
    }

    public void saveDataBase() throws IOException {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (!(files[i * 16 + j].getCurrentTable().isEmpty())) {
                    files[i * 16 + j].write();
                } else {
                    File file = new File(getFullName(i, j));
                    if (file.exists()) {
                        if (!file.delete())
                            throw new IOException("Cannot delete a file");
                    }
                }
            }
            deleteEmptyDirectory(Integer.toString(i) + ".dir");
        }
    }

    public Storeable get(String keyString) {
        checkString(keyString);
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return JSONClass.deserialize(this, file.get(keyString));
    }

    public Storeable put(String keyString, Storeable storeable) {
        checkString(keyString);
        if (storeable == null) {
            throw new IllegalArgumentException("Value is null!");
        }
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return JSONClass.deserialize(this, file.put(keyString, JSONClass.serialize(this, storeable)));
    }

    public Storeable remove(String keyString) {
        checkString(keyString);
        int nDir = Math.abs(keyString.getBytes()[0]) % 16;
        int nFile = Math.abs((keyString.getBytes()[0] / 16) % 16);
        DataBaseFile file = files[nDir * 16 + nFile];
        return JSONClass.deserialize(this, file.remove(keyString));
    }

    public int countCommits() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
        }
        return count;
    }

    public int commit() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
            try {
                files[i].commit();
            } catch (IOException e) {
                throw new RuntimeException("cannot do commit");
            }
        }
        return count;
    }

    public int rollback() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countCommits();
            try {
                files[i].rollback();
            } catch (IOException e) {
                throw new RuntimeException("cannot do rollback");
            }
        }
        return count;
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < 256; ++i) {
            count += files[i].countSize();
        }
        return count;
    }

    public Storeable putStoreable(String keyStr, String valueStr) throws ParseException {
        return put(keyStr, provider.deserialize(this, valueStr));
    }

    public String getName() {
        return new File(dataBaseDirectory).getName();
    }

    private void checkString(String str) {
        if ((str == null) || (str.trim().length() == 0)) {
            throw new IllegalArgumentException("Wrong key!");
        }
    }


    public int getColumnsCount() {
        return types.size();
    }


    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        if ((columnIndex < 0) || (columnIndex >= types.size())) {
            throw new IndexOutOfBoundsException("wrong columnIndex");
        }
        return types.get(columnIndex);
    }


}
