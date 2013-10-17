package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class MyFileMap implements CommandAbstract {

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

    public MyFileMap(String db, Map<String, String> dbMap) throws IOException {
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

    public boolean selfParsing() {
        return true;
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

        byte[] arrayByte;
        Vector<Byte> vectorByte = new Vector<Byte>();
        long separator = -1;

        while (dbFile.getFilePointer() != dbFile.length()) {
            byte currentByte = dbFile.readByte();
            if (currentByte == '\0') {
                int point1 = dbFile.readInt();
                if (separator == -1) {
                    separator = point1;
                }
                long currentPoint = dbFile.getFilePointer();

                while (dbFile.getFilePointer() != separator) {
                    if (dbFile.readByte() == '\0') {
                        break;
                    }
                }

                int point2;
                if (dbFile.getFilePointer() == separator) {
                    point2 = (int) dbFile.length();
                } else {
                    point2 = dbFile.readInt();
                }

                dbFile.seek(point1);

                arrayByte = new byte[point2 - point1];
                dbFile.readFully(arrayByte);
                String value = new String(arrayByte, "UTF8");

                arrayByte = new byte[vectorByte.size()];
                for (int i = 0; i < vectorByte.size(); ++i) {
                    arrayByte[i] = vectorByte.elementAt(i).byteValue();
                }
                String key = new String(arrayByte, "UTF8");

                dbMap.put(key, value);

                vectorByte.clear();
                dbFile.seek(currentPoint);
            } else {
                vectorByte.add(currentByte);
            }
        }
    }

    public void closeDbFile() throws IOException {
        dbFile.setLength(0);
        dbFile.seek(0);
        int len = 0;

        for (String key : dbMap.keySet()) {
            len += key.getBytes("UTF8").length + 1 + 4;
        }

        for (String key : dbMap.keySet()) {
            dbFile.write(key.getBytes("UTF8"));
            dbFile.writeByte(0);
            dbFile.writeInt(len);

            long point = dbFile.getFilePointer();

            dbFile.seek(len);
            String value = dbMap.get(key);
            dbFile.write(value.getBytes("UTF8"));
            len += value.getBytes("UTF8").length;

            dbFile.seek(point);
        }

    }

    public String startShellString() {
        return "$ ";
    }

    public String[] myParsing(String[] args) {
        String arg = args[0];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int i = 0;
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) != ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) != ' ') {
            key.append(arg.charAt(i));
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length()) {
            value.append(arg.charAt(i));
            ++i;
        }
        return new String[]{key.toString(), value.toString()};
    }

    public Code put(String[] args) {
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() <= 0) {
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
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
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
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
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
