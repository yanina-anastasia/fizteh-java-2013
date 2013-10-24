package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

public class MyFileMap implements CommandAbstract {
    private final Path pathTables;
    private final CommandShell mySystem;
    String useNameTable;
    Map<String, Map<String, String>> dbData;
    HashSet<String> setMap;
    boolean err;
    boolean out;

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

    public MyFileMap() {
        this.pathTables = null;
        this.mySystem = null;
    }

    public MyFileMap(String pathT) throws ErrorFileMap {
        this.out = true;
        this.err = true;
        File file = null;
        try {
            file = new File(pathT);
        } catch (Exception e) {
            errPrint(pathT + " папка не открывается");
            throw new ErrorFileMap(pathT + " папка не открывается");
        }
        if (!file.exists()) {
            errPrint(pathT + " не существует");
            throw new ErrorFileMap(pathT + " не существует");
        }
        if (!file.isDirectory()) {
            errPrint(pathT + " не папка");
            throw new ErrorFileMap(pathT + " не папка");
        }
        this.useNameTable = "";
        this.pathTables = Paths.get(pathT);
        this.mySystem = new CommandShell(pathT, false, false);
        this.dbData = new HashMap(){};
        this.setMap = new HashSet<String>();

        if (checkBdDir(pathTables) == Code.ERROR) {
            throw new ErrorFileMap("Ошибка загрузки базы");
        }

        /*for (String key : dbData.keySet()) {
            outPrint("Таблица " + key);
            for (String k : dbData.get(key).keySet()) {
                outPrint(dbData.get(key).get(k));
            }
        }*/

    }

    public void exit() throws IOException {
        closeAllDb();
    }

    private Code checkBdDir(Path pathTables) {
        File currentFile = pathTables.toFile();
        for (File nameMap : currentFile.listFiles()) {
            if (!nameMap.isDirectory()) {
                errPrint(nameMap.getAbsolutePath() + " не папка");
                return Code.ERROR;
            } else {
                setMap.add(nameMap.getName());
            }
        }
        return Code.OK;
    }

    private Code loadDb(String nameMap) {
        try {
            //File currentFile = pathTables.toFile();
            //for (String nameMap : currentFile.list()) {
                //String nameMap =
                File currentFileMap = pathTables.resolve(nameMap).toFile();
                if (!currentFileMap.isDirectory()) {
                    errPrint(currentFileMap.getAbsolutePath() + " не директория");
                    return Code.ERROR;
                }
                dbData.put(nameMap, new HashMap<String, String>());

                File[] listFileMap = currentFileMap.listFiles();

                if (listFileMap == null || listFileMap.length == 0) {
                    //errPrint(currentFileMap.getAbsolutePath() + " папка пуста");
                    return Code.OK;
                }

                for (File nameDir : listFileMap) {
                    if (!nameDir.isDirectory()) {
                        errPrint(nameDir.getAbsolutePath() + " не директория");
                        return Code.ERROR;
                    }
                    String nameStringDir = nameDir.getName();
                    Pattern p = Pattern.compile("(^[0-9].dir$)|(^1[0-5].dir$)");
                    Matcher m = p.matcher(nameStringDir);

                    if (!(m.matches() && m.start() == 0 && m.end() == nameStringDir.length())) {
                        errPrint(nameDir.getAbsolutePath() + " неверное название папки");
                        return Code.ERROR;
                    }

                    File[] listNameDir = nameDir.listFiles();
                    if (listNameDir == null || listNameDir.length == 0) {
                        errPrint(nameDir.getAbsolutePath() + " папка пуста");
                        return Code.ERROR;
                    }
                    for (File randomFile : listNameDir) {

                        p = Pattern.compile("(^[0-9].dat$)|(^1[0-5].dat$)");
                        m = p.matcher(randomFile.getName());
                        int lenRandomFile = randomFile.getName().length();
                        if (!(m.matches() && m.start() == 0 && m.end() == lenRandomFile)) {
                            errPrint(randomFile.getAbsolutePath() + " неверное название файла");
                            return Code.ERROR;
                        }

                        Code res = loadDbMapFile(randomFile, dbData.get(nameMap), nameDir.getName());
                        if (res != Code.OK) {
                            return res;
                        }
                    }
                }


            //}
            return Code.OK;
        } catch (Exception e) {
            return Code.ERROR;
        }
    }

    private int getCode(String s) {
        if (s.charAt(1) == '.') {
            return Integer.parseInt(s.substring(0, 1));
        } else {
            return Integer.parseInt(s.substring(0, 2));
        }
    }

    private int getHashDir(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % 16;
        if (ndirectory < 0) {
            ndirectory *= -1;
        }
        return ndirectory;
    }

    private int getHashFile(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / 16 % 16;
        if (nfile < 0) {
            nfile *= -1;
        }
        return nfile;
    }

