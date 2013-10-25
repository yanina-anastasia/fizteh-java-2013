package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MultiOpenFile {
    public static String curTable;

    public static File getDirWithNum(int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        File res = new File(System.getProperty("fizteh.db.dir"), curTable);
        return new File(res, dirName);
    }

    public static File getFileWithNum(int fileNum, int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        String fileName = String.format("%d.dat", fileNum);
        File res = new File(System.getProperty("fizteh.db.dir"), curTable);
        res = new File(res, dirName);
        return new File(res, fileName);
    }

    public static boolean open(State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        curTable = myState.curTableName;
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        if (!(new File(path).exists())) {
            System.err.println("The path from the property does not exist");
            System.exit(1);
        }
        if (new File(path).isFile()) {
            System.err.println("The path from the property is not a directory");
            System.exit(1);
        }
        HashMap<String, String> loadingTable = new HashMap<String, String>();
        for (File table : new File(path).listFiles()) {
            curTable = table.getName();
            File[] files = new File(path, curTable).listFiles();
            for (File step : files) {
                if (step.isFile()) {
                    System.err.println("The " + curTable + " is not a directory");
                    System.exit(1);
                }
            }
            if (files == null) {
                myState.myDatabase.database.put(curTable, loadingTable);
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
                            myState.myDatabase.database.put(curTable, loadingTable);
                        }
                    }
                }
            }
        }
        myState.table = null;
        return true;
    }

    private static void loadTable(RandomAccessFile temp, HashMap<String, String> table, int i, int j) throws IOException {
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
            if (i == MultiFileMapUtils.getDirectoryNum(key) && j == MultiFileMapUtils.getFileNum(key)) {
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
        if (i == MultiFileMapUtils.getDirectoryNum(key) && j == MultiFileMapUtils.getFileNum(key)) {
            table.put(nextKey, putValue);
        } else {
            throw new IOException("File has incorrect format");
        }
        temp.close();
    }
}
