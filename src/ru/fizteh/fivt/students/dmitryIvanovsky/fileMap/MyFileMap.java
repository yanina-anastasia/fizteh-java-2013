package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

public class MyFileMap implements CommandAbstract{

    private final RandomAccessFile dbFile;
    private final Path pathDbFile;
    Map<String, String> dbMap;
    String nameTable = "db.dat";

    public MyFileMap() {
        this.dbFile = null;
        this.pathDbFile = null;
    }

    public MyFileMap(RandomAccessFile dbFile, Path pathDbFile) {
        this.dbFile = dbFile;
        this.pathDbFile = pathDbFile;
        dbMap = new HashMap<String, String>();
    }

    public MyFileMap(String db, Map dbMap) throws IOException {
        this.pathDbFile = Paths.get(db).resolve(nameTable);
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        this.dbMap = dbMap;
    }

    public MyFileMap(String db) throws IOException {
        this.pathDbFile = Paths.get(db).resolve(nameTable);
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        dbMap = new HashMap<String, String>();
        loadDbMap();
    }

    public boolean isEmpty() {
        return dbMap.isEmpty();
    }

    public void exit() throws IOException {
        if (!isEmpty()) {
            closeDbFile();
        }
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
            len += dbMap.get(key).toString().getBytes().length + 2;
        }

        for (Object value : dbMap.values()) {
            dbFile.writeUTF(value.toString());
        }

        long lengthFile = dbFile.length();
        dbFile.close();
        if (lengthFile == 0) {
            CommandShell sys = new CommandShell(pathDbFile.toString());
            sys.rm(new String[]{nameTable});
        }
    }

    public String startShellString() {
        return "$ ";
    }

    public Code put(String[] args) {
        if (args.length != 2) {
            System.err.println("У команды put 2 аргумента");
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
            System.err.println("У команды get 1 аргумент");
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
            System.err.println("У команды remove 1 аргумент");
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


}
