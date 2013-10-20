package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import ru.fizteh.fivt.students.ryabovaMaria.shell.Shell;

public class FileMap {
    public static Shell shell;
    public static FileMapCommands commands;
    public static HashMap<String, String> loadList = new HashMap();
    public static File currentDir;
    
    public static void loadFile() throws Exception {
        File dbFile = currentDir.toPath().resolve("db.dat").toFile();
        if (!dbFile.exists()) {
            throw new FileNotFoundException();
        }
        if (!dbFile.isFile()) {
            throw new Exception("db.dat is not a file");
        }
        RandomAccessFile db;
        db = new RandomAccessFile(dbFile, "r");
        try {
            long curPointer = 0;
            long lastPointer = 0;
            long length = db.length();
            if (length == 0) {
                return;
            }
            db.seek(0);
            String lastKey = "";
            int lastOffset = 0;
            while (curPointer < length) {
                byte curByte = db.readByte();
                if (curByte == '\0') {
                    byte[] byteKey = new byte[(int) curPointer - (int) lastPointer];
                    curPointer = db.getFilePointer();
                    db.seek(lastPointer);
                    db.readFully(byteKey);
                    db.seek(curPointer);
                    String currentKey = new String(byteKey, "UTF-8");
                    int offset = db.readInt();
                    if (!lastKey.isEmpty()) {
                        byte[] byteValue = new byte[offset - lastOffset];
                        curPointer = db.getFilePointer();
                        db.seek(lastOffset);
                        db.readFully(byteValue);
                        String lastValue = new String(byteValue, "UTF-8");
                        db.seek(curPointer);
                        if (loadList.containsKey(lastKey)) {
                            System.err.println(lastKey + " meets twice in db.dat");
                            System.exit(1);
                        }
                        loadList.put(lastKey, lastValue);
                    }
                    lastOffset = offset;
                    lastKey = currentKey;
                    lastPointer = db.getFilePointer();
                }
                curPointer = db.getFilePointer();
            }
            if (lastOffset == 0 || lastKey.isEmpty()) {
                System.err.println("Incorrect db");
                System.exit(1);
            }
            byte[] byteValue = new byte[(int) length - lastOffset];
            db.seek(lastOffset);
            db.readFully(byteValue);
            String lastValue = new String(byteValue, "UTF-8");
            if (loadList.containsKey(lastKey)) {
                System.err.println(lastKey + " meets twice in db.dat");
                System.exit(1);
            }
            loadList.put(lastKey, lastValue);
        } catch (Exception e) {
            db.close();
            throw new Exception(e);
        }
        db.close();
    }
    
    public static void writeIntoFile() throws Exception {
        File dbFile = commands.currentDir.toPath().resolve("db.dat").toFile();
        RandomAccessFile db;
        db = new RandomAccessFile(dbFile, "rw");
        try {    
            db.setLength(0);
            Iterator<Map.Entry<String, String>> it;
            it = commands.list.entrySet().iterator();
            long[] pointers = new long[commands.list.size()];
            int counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String key = m.getKey();
                db.write(key.getBytes("UTF-8"));
                db.write("\0".getBytes("UTF-8"));
                pointers[counter] = db.getFilePointer();
                db.seek(pointers[counter] + 4);
                ++counter;
            }
            it = commands.list.entrySet().iterator();
            counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String value = m.getValue();
                int curPointer = (int) db.getFilePointer();
                db.seek(pointers[counter]);
                db.writeInt(curPointer);
                db.seek(curPointer);
                db.write(value.getBytes("UTF-8"));
                ++counter;
            }
        } catch (Exception e) {
            db.close();
            throw new Exception(e);
        }
        db.close();
    }
    
    public static void main(String[] args) {
        String getPropertyString = System.getProperty("user.dir");
        if (getPropertyString == null) {
            System.err.println("I can't find this directory");
            System.exit(1);
        }
        currentDir = new File(getPropertyString);
        try {
            loadFile();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            System.err.println("Incorrect files db.dat");
            System.exit(1);
        }
        commands = new FileMapCommands(loadList);
        shell = new Shell(commands, "user.dir");
        int argc = args.length;
        if (argc == 0) {
            Shell.interactive();
        } else {
            Shell.packet(args);
        }
        try { 
            FileMap.writeIntoFile();
        } catch (Exception e) {
            System.err.println("I can't write into db.dat");
            System.exit(1);
        }
    }
}
