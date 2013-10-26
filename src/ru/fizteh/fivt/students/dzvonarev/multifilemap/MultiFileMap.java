package ru.fizteh.fivt.students.dzvonarev.multifilemap;

import ru.fizteh.fivt.students.dzvonarev.shell.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    public static void readMultiFileMap(String workingDir) throws IOException {
        changeWorkingTable("noTable");
        multiFileMap = new HashMap<String, HashMap<String, String>>();
        File currDir = new File(workingDir);
        if (currDir.exists() && currDir.isDirectory()) {
            String[] tables = currDir.list();
            if (tables != null && tables.length != 0) {
                for (String table : tables) {
                    if (!isValidTable(workingDir + File.separator + table)) {
                        System.out.println("table " + table + " is not valid");
                        System.exit(1);
                    }
                    File dirTable = new File(workingDir + File.separator + table);
                    if (dirTable.isFile()) {
                        continue;
                    }
                    String[] dbDirs = dirTable.list();
                    if (dbDirs != null && dbDirs.length != 0) {
                        HashMap<String, String> tempMap = new HashMap<String, String>();
                        for (String dbDir : dbDirs) {
                            File dbDirTable = new File(workingDir + File.separator + table + File.separator + dbDir);
                            String[] dbDats = dbDirTable.list();
                            if (dbDats == null || dbDats.length == 0) {
                                throw new IOException("reading directory: " + table + " is not valid");
                            }
                            for (String dbDat : dbDats) {
                                String str = workingDir + File.separator + table + File.separator + dbDir + File.separator + dbDat;
                                readFileMap(tempMap, str, dbDir, dbDat); // table -> all |"key"|"value"|

                            }
                        }
                        multiFileMap.put(table, tempMap);
                    }
                }
            }
        } else {
            System.out.println("working directory is not valid");
            System.exit(1);
        }
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

    public static boolean isValidTable(String path) {
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

    public static void printMultiMap() {
        HashMap<String, HashMap<String, String>> myMultiHash = multiFileMap;
        Set mapSet = myMultiHash.entrySet();
        Iterator<Map.Entry<String, HashMap<String, String>>> i = mapSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, HashMap<String, String>> currItem = i.next();
            System.out.println(currItem.getKey() + ":");
            HashMap<String, String> map = currItem.getValue();
            Set set = map.entrySet();
            Iterator<Map.Entry<String, String>> j = set.iterator();
            while (j.hasNext()) {
                Map.Entry<String, String> curr = j.next();
                System.out.println(curr.getKey() + " " + curr.getValue());
            }
        }
    }

    public static boolean keyIsValid(String key, String dir, String file) {
        char b = key.charAt(0);
        int nDirectory = Math.abs(b) % 16;
        int nFile = Math.abs(b) / 16 % 16;
        String rightDir = Integer.toString(nDirectory) + ".dir";
        String rightFile = Integer.toString(nFile) + ".dat";
        return (dir.equals(rightDir) && file.equals(rightFile));
    }

    public static void readFileMap(HashMap<String, String> fileMap, String fileName, String dir, String file) throws IOException {
        RandomAccessFile fileReader = openFileForRead(fileName);
        long endOfFile = fileReader.length();
        long currFilePosition = fileReader.getFilePointer();
        if (endOfFile == 0) {
            closeFile(fileReader);
            throw new IOException("reading directory: " + dir + " is not valid");
        }
        while (currFilePosition != endOfFile) {
            int keyLen = fileReader.readInt();
            int valueLen = fileReader.readInt();
            if (keyLen <= 0 || valueLen <= 0) {
                closeFile(fileReader);
                System.out.println(fileName + " : file is broken");
                System.exit(1);
            }
            byte[] keyByte = null;
            byte[] valueByte = null;
            try {
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
            } catch (OutOfMemoryError e) {
                closeFile(fileReader);
                System.out.println(fileName + " : file is broken");
                System.exit(1);
            }
            fileReader.readFully(keyByte, 0, keyLen);
            fileReader.readFully(valueByte, 0, valueLen);
            String key = new String(keyByte);
            String value = new String(valueByte);
            if (!keyIsValid(key, dir, file)) {
                System.out.println("file " + file + " in " + dir + " is not valid");
                System.exit(1);
            }
            fileMap.put(key, value);
            currFilePosition = fileReader.getFilePointer();
            endOfFile = fileReader.length();
        }
        closeFile(fileReader);
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
            newFile.getChannel().truncate(0);
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

    private static void remove(File aim) throws IOException {
        if (aim.isDirectory()) {
            if (aim.list().length == 0) {
                if (!aim.delete()) {
                    throw new IOException("drop: can't remove " + aim + " : no such file or directory");
                }
            } else {
                String[] file = aim.list();
                for (String aFile : file) {
                    File currFile = new File(aim, aFile);
                    remove(currFile);
                }
                if (aim.list().length == 0) {
                    if (!aim.delete()) {
                        throw new IOException("drop: can't remove " + aim + " : no such file or directory");
                    }
                }
            }
        } else {
            if (!aim.delete()) {
                throw new IOException("drop: can't remove " + aim + " : no such file or directory");
            }
        }
    }

    public static void realRemove(String expr) throws IOException {
        String path = System.getProperty("fizteh.db.dir") + File.separator + expr;
        if (path.equals(Shell.getCurrentDirectory()) || Shell.getCurrentDirectory().contains(path)) {                                  // can't delete father of son
            throw new IOException("drop: can't remove " + path);
        }
        if ((new File(path)).isFile() || (new File(path)).isDirectory()) {
            remove(new File(path));
        } else {
            throw new IOException("drop: can't remove " + path);
        }
    }


}
