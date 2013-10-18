package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Vector;

import ru.fizteh.fivt.students.mikhaylova_daria.shell.Parser;

public class DbMain {
    private static String workingDirectoryName;

    public static void main(String[] arg) {
        workingDirectoryName = System.getProperty("fizteh.db.dir");
        try {
            readerDateBase();
        } catch (Exception e) {
            System.err.println(e.getMessage() + ": File will be created");
        }
        HashMap<String, String> commandsList = new HashMap<String, String>();
        commandsList.put("put", "put");
        commandsList.put("get", "get");
        commandsList.put("remove", "remove");
        commandsList.put("exit", "exit");
        try {
            Parser.parser(arg, FileMap.class, commandsList);
            writerDateBase();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void writerDateBase() throws Exception {
        File workingDirectory = new File(workingDirectoryName);
        if (!workingDirectory.isDirectory()) {
            System.err.println(workingDirectoryName + "is not directory");
            System.exit(1);
        }
        RandomAccessFile dateBase = null;
        try {
            dateBase = new RandomAccessFile(workingDirectory.toPath().resolve("db.dat").toFile(), "rw");
            dateBase.setLength(0);
        } catch (Exception e) {
            throw new Exception("Creating" + workingDirectory.toPath().resolve("db.dat").toFile()
                    + "is not possible");
        }
        HashMap<String, Long> offsets = new HashMap<String, Long>();
        long currentOffsetOfValue;
        long offset = dateBase.getFilePointer();
        for (String key: FileMap.fileMap.keySet()) {
            dateBase.write(key.getBytes("UTF8"));
            dateBase.write("\0".getBytes());
            offset = dateBase.getFilePointer();
            offsets.put(key, offset);
            dateBase.seek(dateBase.getFilePointer() + 4);
            currentOffsetOfValue = dateBase.getFilePointer();
        }

        long currentPosition = 0;
        for (String key: FileMap.fileMap.keySet()) {
            dateBase.write(FileMap.fileMap.get(key).getBytes("UTF8")); // выписали значение
            currentPosition  = dateBase.getFilePointer();
            currentOffsetOfValue = currentPosition - FileMap.fileMap.get(key).getBytes("UTF8").length; //начало строки
            dateBase.seek(offsets.get(key));
            Integer lastOffsetInt = new Long(currentOffsetOfValue).intValue();
            dateBase.writeInt(lastOffsetInt);
            dateBase.seek(currentPosition);
         }
         dateBase.close();
    }

    static void readerDateBase() throws Exception {
        File workingDirectory = new File(workingDirectoryName);
        RandomAccessFile dateBase = null;
        try {
            dateBase = new RandomAccessFile(workingDirectory.toPath().resolve("db.dat").toFile(), "r");
        } catch (FileNotFoundException e) {
            throw new Exception("File is not found " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Opening isn't possible");
        }
        HashMap<Integer, String> offsetAndKeyMap = new HashMap<Integer, String>();
        HashMap<String, Integer> keyAndValueLength = new HashMap<String, Integer>();
        String key = readKey(dateBase);
        if (keyAndValueLength.containsKey(key)) {
            System.err.println("1Bad dates");
            dateBase.close();
            System.exit(1);
        }
        Integer offset = 0;
        try {
            offset = dateBase.readInt();
        } catch (EOFException e) {
            System.err.println("1Bad file");
            dateBase.close();
            System.exit(1);
        }
        offsetAndKeyMap.put(offset, key);
        final int firstOffset = offset;
        try {
            int lastOffset = offset;
            String lastKey = null;
            while (dateBase.getFilePointer() < firstOffset) {
                lastKey = key;
                key = readKey(dateBase);
                lastOffset = offset;
                offset = dateBase.readInt();
                offsetAndKeyMap.put(offset, key);
                keyAndValueLength.put(lastKey, offset - lastOffset);
                if (keyAndValueLength.containsKey(key)) {
                    System.err.println("Bad dates");
                    dateBase.close();
                    System.exit(1);
                }
            }
            keyAndValueLength.put(key, (int) dateBase.length() - offset);
        } catch (EOFException e) {
            System.err.println("Bad file");
            dateBase.close();
            System.exit(1);
        }
        int lengthOfValue = 0;
        try {
            while (dateBase.getFilePointer() < dateBase.length()) {
                int currentOffset = (int) dateBase.getFilePointer();
                if (!offsetAndKeyMap.containsKey(currentOffset)) {
                    System.err.println("Bad file");
                    dateBase.close();
                    System.exit(1);
                } else {
                    key = offsetAndKeyMap.get(currentOffset);
                    lengthOfValue = keyAndValueLength.get(key);
                }
                byte[] valueInBytes = new byte[lengthOfValue];
                for (int i = 0; i < lengthOfValue; ++i) {
                    valueInBytes[i] = dateBase.readByte();
                }
                String value = new String(valueInBytes, "UTF8");
                FileMap.fileMap.put(key, value);
            }
        } catch (EOFException e) {
            System.err.println("Bad File");
            dateBase.close();
            System.exit(1);
        }
        dateBase.close();
    }

    private static String readKey(RandomAccessFile dateBase) throws Exception {
        Vector<Byte> keyBuilder = new Vector<Byte>();
        byte buf = dateBase.readByte();
        try {
            while (buf != "\0".getBytes("UTF8")[0]) {
                keyBuilder.add(buf);
                buf = dateBase.readByte();
            }
        } catch (EOFException e) {
            System.err.println("Bad file");
            dateBase.close();
            System.exit(1);
        }
        String key = null;
        try {
            byte[] keyInBytes = new byte[keyBuilder.size()];
            for (int i = 0; i < keyBuilder.size(); ++i) {
                keyInBytes[i] = keyBuilder.elementAt(i);
            }
            key = new String(keyInBytes, "UTF8");
        } catch (Exception e) {
            System.err.println("Reading Error");
            dateBase.close();
            System.exit(1);
        }
        return key;
    }
 }



