package ru.fizteh.fivt.students.dzvonarev.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MultiFileMap {

    private static HashMap<String, HashMap<String, String>> multiFileMap;

    private static String workingTable;   // "noTable" means we are not in table

    public static String getWorkingTable() {
        return workingTable;
    }

    public static void changeWorkingTable(String newWorkingTable) {
        workingTable = newWorkingTable;
    }

    public static HashMap<String, HashMap<String, String>> getMultiFileMap() {
        return multiFileMap;
    }

    public static void readMultiFileMap(String workingDir) throws IOException, RuntimeException {
        changeWorkingTable("noTable");
        multiFileMap = new HashMap<>();
        File currDir = new File(workingDir);
        if (currDir.exists() && currDir.isDirectory()) {
            String[] tables = currDir.list();
            if (tables != null && tables.length != 0) {
                for (String table : tables) {
                    File dirTable = new File(workingDir + File.separator + table);
                    if (dirTable.isFile()) {
                        continue;
                    }
                    String[] dbDirs = dirTable.list();
                    if (dbDirs != null && dbDirs.length != 0) {
                        HashMap<String, String> tempMap;
                        tempMap = new HashMap<>();
                        for (String dbDir : dbDirs) {
                            if (!isValidDir(dbDir)) {
                                throw new RuntimeException("directory " + dbDir + " is not valid");
                            }
                            File dbDirTable = new File(workingDir + File.separator + table + File.separator + dbDir);
                            String[] dbDats = dbDirTable.list();
                            if (dbDats == null || dbDats.length == 0) {
                                throw new RuntimeException("reading directory: " + table + " is not valid");
                            }
                            for (String dbDat : dbDats) {
                                String str = workingDir + File.separator + table + File.separator + dbDir + File.separator + dbDat;
                                readFileMap(tempMap, str, dbDir, dbDat); // table -> all |"key"|"value"|
                            }
                        }
                        multiFileMap.put(table, tempMap);
                    }
                }
                for (String table : tables) {
                    if (new File(workingDir + File.separator + table).isFile()) {
                        continue;
                    }
                    ShellRemove.execute(table);
                    if (!(new File(workingDir + File.separator + table)).mkdir()) {
                        throw new IOException("exit: can't make " + table + " directory");
                    }
                }
            }
        } else {
            throw new RuntimeException("working directory is not valid");
        }
    }


    public static void readFileMap(HashMap<String, String> fileMap, String fileName, String dir, String file) throws IOException, RuntimeException {
        RandomAccessFile fileReader = openFileForRead(fileName);
        long endOfFile = fileReader.length();
        long currFilePosition = fileReader.getFilePointer();
        if (endOfFile == 0) {
            closeFile(fileReader);
            throw new RuntimeException("reading directory: " + dir + " is not valid");
        }
        while (currFilePosition != endOfFile) {
            int keyLen = fileReader.readInt();
            int valueLen = fileReader.readInt();
            if (keyLen <= 0 || valueLen <= 0) {
                closeFile(fileReader);
                throw new RuntimeException(fileName + " : file is broken");
            }
            byte[] keyByte;
            byte[] valueByte;
            try {
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
            } catch (OutOfMemoryError e) {
                closeFile(fileReader);
                throw new RuntimeException(fileName + " : file is broken");
            }
            fileReader.readFully(keyByte, 0, keyLen);
            fileReader.readFully(valueByte, 0, valueLen);
            String key = new String(keyByte);
            String value = new String(valueByte);
            if (!keyIsValid(key, dir, file)) {
                closeFile(fileReader);
                throw new RuntimeException("file " + file + " in " + dir + " is not valid");
            }
            fileMap.put(key, value);
            currFilePosition = fileReader.getFilePointer();
            endOfFile = fileReader.length();
        }
        closeFile(fileReader);
    }

    public static boolean isFilesInDirValid(String dirName) {
        File dir = new File(dirName);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            if (new File(dirName + File.separator + currFile).isHidden()) {
                continue;
            }
            if (new File(dirName + File.separator + currFile).isDirectory()) {
                return false;
            }
            if (!currFile.matches("[0-9][.]dat|1[0-5][.]dat")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidDir(String path) {
        File dir = new File(path);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            if (new File(path + File.separator + currFile).isHidden()) {
                continue;
            }
            if (new File(path + File.separator + currFile).isDirectory() && currFile.matches("[0-9][.]dir|1[0-5][.]dir")) {
                if (!isFilesInDirValid(path + File.separator + currFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean keyIsValid(String key, String dir, String file) {
        int b = key.getBytes()[0];
        int nDirectory = Math.abs(b) % 16;
        int nFile = Math.abs(b) / 16 % 16;
        String rightDir = Integer.toString(nDirectory) + ".dir";
        String rightFile = Integer.toString(nFile) + ".dat";
        return (dir.equals(rightDir) && file.equals(rightFile));
    }

    public static void writeMap(HashMap<String, HashMap<String, String>> map, String table) throws IOException {
        HashMap<String, String> fileMap = map.get(table);
        if (fileMap == null) {
            return;
        } else {
            if (fileMap.isEmpty()) {
                return;
            }
        }
        Set fileSet = fileMap.entrySet();
        Iterator<Map.Entry<String, String>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> currItem = i.next();
            String key = currItem.getKey();
            String value = currItem.getValue();
            int b = key.getBytes()[0];
            int nDirectory = Math.abs(b) % 16;
            int nFile = Math.abs(b) / 16 % 16;
            String rightDir = Integer.toString(nDirectory) + ".dir";
            String rightFile = Integer.toString(nFile) + ".dat";
            String path = System.getProperty("fizteh.db.dir") + File.separator + table +
                    File.separator + rightDir + File.separator + rightFile;
            String dir = System.getProperty("fizteh.db.dir") + File.separator + table +
                    File.separator + rightDir;
            File file = new File(path);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("can't create directory " + dir);
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("can't create file " + path);
                }
            }
            writeInFile(path, key, value);
        }
    }

    public static void writeInFile(String path, String key, String value) throws IOException {
        RandomAccessFile fileWriter = MultiFileMap.openFileForWrite(path);
        fileWriter.skipBytes((int) fileWriter.length());
        try {
            if (key == null || value == null) {
                MultiFileMap.closeFile(fileWriter);
                throw new IOException("updating file: error in writing");
            }
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            fileWriter.writeInt(keyBytes.length);
            fileWriter.writeInt(valueBytes.length);
            fileWriter.write(keyBytes);
            fileWriter.write(valueBytes);
        } catch (IOException e) {
            throw new IOException("updating file: error in writing");
        } finally {
            MultiFileMap.closeFile(fileWriter);
        }
    }


    public static RandomAccessFile openFileForRead(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("reading from file: file not found");
        }
        return newFile;
    }

    public static RandomAccessFile openFileForWrite(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("writing to file: file not found");
        }
        return newFile;
    }

    public static void closeFile(RandomAccessFile file) throws IOException {
        try {
            file.close();
        } catch (IOException e) {
            throw new IOException("error in closing file");
        }
    }

}
