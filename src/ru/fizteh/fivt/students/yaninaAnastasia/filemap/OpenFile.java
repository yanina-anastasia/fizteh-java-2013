package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OpenFile {
    public String curTable;

    public OpenFile(MultiDBState curState) {
        curTable = curState.curTableName;
    }

    public File getDirWithNum(int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        File res = new File(System.getProperty("fizteh.db.dir"), curTable);
        return new File(res, dirName);
    }

    public File getFileWithNum(int fileNum, int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        String fileName = String.format("%d.dat", fileNum);
        File res = new File(System.getProperty("fizteh.db.dir"), curTable);
        res = new File(res, dirName);
        return new File(res, fileName);
    }

    public boolean open(State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        if (new File(path).isFile()) {
            System.err.println("The path from the property is not a directory");
            System.exit(1);
        }
        File databaseDirectory = new File(path);
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
            System.exit(1);
        }

        DatabaseTable loadingTable = new DatabaseTable(curTable);
        for (File table : new File(path).listFiles()) {
            curTable = table.getName();
            File[] files = new File(path, curTable).listFiles();
            for (File step : files) {
                if (step.isFile()) {
                    System.err.println("The " + curTable + " is not a directory");
                    System.exit(1);
                }
            }
            if (files.length == 0) {
                myState.database.tables.put(curTable, loadingTable);
                continue;
            }
            for (int i = 0; i < 16; i++) {
                File currentDir = getDirWithNum(i);
                if (currentDir.isFile()) {
                    System.err.println("The " + currentDir.toString() + " is not a directory");
                    System.exit(1);
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
            myState.database.tables.put(curTable, loadingTable);
        }
        myState.table = null;
        return true;
    }

    private void loadTable(RandomAccessFile temp, DatabaseTable table, int i, int j) throws IOException {
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
                throw new IOException("File has incorrect format");
            }
            byte[] bytes = new byte[len];
            temp.read(bytes);
            String putValue = new String(bytes, StandardCharsets.UTF_8);
            if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
                table.put(key, putValue);
            } else {
                throw new IOException("File has incorrect format");
            }
            temp.seek(cursor);
            key = nextKey;
            currentOffset = nextOffset;
        }
        temp.seek(currentOffset);
        int len = (int) (temp.length() - currentOffset);
        if (len < 0) {
            throw new IOException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        temp.read(bytes);
        String putValue = new String(bytes, StandardCharsets.UTF_8);
        if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
            table.put(nextKey, putValue);
        } else {
            throw new IOException("File has incorrect format");
        }
    }
}
