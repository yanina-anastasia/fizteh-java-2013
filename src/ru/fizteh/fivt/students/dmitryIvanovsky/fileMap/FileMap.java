package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMap implements Table {

    private final Path pathDb;
    private final CommandShell mySystem;
    String nameTable;
    Map<String, String> tableData;
    Map<String, String> changeTable;
    boolean existDir = false;

    public FileMap(Path pathDb, String nameTable) throws Exception {
        this.nameTable = nameTable;
        this.pathDb = pathDb;
        this.changeTable = new HashMap<>();
        this.tableData = new HashMap<>();
        this.mySystem = new CommandShell(pathDb.toString(), false, false);

        File theDir = new File(String.valueOf(pathDb.resolve(nameTable)));
        if (!theDir.exists()) {
            try {
                mySystem.mkdir(new String[]{pathDb.resolve(nameTable).toString()});
                existDir = true;
            } catch (Exception e) {
                e.addSuppressed(new ErrorFileMap("Не могу создать папку таблицы " + nameTable));
                throw e;
            }
        }

        try {
            loadTable(nameTable);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Ошибка формата хранения таблицы " + nameTable));
            throw e;
        }
    }

    public void exit() throws Exception {
        closeTable();
    }

    private void loadTable(String nameMap) throws Exception {
        File currentFileMap = pathDb.resolve(nameMap).toFile();
        if (!currentFileMap.isDirectory()) {
            throw new ErrorFileMap(currentFileMap.getAbsolutePath() + " не директория");
        }

        File[] listFileMap = currentFileMap.listFiles();

        if (listFileMap == null || listFileMap.length == 0) {
            //errPrint(currentFileMap.getAbsolutePath() + " папка пуста");
            return ;
        }

        for (File nameDir : listFileMap) {
            if (!nameDir.isDirectory()) {
                throw new ErrorFileMap(nameDir.getAbsolutePath() + " не директория");
            }
            String nameStringDir = nameDir.getName();
            Pattern p = Pattern.compile("(^[0-9].dir$)|(^1[0-5].dir$)");
            Matcher m = p.matcher(nameStringDir);

            if (!(m.matches() && m.start() == 0 && m.end() == nameStringDir.length())) {
                throw new ErrorFileMap(nameDir.getAbsolutePath() + " неверное название папки");
            }

            File[] listNameDir = nameDir.listFiles();
            if (listNameDir == null || listNameDir.length == 0) {
                //throw new ErrorFileMap(nameDir.getAbsolutePath() + " папка пуста");
                return;
            }
            for (File randomFile : listNameDir) {

                p = Pattern.compile("(^[0-9].dat$)|(^1[0-5].dat$)");
                m = p.matcher(randomFile.getName());
                int lenRandomFile = randomFile.getName().length();
                if (!(m.matches() && m.start() == 0 && m.end() == lenRandomFile)) {
                    throw new ErrorFileMap(randomFile.getAbsolutePath() + " неверное название файла");
                }

                try {
                    loadTableFile(randomFile, tableData, nameDir.getName());
                } catch (Exception e) {
                    e.addSuppressed(new ErrorFileMap("Ошибка в файле " + randomFile.getAbsolutePath()));
                    throw e;
                }
            }
        }
    }

    public void loadTableFile(File randomFile, Map dbMap, String nameDir) throws Exception {
        if (randomFile.isDirectory()) {
            throw new ErrorFileMap("файл данных не может быть директорией");
        }
        int intDir = FileMapUtils.getCode(nameDir);
        int intFile = FileMapUtils.getCode(randomFile.getName());

        RandomAccessFile dbFile = null;
        try {
            dbFile = new RandomAccessFile(randomFile, "rw");
        } catch (Exception e) {
            throw new ErrorFileMap("файл не открылся");
        }

        Exception error = null;
        try {
            if (dbFile.length() == 0) {
                throw new ErrorFileMap("пустой файл");
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

                    if (FileMapUtils.getHashDir(key) != intDir || FileMapUtils.getHashFile(key) != intFile) {
                        throw new ErrorFileMap("в файле несоответствующий ключ");
                    }
                    dbMap.put(key, value);

                    vectorByte.clear();
                    dbFile.seek(currentPoint);
                } else {
                    vectorByte.add(currentByte);
                }
            }
        } catch (Exception e) {
            error.addSuppressed(e);
            throw error;
        } finally {
            try {
                dbFile.close();
            } catch (Exception e) {
                ErrorFileMap notClose = new ErrorFileMap("не закрылся");
                error.addSuppressed(e);
                error.addSuppressed(notClose);
                throw error;
            }
        }
    }

    public void closeTable() throws Exception {
        mySystem.rm(new String[]{pathDb.resolve(nameTable).toString()});
        existDir = false;
        mySystem.mkdir(new String[]{pathDb.resolve(nameTable).toString()});
        existDir = true;
        if (tableData.isEmpty()) {
            return;
        }

        Map<String, String>[][] arrayMap = new HashMap[16][16];
        boolean[] useDir = new boolean[16];
        for (String key : tableData.keySet()) {

            int ndirectory = FileMapUtils.getHashDir(key);
            int nfile = FileMapUtils.getHashFile(key);

            if (arrayMap[ndirectory][nfile] == null) {
                arrayMap[ndirectory][nfile] = new HashMap<String, String>();
            }
            arrayMap[ndirectory][nfile].put(key, tableData.get(key));
            useDir[ndirectory] = true;
        }
        for (int i = 0; i < 16; ++i) {
            if (useDir[i]) {
                Integer numDir = i;
                Path tmp = pathDb.resolve(nameTable).resolve(numDir.toString() + ".dir");
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
                Path tmp = pathDb.resolve(nameTable).resolve(numDir.toString() + ".dir");
                closeTableFile(tmp.resolve(numFile.toString() + ".dat").toFile(), arrayMap[i][j]);
            }
        }
        tableData.clear();
    }

    public void closeTableFile(File randomFile, Map<String, String> curMap) throws Exception {
        if (curMap == null || curMap.isEmpty()) {
            return;
        }
        RandomAccessFile dbFile = null;
        ErrorFileMap error = null;
        try {
            dbFile = new RandomAccessFile(randomFile, "rw");
        } catch (Exception e) {
            throw new ErrorFileMap(randomFile.getAbsolutePath() + " не открылся");
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
            error.addSuppressed(e);
            throw error;
        }  finally {
            try {
                dbFile.close();
            } catch (Exception e) {
                ErrorFileMap notClose = new ErrorFileMap(randomFile.getAbsolutePath() + " не закрылся");
                error.addSuppressed(e);
                error.addSuppressed(notClose);
                throw error;
            }
        }
    }

    public String getName() {
        return nameTable;
    }

    public int changeKey() {
        return changeTable.size();
    }

    private Boolean onlySpace(String s) {
        for (char c : s.toCharArray()) {
            if (c != ' ') {
                return false;
            }
        }
        return true;
    }

    public String put(String key, String value) {
        if (key == null || value == null || key.equals("") || value.equals("")) {
            throw new IllegalArgumentException();
        }
        if (onlySpace(key) || onlySpace(value)) {
            throw new IllegalArgumentException();
        }
        if (key.contains("\n") || value.contains("\n")) {
            throw new IllegalArgumentException();
        }
        if (tableData.containsKey(key)) {
            String oldValue = tableData.get(key);

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, oldValue);
            }

            tableData.put(key, value);
            return oldValue;
        } else {

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, null);
            }

            tableData.put(key, value);
            return null;
        }
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (tableData.containsKey(key)) {
            return tableData.get(key);
        } else {
            return null;
        }
    }

    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (tableData.containsKey(key)) {

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, tableData.get(key));
            }

            String value = tableData.get(key);
            tableData.remove(key);
            return value;
        } else {
            return null;
        }
    }

    public int size() {
        return tableData.size();
    }

    public int commit() {
        int res = changeTable.size();
        changeTable.clear();
        return res;
    }

    public int rollback() {
        for (String key : changeTable.keySet()) {
            String value = changeTable.get(key);
            if (value == null) {
                tableData.remove(key);
            } else {
                tableData.put(key, changeTable.get(key));
            }
        }
        changeTable.clear();
        return changeTable.size();
    }

}
