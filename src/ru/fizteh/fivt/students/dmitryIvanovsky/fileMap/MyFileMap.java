package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class MyFileMap {

    RandomAccessFile dbFile;
    Map dbMap;

    public String startShellString() {
        return "$ ";
    }

    public Code put(String[] args) {
        if (args.length != 2) {
            return Code.ERROR;
        }
        String key = args[0];
        String value = args[1];
        if (dbMap.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dbMap.get(key));
        } else {
            System.out.println("new");
        }
        dbMap.put(key, value);
        return Code.OK;
    }

    public Code get(String[] args) {
        if (args.length != 1) {
            return Code.ERROR;
        }
        String key = args[0];
        if (dbMap.containsKey(key)) {
            System.out.println("found");
            System.out.println(dbMap.get(key));
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public Code remove(String[] args) {
        if (args.length != 1) {
            return Code.ERROR;
        }
        String key = args[0];
        if (dbMap.containsKey(key)) {
            dbMap.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public void loadDbMap() throws IOException {
        if (dbFile.length() == 0) {
            return;
        }

        dbFile.seek(0);
        String key = dbFile.readUTF();
        dbFile.readChar();
        int separate = dbFile.readInt();

        long point = dbFile.getFilePointer();
        dbFile.seek(separate);
        String value = dbFile.readUTF();
        long point2 = dbFile.getFilePointer();

        dbMap.put(key, value);
        while (point < separate) {
            dbFile.seek(point);
            key = dbFile.readUTF();

            dbFile.readChar();
            dbFile.readInt();
            point = dbFile.getFilePointer();

            dbFile.seek(point2);

            value = dbFile.readUTF();
            point2 = dbFile.getFilePointer();

            dbMap.put(key, value);

        }
    }

    public MyFileMap(String db, Map map) throws IOException {
        //RandomAccessFile(System.getProperty("fizteh.db.dir"), "rw");
        Path pathDbFile = Paths.get(db);
        pathDbFile = pathDbFile.resolve("db.dat");
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        dbMap = map;
    }

    public MyFileMap(String db) throws IOException {
        //RandomAccessFile(System.getProperty("fizteh.db.dir"), "rw");
        Path pathDbFile = Paths.get(db);
        pathDbFile = pathDbFile.resolve("db.dat");
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        dbMap = new HashMap<String, String>();
        loadDbMap();
    }

    public void closeDbFile() throws IOException {
        dbFile.setLength(0);
        dbFile.seek(0);

        int len = 0;
        for (Object key : dbMap.keySet()) {
            len += key.toString().getBytes().length + 4 + 4;
        }
        for (Object key : dbMap.keySet()) {
            dbFile.writeUTF(key.toString());
            dbFile.writeChar('\0');
            dbFile.writeInt(len);
            len += dbMap.get(key).toString().length() * 2;
        }

        for (Object value : dbMap.values()) {
            dbFile.writeUTF(value.toString());
        }

        dbFile.close();
    }
}