    public Code loadDbMapFile(File randomFile, Map dbMap, String nameDir) {
        if (randomFile.isDirectory()) {
            errPrint(randomFile.getAbsolutePath() + " не файл");
            return Code.ERROR;
        }
        int intDir = getCode(nameDir);
        int intFile = getCode(randomFile.getName());

        RandomAccessFile dbFile = null;
        try {
            dbFile = new RandomAccessFile(randomFile, "rw");
        } catch (Exception e) {
            errPrint(randomFile.getAbsolutePath() + " файл не открылся");
            return Code.ERROR;
        }

        Code res = Code.OK;
        try {
            if (dbFile.length() == 0) {
                errPrint(randomFile.getAbsolutePath() + " пустой файл");
                res = Code.ERROR;
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

                    if (getHashDir(key) != intDir || getHashFile(key) != intFile) {
                        errPrint(randomFile.getAbsolutePath() + " в файле несоответствующий ключ");
                        res = Code.ERROR;
                        return Code.ERROR;
                    }
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
            try {
                dbFile.close();
            } catch (Exception e) {
                errPrint(randomFile.getAbsolutePath() + " не закрылся");
                res = Code.ERROR;
            }
            return res;
        }
    }

    public void closeAllDb() throws IOException {
        for (String nameMap : dbData.keySet()) {
            mySystem.rm(new String[]{pathTables.resolve(nameMap).toString()});
            mySystem.mkdir(new String[]{pathTables.resolve(nameMap).toString()});
            if (dbData.get(nameMap).isEmpty()) {
                continue;
            }
            Map<String, String>[][] arrayMap = new HashMap[16][16];
            boolean[] useDir = new boolean[16];
            for (String key : dbData.get(nameMap).keySet()) {

                int ndirectory = getHashDir(key);
                int nfile = getHashFile(key);

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
        RandomAccessFile dbFile = null;
        try {
            dbFile = new RandomAccessFile(randomFile, "rw");
        } catch (Exception e) {
            errPrint(randomFile.getAbsolutePath() + " не открылся");
            throw new IOException();
        }
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
            errPrint("Ошибка записи базы");
        }  finally {
            try {
                dbFile.close();
            } catch (Exception e) {
                errPrint(randomFile.getAbsolutePath() + " не закрылся");
                throw new IOException();
            }
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
            errPrint("У команды put 2 аргумента");
            return Code.ERROR;
        }
        String key = args[0];
        String value = args[1];
        if (dbData.get(useNameTable).containsKey(key)) {
            outPrint("overwrite");
            outPrint(dbData.get(useNameTable).get(key));
        } else {
            outPrint("new");
        }
        dbData.get(useNameTable).put(key, value);
        return Code.OK;
    }

    public Code multiPut(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
            return Code.ERROR;
        } else {
            if (put(args) != Code.ERROR) {
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code get(String[] args) {
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
            errPrint("У команды get 1 аргумент");
            return Code.ERROR;
        }
        String key = args[0];
        if (dbData.get(useNameTable).containsKey(key)) {
            outPrint("found");
            outPrint(dbData.get(useNameTable).get(key));
        } else {
            outPrint("not found");
        }
        return Code.OK;
    }

    public Code multiGet(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
            return Code.ERROR;
        } else {
            return get(args);
        }
    }

    public Code remove(String[] args) {
        args = myParsing(args);
        if (args[0].length() <= 0 || args[1].length() != 0) {
            errPrint("У команды remove 1 аргумент");
            return Code.ERROR;
        }
        String key = args[0];
        if (dbData.get(useNameTable).containsKey(key)) {
            dbData.get(useNameTable).remove(key);
            outPrint("removed");
        } else {
            outPrint("not found");
        }
        return Code.OK;
    }

    public Code multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
            return Code.ERROR;
        } else {
            if (remove(args) != Code.ERROR) {
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code use(String[] args) throws ErrorFileMap {
        if (args.length != 1) {
            errPrint("У команды use 1 аргумент");
            return Code.ERROR;
        } else {
            String nameTable = args[0];
            if (setMap.contains(nameTable)) {
                if (!dbData.containsKey(nameTable)) {
                    if (loadDb(nameTable) != Code.OK) {
                        errPrint("Ошибка формата хранения таблицы " + nameTable);
                        return Code.ERROR;
                    }
                }
                if (!nameTable.equals(useNameTable)) {
                    useNameTable = nameTable;
                }
                outPrint("using " + nameTable);
                return Code.OK;
            } else {
                outPrint(nameTable + " not exists");
                //useNameTable = "";
                return Code.ERROR;
            }
        }
    }

    public Code drop(String[] args) {
        if (args.length != 1) {
            errPrint("У команды drop 1 аргумент");
            return Code.ERROR;
        } else {
            String nameTable = args[0];
            if (nameTable.equals(useNameTable)) {
                useNameTable = "";
            }
            if (setMap.contains(nameTable)) {
                if (dbData.containsKey(nameTable)) {
                    dbData.remove(nameTable);
                }
                mySystem.rm(new String[]{pathTables.resolve(nameTable).toString()});
                setMap.remove(nameTable);
                outPrint("dropped");
                return Code.OK;
            } else {
                outPrint(nameTable + " not exists");
                return Code.ERROR;
            }
        }
    }

    public Code create(String[] args) {
        if (args.length != 1) {
            errPrint("У команды create 1 аргумент");
            return Code.ERROR;
        } else {
            String nameTable = args[0];
            if (setMap.contains(nameTable)) {
                outPrint(nameTable + " exists");
                return Code.ERROR;
            } else {
                mySystem.mkdir(new String[]{pathTables.resolve(nameTable).toString()});
                setMap.add(nameTable);
                //dbData.put(nameTable, new HashMap<String, String>());
                outPrint("created");
                return Code.OK;
            }
        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.println(message);
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}

class ErrorFileMap extends Exception {
    public ErrorFileMap(String text) {
        super(text);
    }
}

