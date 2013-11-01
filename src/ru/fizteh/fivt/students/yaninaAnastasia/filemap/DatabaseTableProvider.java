package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DatabaseTableProvider implements TableProvider {
    public DatabaseTable curTable = null;
    HashMap<String, DatabaseTable> tables = new HashMap<String, DatabaseTable>();
    String curDir;

    public DatabaseTableProvider(String directory) {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("Error with the property");
        }
        File databaseDirectory = new File(directory);
        curDir = directory;
        /*for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }
            if ((tableFile.getName() == null) || (tableFile.getName().isEmpty())) {
                throw new IllegalArgumentException("Error with the property");
            }
            //DatabaseTable table = new DatabaseTable(tableFile.getName());
            //tables.put(table.getName(), table);
        }  */
        if (!open()) {
            throw new IllegalArgumentException("Wrong format");
        }
    }

    public DatabaseTable getTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        DatabaseTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        table.putName(name);

        if (curTable != null && curTable.uncommittedChanges > 0) {
            throw new IllegalArgumentException(String.format("%d unsaved changes", curTable.uncommittedChanges));
        }

        curTable = table;
        return table;
    }

    public Table createTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        if (tables.containsKey(name)) {
            return null;
        }

        DatabaseTable table = new DatabaseTable(name);
        tables.put(name, table);
        return table;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(String.format("%s not exists", name));
        }

        tables.remove(name);
    }

    public File getDirWithNum(int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        File res = new File(curDir, curTable.getName());
        return new File(res, dirName);
    }

    public File getFileWithNum(int fileNum, int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        String fileName = String.format("%d.dat", fileNum);
        File res = new File(curDir, curTable.getName());
        res = new File(res, dirName);
        return new File(res, fileName);
    }

    public boolean open() {
        File databaseDirectory = new File(curDir);
        String curTableName;
        DatabaseTable loadingTable;
        for (File table : databaseDirectory.listFiles()) {
            curTableName = table.getName();
            curTable = new DatabaseTable(curTableName);
            loadingTable = new DatabaseTable(curTableName);
            File[] files = new File(curDir, curTableName).listFiles();
            for (File step : files) {
                if (step.isFile()) {
                    continue;
                }
                if ((step.getName() == null) || (step.getName().isEmpty())) {
                    throw new IllegalArgumentException("Error with the property");
                }
            }
            if (files.length == 0) {
                tables.put(curTableName, loadingTable);
                continue;
            }
            for (int i = 0; i < 16; i++) {
                File currentDir = getDirWithNum(i);
                if (currentDir.isFile()) {
                    throw new IllegalArgumentException("Illegal argument: it is not a directory");
                }
                if (!currentDir.exists()) {
                    continue;
                } else {
                    for (int j = 0; j < 16; ++j) {
                        File currentFile = getFileWithNum(j, i);

                        if (currentFile.exists()) {
                            try {
                                File tmpFile = new File(currentFile.toString());
                                RandomAccessFile temp = new RandomAccessFile(tmpFile, "r");
                                try {
                                    loadTable(temp, loadingTable, i, j);
                                    temp.close();
                                } catch (EOFException e) {
                                    System.err.println("Wrong format");
                                    return false;
                                } catch (IOException e) {
                                    System.err.println("IO exception");
                                    return false;
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Wrong file format");
                                    return false;
                                }
                            } catch (IOException e) {
                                System.err.println("Cannot create new file");
                                return false;
                            }
                        }
                    }
                }
            }
            loadingTable.uncommittedChanges = 0;
            tables.put(curTableName, loadingTable);
        }
        curTable = null;
        return true;
    }

    private void loadTable(RandomAccessFile temp, DatabaseTable table, int i, int j) throws IllegalArgumentException, IOException {
        if (temp.length() == 0) {
            return;
        }
        long nextOffset = 0;
        temp.seek(0);
        byte c = temp.readByte();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (c != 0) {
            out.write(c);
            c = temp.readByte();
        }
        String key = new String(out.toByteArray(), StandardCharsets.UTF_8);
        long firstOffset = temp.readInt();
        long currentOffset = firstOffset;
        long cursor = temp.getFilePointer();
        String nextKey = key;
        while (cursor < firstOffset) {
            c = temp.readByte();
            out = new ByteArrayOutputStream();
            while (c != 0) {
                out.write(c);
                c = temp.readByte();
            }
            nextKey = new String(out.toByteArray(), StandardCharsets.UTF_8);
            nextOffset = temp.readInt();
            cursor = temp.getFilePointer();
            temp.seek(currentOffset);
            int len = (int) (nextOffset - currentOffset);
            if (len < 0) {
                throw new IllegalArgumentException("File has incorrect format");
            }
            byte[] bytes = new byte[len];
            temp.read(bytes);
            String putValue = new String(bytes, StandardCharsets.UTF_8);
            if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
                table.put(key, putValue);
            } else {
                throw new IllegalArgumentException("File has incorrect format");
            }
            temp.seek(cursor);
            key = nextKey;
            currentOffset = nextOffset;
        }
        temp.seek(currentOffset);
        int len = (int) (temp.length() - currentOffset);
        if (len < 0) {
            throw new IllegalArgumentException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        temp.read(bytes);
        String putValue = new String(bytes, StandardCharsets.UTF_8);
        if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
            table.put(nextKey, putValue);
        } else {
            throw new IllegalArgumentException("File has incorrect format");
        }
    }
}
