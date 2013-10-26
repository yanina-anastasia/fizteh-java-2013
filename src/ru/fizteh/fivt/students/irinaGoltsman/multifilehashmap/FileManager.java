package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;

import java.io.*;
import java.util.HashMap;

public class FileManager {

    private static class LineOfDB {
        public long length = 0;
        public String key = "";
        public String value = "";

        public LineOfDB() {
        }

        public LineOfDB(long length, String key, String value) {
            this.length = length;
            this.key = key;
            this.value = value;
        }
    }

    private static LineOfDB readLineOfDatFile(RandomAccessFile datFile, long inputLength) {
        long length = inputLength;
        int lengthOfKey;
        int lengthOfValue;
        LineOfDB empty = new LineOfDB();
        try {
            lengthOfKey = datFile.readInt();
            lengthOfValue = datFile.readInt();
        } catch (IOException e) {
            System.err.println("Wrong format of db");
            return empty;
        }
        length -= 8;
        if (lengthOfKey <= 0 || lengthOfValue <= 0) {
            System.err.println("Wrong format of db: length of key and length of value must be positive integers.");
            return empty;
        }
        if (lengthOfKey > length) {
            System.err.println("Wrong format of db: length of key ​​do not match content.");
            return empty;
        }
        byte[] bytesOfKey = readKeyOrValue(datFile, lengthOfKey);
        if (bytesOfKey.length == 0) {
            return empty;
        }
        length -= lengthOfKey;
        if (lengthOfValue > length) {
            System.err.println("Wrong format of db: length of value ​​do not match content.");
            return empty;
        }
        byte[] bytesOfValue = readKeyOrValue(datFile, lengthOfValue);
        if (bytesOfValue.length == 0) {
            return empty;
        }
        length -= lengthOfValue;
        String key;
        String value;
        try {
            key = new String(bytesOfKey, "UTF-8");
            value = new String(bytesOfValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
            return empty;
        }
        long lengthOfLine = inputLength - length;
        LineOfDB result = new LineOfDB(lengthOfLine, key, value);
        return result;
    }

    private static byte[] readKeyOrValue(RandomAccessFile datFile, int length) {
        byte[] bytes = new byte[length];
        int countOfBytesWasRead = 0;
        while (true) {
            try {
                countOfBytesWasRead = datFile.read(bytes, countOfBytesWasRead, length - countOfBytesWasRead);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                bytes = new byte[0];
                return bytes;
            }
            if (countOfBytesWasRead == length) {
                break;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Error while reading key or value");
                bytes = new byte[0];
                return bytes;
            }
        }
        return bytes;
    }

    private static Code readFromDisk(RandomAccessFile datFile, HashMap<String, String> storage) {
        long length = -1;
        try {
            length = datFile.length();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        if (length == 0) {
            return Code.OK;
        }
        while (length > 0) {
            LineOfDB line = readLineOfDatFile(datFile, length);
            if (line.length == 0) {
                return Code.ERROR;
            }
            length -= line.length;
            storage.put(line.key, line.value);
        }
        try {
            datFile.close();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        return Code.OK;
    }

    public static Code readDBFromDisk(File tableDirectory, HashMap<String, String> tableStorage) {
        if (!tableDirectory.exists() || tableDirectory.isFile()) {
            System.err.println(tableDirectory + ": not directory or not exist");
            return Code.ERROR;
        }
        for (int index = 0; index < 16; index++) {
            String currentDirectoryName = index + ".dir";
            File currentDirectory = new File(tableDirectory, currentDirectoryName);
            if (!currentDirectory.exists()) {
                continue;
            }
            if (!currentDirectory.isDirectory()) {
                System.err.println(currentDirectory.toString() + ": not directory");
                return Code.ERROR;
            }
            for (int fileIndex = 0; fileIndex < 16; fileIndex++) {
                String currentFileName = fileIndex + ".dat";
                File currentFile = new File(currentDirectory, currentFileName);
                if (!currentFile.exists()) {
                    continue;
                }
                RandomAccessFile fileIndexDat = null;
                try {
                    fileIndexDat = new RandomAccessFile(currentFile, "rw");
                } catch (FileNotFoundException e) {
                    continue;
                }
                Code returnCode = readFromDisk(fileIndexDat, tableStorage);
                if (returnCode != Code.OK) {
                    return returnCode;
                }
            }
        }
        return Code.OK;
    }

    private static Code cleanEmptyDir(File dir) {
        if (dir.exists()) {
            if (dir.listFiles().length == 0) {
                if (!dir.delete()) {
                    System.err.println("File: " + dir.toString() + " can't be deleted");
                    return Code.SYSTEM_ERROR;
                }
            }
        }
        return Code.OK;
    }

    private static void parseStorage(HashMap<String, String>[][] parsedStorage, HashMap<String, String> storage) {
        for (String key : storage.keySet()) {
            int hashCode = key.hashCode();
            int indexOfDir = hashCode % 16;
            if (indexOfDir < 0) {
                indexOfDir *= -1;
            }
            int indexOfDat = hashCode / 16 % 16;
            if (indexOfDat < 0) {
                indexOfDat *= -1;
            }
            if (parsedStorage[indexOfDir][indexOfDat] == null) {
                parsedStorage[indexOfDir][indexOfDat] = new HashMap<>();
            }
            parsedStorage[indexOfDir][indexOfDat].put(key, storage.get(key));
        }
    }

    public static Code writeToDatFile(RandomAccessFile datFile, HashMap<String, String> storage) {
        try {
            datFile.seek(0);
            datFile.setLength(0);
            for (String key : storage.keySet()) {
                byte[] bytesOfKey = key.getBytes("UTF-8");
                byte[] bytesOfValue = storage.get(key).getBytes("UTF-8");
                datFile.writeInt(bytesOfKey.length);
                datFile.writeInt(bytesOfValue.length);
                datFile.write(bytesOfKey);
                datFile.write(bytesOfValue);
            }
        } catch (Exception e) {
            System.err.println(e);
            try {
                datFile.close();
            } catch (Exception e2) {
                System.err.println(e2);
            }
            return Code.SYSTEM_ERROR;
        }
        return Code.OK;
    }

    public static Code writeTableOnDisk(File tableDirectory, HashMap<String, String> tableStorage) {
        if (tableDirectory == null) {
            return Code.ERROR;
        }
        HashMap<String, String>[][] parsedStorage = new HashMap[16][16];
        parseStorage(parsedStorage, tableStorage);
        for (int indexOfDir = 0; indexOfDir < 16; indexOfDir++) {
            File dir = new File(tableDirectory, indexOfDir + ".dir");
            for (int indexOfDatFile = 0; indexOfDatFile < 16; indexOfDatFile++) {
                File datFile = new File(dir, indexOfDatFile + ".dat");
                if (parsedStorage[indexOfDir][indexOfDatFile] == null) {
                    if (datFile.exists()) {
                        if (!datFile.delete()) {
                            System.err.println("File: " + datFile.toString() + " can't be deleted");
                            return Code.SYSTEM_ERROR;
                        }
                    }
                    continue;
                }
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        System.err.println("Directory  " + dir.toString() + " can't be created");
                        return Code.SYSTEM_ERROR;
                    }
                }
                if (!datFile.exists()) {
                    try {
                        if (!datFile.createNewFile()) {
                            System.err.println("File " + datFile.toString() + " can't be created");
                            return Code.SYSTEM_ERROR;
                        }
                    } catch (IOException e) {
                        System.err.println("File " + datFile.toString() + " can't be created");
                        return Code.SYSTEM_ERROR;
                    }
                }
                RandomAccessFile currentFile = null;
                try {
                    currentFile = new RandomAccessFile(datFile, "rw");
                } catch (FileNotFoundException e) {
                    System.err.println("This error is my fail. Check 'writeTableOnDisk' function");
                    return Code.SYSTEM_ERROR;
                }
                Code returnCode = writeToDatFile(currentFile, parsedStorage[indexOfDir][indexOfDatFile]);
                if (returnCode != Code.OK) {
                    return returnCode;
                }
                try {
                    currentFile.close();
                } catch (IOException e) {
                    System.err.println("File " + currentFile.toString() + " can't be closed.");
                    return Code.SYSTEM_ERROR;
                }
            }
            Code returnCode = cleanEmptyDir(dir);
            if (returnCode != Code.OK) {
                return returnCode;
            }
        }
        return Code.OK;
    }
}
