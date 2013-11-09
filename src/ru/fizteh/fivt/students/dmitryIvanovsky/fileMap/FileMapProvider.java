package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.convertStringToClass;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.myParsing;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.parseValue;
import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.getStringFromElement;


public class FileMapProvider implements CommandAbstract, TableProvider {

    private final Path pathDb;
    private final CommandShell mySystem;
    String useNameTable;
    Set<String> setDirTable;
    FileMap dbData;
    boolean err;
    boolean out;
    Map<String, FileMap> mapFileMap;

    final HashSet allowType = new HashSet(){ {
        add(String.class);
        add(Boolean.class);
        add(Double.class);
        add(Float.class);
        add(Byte.class);
        add(Long.class);
        add(Integer.class);
    }};

    public Map<String, Object[]> mapComamnd() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put",      new Object[] {"multiPut",      true,  2 });
            put("get",      new Object[] {"multiGet",      false, 1 });
            put("remove",   new Object[] {"multiRemove",   false, 1 });
            put("create",   new Object[] {"multiCreate",        true, 1 });
            put("drop",     new Object[] {"multiDrop",          false, 1 });
            put("use",      new Object[] {"multiUse",           false, 1 });
            put("size",     new Object[] {"multiSize",     false, 0 });
            put("commit",   new Object[] {"multiCommit",   false, 0 });
            put("rollback", new Object[] {"multiRollback", false, 0 });
        }};
        return commandList;
    }

    public FileMapProvider(String pathDb) throws Exception {
        this.out = true;
        this.err = true;

        this.useNameTable = "";
        this.pathDb = Paths.get(pathDb);
        this.mySystem = new CommandShell(pathDb, false, false);
        this.dbData = null;
        this.setDirTable = new HashSet<>();
        this.mapFileMap = new HashMap<>();

        try {
            checkBdDir(this.pathDb);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Error loading base"));
            throw e;
        }
    }

    public void exit() throws Exception {
        if (dbData != null) {
            dbData.closeTable();
        }
    }

    private void checkBdDir(Path pathTables) throws Exception {
        File currentFile = pathTables.toFile();
        File[] listFiles = currentFile.listFiles();
        for (File nameMap : listFiles) {
            if (!nameMap.isDirectory()) {
                throw new ErrorFileMap(nameMap.getAbsolutePath() + " isn't directory");
            } else {
                setDirTable.add(nameMap.getName());
            }
        }
    }

    private FileMap loadDb(String nameMap) throws Exception {
        try {
            File currentFileMap = pathDb.resolve(nameMap).toFile();
            if (!currentFileMap.isDirectory()) {
                throw new ErrorFileMap(currentFileMap.getAbsolutePath() + " isn't directory");
            }
            FileMap table = new FileMap(pathDb, nameMap, this);
            return table;
        } catch (Exception e) {
            //e.printStackTrace();
            e.addSuppressed(new ErrorFileMap("Error opening a table " + nameMap));
            throw e;
            //return null;
        }
    }

    public String startShellString() {
        return "$ ";
    }

    public void multiPut(String[] args) throws ParseException {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            args = myParsing(args);
            String key = args[0];
            String value = args[1];
            Storeable res = dbData.put(key, deserialize(dbData, value));
            if (res == null) {
                outPrint("new");
            } else {
                outPrint("overwrite");
                outPrint(serialize(dbData, res));
            }
        }
    }

    public void multiGet(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            String key = args[0];
            Storeable res = dbData.get(key);
            if (res == null) {
                outPrint("not found");
            } else {
                //System.err.println("found\r\n" + serialize(dbData, res));
                //outPrint("found\r\n" + serialize(dbData, res));
                //System.out.flush();
                String s = serialize(dbData, res);
                outPrint("found");
                //outPrint("\n");
                //System.out.flush();
                outPrint(s);
                //System.out.flush();
            }
        }
    }

    public void multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {

            String key = args[0];
            Storeable res = dbData.remove(key);
            if (res == null) {
                outPrint("not found");
            } else {
                outPrint("removed");
            }
        }
    }

    public void multiSize(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            outPrint(String.valueOf(dbData.size()));
        }
    }

    public void multiCommit(String[] args) {
        outPrint(String.valueOf(dbData.commit()));
    }

    public void multiRollback(String[] args) {
        outPrint(String.valueOf(dbData.rollback()));
    }

    public void multiUse(String[] args) throws Exception {
        int changeKey = 0;
        if (dbData != null) {
            changeKey = dbData.changeKey();
        }

        if (changeKey > 0) {
            errPrint(String.format("%d unsaved changes", changeKey));
        } else {
            String nameTable = args[0];
            if (!setDirTable.contains(nameTable)) {
                outPrint(nameTable + " not exists");
            } else {
                if (!nameTable.equals(useNameTable)) {
                    if (dbData != null) {
                        dbData.closeTable();
                    }
                    dbData = loadDb(nameTable);
                    useNameTable = nameTable;
                }
                outPrint("using " + nameTable);
            }
        }
    }

    public void multiDrop(String[] args) throws ErrorFileMap {
        String nameTable = args[0];
        try {
            if (nameTable.equals(useNameTable)) {
                useNameTable = "";
            }
            removeTable(nameTable);
            outPrint("dropped");
        } catch (IllegalStateException e) {
            outPrint(nameTable + " not exists");
        }
    }

    private List<String> parsingForCreate(String[] args) throws IllegalArgumentException {
        String query = args[0];
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();
        if (countTokens < 3) {
            throw new IllegalArgumentException("A few argument");
        }

        List<String> res = new ArrayList<>();
        res.add(token.nextToken());
        res.add(token.nextToken());

        for (int i = 2; i < countTokens; ++i) {
            String t = token.nextToken();
            if (i == 2 && t.trim().charAt(0) != '(') {
                throw new IllegalArgumentException("wrong type ( )");
            }
            if (i == countTokens - 1 && t.trim().charAt(t.trim().length() - 1) != ')') {
                throw new IllegalArgumentException("wrong type ( )");
            }
            if (t.charAt(0) == '(') {
                t = t.substring(1);
            }
            if (t.isEmpty()) {
                continue;
            }
            if (t.charAt(t.length() - 1) == ')') {
                if (t.length() - 1 > 0) {
                    t = t.substring(0, t.length() - 1);
                }
            }
            if (!t.trim().isEmpty() && !t.contains(")") && !t.contains("(")) {
                res.add(t.trim());
            }
        }
        return res;
    }

    public void multiCreate(String[] args) throws Exception {
        List<String> argsParse = parsingForCreate(args);
        String nameTable = argsParse.get(1);
        List<Class<?>> colType = new ArrayList<>();

        for (int i = 2; i < argsParse.size(); ++i) {
            Class<?> type = convertStringToClass(argsParse.get(i));
            if (type == null) {
                throw new IllegalArgumentException(String.format("wrong type (%s)", argsParse.get(i)));
            }
            colType.add(type);
        }
        Table fileMap = createTable(nameTable, colType);

        if (fileMap == null) {
            outPrint(nameTable + " exists");
            throw new ErrorFileMap(null);
        } else {
            outPrint("created");
        }
    }

    public Table createTable(String name, List<Class<?>> columnType) throws IOException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (antiCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        if (columnType == null || columnType.size() == 0) {
            throw new IllegalArgumentException("wrong type ( )");
        }
        for (Class<?> col : columnType) {
            if (col == null || !allowType.contains(col)) {
                throw new IllegalArgumentException("name is clear");
            }
        }
        if (setDirTable.contains(name)) {
            return null;
        } else {
            setDirTable.add(name);
            try {
                FileMap fileMap = new FileMap(pathDb, name, this, columnType);
                mapFileMap.put(name, fileMap);
                return fileMap;
            } catch (Exception e) {
                RuntimeException error = new RuntimeException();
                error.addSuppressed(e);
                throw error;
            }

        }
    }

    private Boolean antiCorrectDir(String dir) {
        return dir.contains("/") || dir.contains(":") || dir.contains("*")
               || dir.contains("?") || dir.contains("\"") || dir.contains("\\")
               || dir.contains(">") || dir.contains("<") || dir.contains("|");
    }

    public Table getTable(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (antiCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        if (mapFileMap.containsKey(name)) {
            return mapFileMap.get(name);
        }
        if (setDirTable.contains(name)) {
            try {
                FileMap fileMap = new FileMap(pathDb, name, this);
                return fileMap;
            } catch (Exception e) {
                RuntimeException error = new RuntimeException();
                error.addSuppressed(e);
                throw error;
            }
        } else {
            return null;
        }
    }

    public void removeTable(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (setDirTable.contains(name)) {
            setDirTable.remove(name);
            mapFileMap.remove(name);
            dbData.setDrop();
            try {
                if (!dbData.isDrop()) {
                    mySystem.rm(new String[]{pathDb.resolve(name).toString()});
                }
            } catch (Exception e) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.addSuppressed(e);
                throw ex;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            List<Class<?>> columnTypes = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                columnTypes.add(table.getColumnType(i));
            }

            XMLInputFactory inputFactory = XMLInputFactory.newFactory();
            Storeable line = new FileMapStoreable(columnTypes);
            try {
                XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(value));
                try {
                    if (!reader.hasNext()) {
                        throw new ParseException("input string is empty", 0);
                    }
                    reader.nextTag();
                    if (!reader.getLocalName().equals("row")) {
                        throw new ParseException("invalid xml format", reader.getLocation().getCharacterOffset());
                    }
                    int columnIndex = 0;
                    while (reader.hasNext()) {
                        reader.nextTag();
                        if (!reader.getLocalName().equals("col")) {
                            //System.out.println(reader.getLocalName());
                            if (!reader.getLocalName().equals("null") && !reader.isEndElement()) {
                                throw new ParseException("invalid xml", reader.getLocation().getCharacterOffset());
                            }
                            if (reader.isEndElement() && reader.getLocalName().equals("row")) {
                                break;
                            }
                        }
                        String text = reader.getElementText();
                        if (text.isEmpty()) {
                            line.setColumnAt(columnIndex, null);
                        } else {
                            //System.out.println("123" + text);
                            if (!text.equals("null")) {
                                line.setColumnAt(columnIndex, parseValue(text, columnTypes.get(columnIndex)));
                            }
                        }
                        columnIndex++;
                    }
                } finally {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                throw new ParseException("xml reading error", 0);
            } catch (ColumnFormatException e) {
                throw new ParseException("xml reading error", 0);
            }
            return line;

        } catch (Exception e) {
            //e.printStackTrace();
            throw new ParseException("wrong data format", 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        List<Class<?>> columnType = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnType.add(table.getColumnType(i));
        }

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        StringWriter stringWriter = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);
            try {
                writer.writeStartElement("row");
                for (int i = 0; i < columnType.size(); i++) {
                    if (value.getColumnAt(i) == null) {
                        writer.writeEmptyElement("null");
                    } else {
                        writer.writeStartElement("col");
                        writer.writeCharacters(getStringFromElement(value, i, columnType.get(i)));
                        writer.writeEndElement();
                    }
                }
                writer.writeEndElement();
            } finally {
                writer.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.closeStream(stringWriter);
        }
        return stringWriter.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> colType = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            colType.add(table.getColumnType(i));
        }
        return new FileMapStoreable(colType);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        Storeable storeable = new FileMapStoreable(columnTypes);
        int columnIndex = 0;
        for (Object value : values) {
            storeable.setColumnAt(columnIndex, value);
        }
        return storeable;
    }

    private void errPrint(String message) {
        if (err) {
            System.err.flush();
            System.err.println(message);
            System.err.flush();
        }
    }

    private void outPrint(String message) {
        if (out) {
            //System.out.flush();
            System.out.println(message);
            //System.out.flush();
        }
    }

}
