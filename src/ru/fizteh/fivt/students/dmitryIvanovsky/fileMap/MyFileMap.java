package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

public class MyFileMap implements CommandAbstract {

    private final Path pathTables;
    private final CommandShell mySystem;
    String useNameTable;
    Map<String, Map<String, String>> dbData;
    Set<String> setEmptyTable;

    public MyFileMap() {
        this.pathTables = null;
        this.mySystem = null;
    }

    public MyFileMap(String pathT) throws IOException {
        File file = new File(pathT);
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException();
        }
        this.useNameTable = "";
        this.pathTables = Paths.get(pathT);
        this.mySystem = new CommandShell(pathT);
        this.dbData = new HashMap(){};
        this.setEmptyTable = new HashSet<String>();
        if (loadDb() == Code.ERROR) {
            throw new IOException();
        }

        /*for (String key : dbData.keySet()) {
            System.out.println("Таблица "+key);
            for (String k : dbData.get(key).keySet()) {
                System.out.println(dbData.get(key).get(k));
            }
        }*/

    }

    public Map<String, String> mapComamnd() {
        Map<String, String> commandList = new HashMap<String, String>(){ {
            put("put", "multiPut");
            put("get", "multiGet");
            put("remove", "multiRemove");
            put("create", "create");
            put("drop", "drop");
            put("use", "use");
        }};
        return commandList;
    }

    public Map<String, Boolean> mapSelfParsing() {
        Map<String, Boolean> commandList = new HashMap<String, Boolean>(){ {
            put("put", true);
            put("get", true);
            put("remove", true);
            put("create", false);
            put("drop", false);
            put("use", false);
        }};
        return commandList;
    }

    //Не секлано
    public void exit() throws IOException {
        closeDb();
        //for (String table : setEmptyTable) {
        //    mySystem.rm(new String[]{table});
        //}
    }

    private Code loadDb() throws IOException {
        try {
            File currentFile = pathTables.toFile();
            for (String nameMap : currentFile.list()) {
                dbData.put(nameMap, new HashMap<String, String>());
                File currentFileMap = pathTables.resolve(nameMap).toFile();
                for (File nameDir : currentFileMap.listFiles()) {
                    for (File randomFile : nameDir.listFiles()) {
                        Code res = loadDbMapFile(randomFile, dbData.get(nameMap));
                        if (res != Code.OK) {
                            return res;
                        }
                    }
                }
            }
            return Code.OK;
        } catch (Exception e) {
            return Code.ERROR;
        }
    }

    public Code loadDbMapFile(File randomFile, Map dbMap) throws IOException {
        RandomAccessFile dbFile = new RandomAccessFile(randomFile, "rw");
        Code res = Code.OK;
        try {
            if (dbFile.length() == 0) {
                System.err.println("Обнаружен пустой файл");
                return res;
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
        } catch (Exception e) {
            res = Code.ERROR;
        } finally {
            dbFile.close();
            return res;
        }
    }

    public void closeDb() throws IOException {
        for (String nameMap : dbData.keySet()) {
            mySystem.rm(new String[]{pathTables.resolve(nameMap).toString()});
            mySystem.mkdir(new String[]{pathTables.resolve(nameMap).toString()});

            Map<String, String>[][] arrayMap = new HashMap[16][16];
            boolean[] useDir = new boolean[16];
            for (String key : dbData.get(nameMap).keySet()) {
                int hashcode = key.hashCode();
                int ndirectory = hashcode % 16;
                int nfile = hashcode / 16 % 16;
                if (arrayMap[ndirectory][nfile] == null) {
                    arrayMap[ndirectory][nfile] = new HashMap<String, String>();
                }
                arrayMap[ndirectory][nfile].put(key, dbData.get(nameMap).get(key));
                useDir[ndirectory] = true;
            }
            for (int i = 0; i < 16; ++i) {
                if (useDir[i]) {
                    Integer numDir = i;
                    Path tmp = pathTables.resolve(nameMap).resolve(numDir.toString() + ".dir");
                    mySystem.mkdir(new String[]{tmp.toString()});
                }
            }
            for (int i = 0; i < 16; ++i) {
                if (!useDir[i]) {
                    continue;
                }
                for (int j = 0; j < 16; ++j) {
                    Integer numDir = i;
                    Integer numFile = j;
                    Path tmp = pathTables.resolve(nameMap).resolve(numDir.toString() + ".dir");
                    closeDbFile(tmp.resolve(numFile.toString() + ".dat").toFile(), arrayMap[i][j]);
                }
            }
        }
    }

    public void closeDbFile(File randomFile, Map<String, String> curMap) throws IOException {
        if (curMap == null || curMap.isEmpty()) {
            return;
        }
        RandomAccessFile dbFile = new RandomAccessFile(randomFile, "rw");
        try {
            dbFile.setLength(0);
            dbFile.seek(0);
            int len = 0;

            for (String key : curMap.keySet()) {
                len += key.getBytes("UTF8").length + 1 + 4;
            }

            for (String key : curMap.keySet()) {
                dbFile.write(key.getBytes("UTF8"));
                dbFile.writeByte(0);
                dbFile.writeInt(len);

                long point = dbFile.getFilePointer();

                dbFile.seek(len);
                String value = curMap.get(key);
                dbFile.write(value.getBytes("UTF8"));
                len += value.getBytes("UTF8").length;

                dbFile.seek(point);
            }
        } catch (Exception e) {
            System.err.println("Ошибка записи базы");
        }  finally {
            dbFile.close();
        }

    }

    public String startShellString() {
        return "$ ";
    }

    public String[] myParsing(String[] args) {
        String arg = args[0].trim();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int i = 0;
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
        if (dbData.get(useNameTable).containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dbData.get(useNameTable).get(key));
        } else {
            System.out.println("new");
        }
        dbData.get(useNameTable).put(key, value);
        return Code.OK;
    }

    public Code multiPut(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            if (put(args) != Code.ERROR) {
                setEmptyTable.remove(useNameTable);
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code get(String[] args) {
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
            System.err.println("У команды get 1 аргумент");
            return Code.ERROR;
        }
        String key = args[0];
        if (dbData.get(useNameTable).containsKey(key)) {
            System.out.println("found");
            System.out.println(dbData.get(useNameTable).get(key));
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public Code multiGet(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            return get(args);
        }
    }

    public Code remove(String[] args) {
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
            System.err.println("У команды remove 1 аргумент");
            return Code.ERROR;
        }
        String key = args[0];
        if (dbData.get(useNameTable).containsKey(key)) {
            dbData.get(useNameTable).remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public Code multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            if (remove(args) != Code.ERROR) {
                if (dbData.get(useNameTable).isEmpty()) {
                    setEmptyTable.add(useNameTable);
                }
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code use(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("У команды use 1 аргумент");
            return Code.ERROR;
        } else {
            String nameTable = args[0];
            if (dbData.containsKey(nameTable)) {
                if (!nameTable.equals(useNameTable)) {
                    useNameTable = nameTable;
                }
                System.out.println("using tablename");
                return Code.OK;
            } else {
                System.out.println("tablename not exists");
                return Code.ERROR;
            }
        }
    }

    public Code drop(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("У команды drop 1 аргумент");
            return Code.ERROR;
        } else {
            String dropTable = args[0];
            if (dropTable.equals(useNameTable)) {
                useNameTable = "";
            }
            if (dbData.containsKey(args[0])) {
                setEmptyTable.remove(args[0]);
                dbData.remove(args[0]);
                System.out.println("dropped");
                return Code.OK;
            } else {
                System.out.println("tablename not exists");
                return Code.ERROR;
            }
        }
    }

    public Code create(String[] args) {
        if (args.length != 1) {
            System.err.println("У команды create 1 аргумент");
            return Code.ERROR;
        } else {
            if (dbData.containsKey(args[0])) {
                System.out.println("tablename exists");
                return Code.ERROR;
            } else {
                setEmptyTable.add(args[0]);
                dbData.put(args[0], new HashMap<String, String>());
                System.out.println("created");
                return Code.OK;
            }
        }
    }
}
