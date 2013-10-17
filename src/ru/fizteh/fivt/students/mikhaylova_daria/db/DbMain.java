package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import ru.fizteh.fivt.students.mikhaylova_daria.shell.Parser;

public class DbMain {
    private static String workingDirectoryName;

    public static void main(String[] arg) {
        workingDirectoryName = System.getProperty("fizteh.db.dir");
        try {
            //readerDateBase();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        HashMap<String,String> commandsList = new HashMap<String, String>();
        commandsList.put("put", "put");
        commandsList.put("get","get");
        commandsList.put("remove", "remove");
        commandsList.put("exit","exit");
        try {
            Parser.parser(arg, FileMap.class, commandsList);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void writerDateBase() throws Exception {
        File workingDirectory = new File(workingDirectoryName);
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
            currentOffsetOfValue = currentPosition - FileMap.fileMap.get(key).getBytes("UTF8").length;//начало строки
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

            String key;
            String value;
            long offsetOfKey;
            final long firstOffset;
            int offsetOfValue;
            key = dateBase.readUTF();
            System.out.println(key);
            dateBase.readByte();
            offsetOfValue = dateBase.readInt();
            System.out.println(offsetOfValue);
            offsetOfKey = dateBase.getFilePointer();
            dateBase.seek(offsetOfValue);
            firstOffset = dateBase.getFilePointer();
            value = dateBase.readUTF();
            System.out.println(value);
            FileMap.fileMap.put(key, value);
            while (firstOffset > offsetOfKey) {
                dateBase.seek(offsetOfKey);
                key = dateBase.readUTF();
                System.out.println(key);
                dateBase.readByte();
                offsetOfValue = dateBase.readInt();
                offsetOfKey = dateBase.getFilePointer();
                dateBase.seek(offsetOfValue);
                value = dateBase.readUTF();
                System.out.println(value);
                FileMap.fileMap.put(key, value);
            }
            dateBase.close();
        }
     }



