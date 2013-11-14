package ru.fizteh.fivt.students.dzvonarev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class MyTable implements Table {

    public class ValueNode {
        Storeable oldValue;
        Storeable newValue;
    }

    public MyTable(File dirTable, MyTableProvider currentProvider) throws IOException, RuntimeException {
        tableProvider = currentProvider;
        tableFile = dirTable;
        tableName = dirTable.getName();
        fileMap = new HashMap<>();
        changesMap = new HashMap<>();
        type = new ArrayList<>();
        List<String> temp = new ArrayList<>();  //init type of table
        readTypes(temp);
        Parser myParser = new Parser();
        try {
            type = myParser.parseTypeList(temp);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    private String tableName;                      // name of current table
    private File tableFile;
    private MyTableProvider tableProvider;
    private HashMap<String, Storeable> fileMap;
    private HashMap<String, ValueNode> changesMap;
    private List<Class<?>> type;              // types in this table

    public List<Class<?>> getTypeArray() {
        return type;
    }

    public int getCountOfChanges() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.isEmpty()) {
            return 0;
        }
        Set<Map.Entry<String, ValueNode>> fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        int counter = 0;
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (!equals(value.newValue, value.oldValue)) {
                ++counter;
            }
        }
        return counter;
    }

    public boolean equals(Storeable st1, Storeable st2) throws IndexOutOfBoundsException {
        for (int i = 0; i < getColumnsCount(); ++i) {
            if (st1 == null || st2 == null) {
                if (st1 == null && st2 == null) {
                    continue;
                }
                return false;
            }
            if (st1.getColumnAt(i) == null && st2.getColumnAt(i) == null) {
                continue;
            }
            if (st1.getColumnAt(i) == null && st2.getColumnAt(i) != null) {
                return false;
            }
            if (!st1.getColumnAt(i).equals(st2.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void readTypes(List<String> arr) throws RuntimeException, IOException {
        File signature = new File(tableFile.getAbsolutePath() + File.separator + "signature.tsv");
        if (!signature.exists()) {
            throw new RuntimeException("signature.tsv not existing");
        }
        RandomAccessFile sigFile = openFileForRead(tableFile.getAbsolutePath() + File.separator + "signature.tsv");
        if (sigFile.length() == 0) {
            closeFile(sigFile);
            throw new RuntimeException("signature.tsv is empty");
        }
        closeFile(sigFile);
        try (Scanner formatScanner = new Scanner(signature)) {
            while (formatScanner.hasNextLine()) {
                arr.add(formatScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("signature file wasn't found");
        }
    }

    public void readFileMap() throws RuntimeException, IOException, ParseException {
        List<String> typeNames = new ArrayList<>();
        readTypes(typeNames);        // for storeable
        Parser myParser = new Parser();
        type = myParser.parseTypeList(typeNames);
        String[] dbDirs = tableFile.list();
        if (dbDirs != null && dbDirs.length != 0) {
            for (String dbDir : dbDirs) {
                if (dbDir.equals("signature.tsv")) {
                    continue;
                }
                if (!isValidDir(dbDir)) {
                    throw new RuntimeException("directory " + dbDir + " is not valid");
                }
                File dbDirTable = new File(tableFile.getAbsolutePath(), dbDir);
                String[] dbDats = dbDirTable.list();
                if (dbDats == null || dbDats.length == 0) {
                    throw new RuntimeException("table " + getName() + " is not valid: directory " + dbDirTable + " is empty");
                }
                for (String dbDat : dbDats) {
                    String str = tableFile.getAbsolutePath() + File.separator + dbDir + File.separator + dbDat;
                    readMyFileMap(str, dbDir, dbDat);
                }
            }
        }
    }

    /* READING FILEMAP */
    public void readMyFileMap(String fileName, String dir, String file) throws IOException, RuntimeException, ParseException {
        RandomAccessFile fileReader = null;
        try {
            fileReader = openFileForRead(fileName);
            long endOfFile = fileReader.length();
            long currFilePosition = fileReader.getFilePointer();
            if (endOfFile == 0) {
                closeFile(fileReader);
                throw new RuntimeException("reading directory: " + dir + " is not valid");
            }
            while (currFilePosition != endOfFile) {
                int keyLen = fileReader.readInt();
                int valueLen = fileReader.readInt();
                if (keyLen <= 0 || valueLen <= 0) {
                    closeFile(fileReader);
                    throw new RuntimeException(fileName + " : file is broken");
                }
                byte[] keyByte;
                byte[] valueByte;
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
                try {
                    fileReader.readFully(keyByte, 0, keyLen);
                    fileReader.readFully(valueByte, 0, valueLen);
                } catch (OutOfMemoryError e) {
                    throw new RuntimeException(e.getMessage() + " " + fileName + " : file is broken", e);
                }
                String key = new String(keyByte);
                String value = new String(valueByte);
                if (!keyIsValid(key, dir, file)) {
                    closeFile(fileReader);
                    throw new RuntimeException("file " + file + " in " + dir + " is not valid");
                }
                Storeable storeable = tableProvider.deserialize(this, value);
                fileMap.put(key, storeable);
                currFilePosition = fileReader.getFilePointer();
                endOfFile = fileReader.length();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " " + fileName + " : file is broken", e);
        } finally {
            closeFile(fileReader);
        }
    }

    public boolean keyIsValid(String key, String dir, String file) {
        int b = key.getBytes()[0];
        int nDirectory = Math.abs(b) % 16;
        int nFile = Math.abs(b) / 16 % 16;
        String rightDir = nDirectory + ".dir";
        String rightFile = nFile + ".dat";
        return (dir.equals(rightDir) && file.equals(rightFile));
    }

    public boolean isFilesInDirValid(File file) {
        String[] files = file.list();
        if (files == null || files.length == 0) {
            return true;
        }
        for (String currFile : files) {
            if (new File(file.toString(), currFile).isDirectory()) {
                return false;
            }
            if (!currFile.matches("[0-9][.]dat|1[0-5][.]dat")) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidDir(String path) {
        File dir = new File(path);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            File newCurrFile = new File(path, currFile);
            if (newCurrFile.isDirectory() && currFile.matches("[0-9][.]dir|1[0-5][.]dir")) {
                if (!isFilesInDirValid(newCurrFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isValid(Storeable value) throws IndexOutOfBoundsException {
        for (int i = 0; i < getColumnsCount(); ++i) {
            if (value.getColumnAt(i) == null) {
                continue;
            }
            if (value.getColumnAt(i).getClass() != type.get(i)) {
                return false;
            }
        }
        return true;
    }

    public void writeInTable() throws IOException {
        if (fileMap == null) {
            return;
        } else {
            if (fileMap.isEmpty()) {
                return;
            }
        }
        Set<Map.Entry<String, Storeable>> fileSet = fileMap.entrySet();
        Iterator<Map.Entry<String, Storeable>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, Storeable> currItem = i.next();
            String key = currItem.getKey();
            Storeable value = currItem.getValue();
            int b = key.getBytes()[0];
            int nDirectory = Math.abs(b) % 16;
            int nFile = Math.abs(b) / 16 % 16;
            String rightDir = nDirectory + ".dir";
            String rightFile = nFile + ".dat";
            String path = tableFile.getAbsolutePath() + File.separator + rightDir + File.separator + rightFile;
            String dir = tableFile.getAbsolutePath() + File.separator + rightDir;
            File file = new File(path);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("can't create directory " + dir);
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("can't create file " + path);
                }
            }
            writeInFile(path, key, value);
        }
    }

    public void writeInFile(String path, String key, Storeable value) throws IOException {
        RandomAccessFile fileWriter = null;
        try {
            fileWriter = openFileForWrite(path);
            fileWriter.skipBytes((int) fileWriter.length());
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = tableProvider.serialize(this, value).getBytes(StandardCharsets.UTF_8);
            fileWriter.writeInt(keyBytes.length);
            fileWriter.writeInt(valueBytes.length);
            fileWriter.write(keyBytes);
            fileWriter.write(valueBytes);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + " updating file " + path + " : error in writing", e);
        } finally {
            closeFile(fileWriter);
        }
    }

    public RandomAccessFile openFileForRead(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage() + " reading from file: file " + fileName + " not found", e);
        }
        return newFile;
    }

    public RandomAccessFile openFileForWrite(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage() + " writing to file: file " + fileName + " not found", e);
        }
        return newFile;
    }

    public void closeFile(RandomAccessFile file) throws IOException {
        try {
            file.close();
        } catch (NullPointerException | IOException e) {
            throw new IOException(e.getMessage() + " error in closing file", e);
        }
    }

    @Override
    public String getName() {
        return tableName.substring(tableName.lastIndexOf(File.separator) + 1, tableName.length());
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null || key.trim().isEmpty() || key.contains("\\s+")) {
            throw new IllegalArgumentException("get: wrong key");
        }
        if (changesMap.containsKey(key)) {            // если он был изменен
            return changesMap.get(key).newValue;
        } else {
            if (fileMap.containsKey(key)) {
                return fileMap.get(key);
            } else {
                return null;
            }
        }
    }

    public void addChanges(String key, Storeable value) {
        if (changesMap.containsKey(key)) {
            changesMap.get(key).newValue = value;
        } else {
            ValueNode valueNode = new ValueNode();
            valueNode.oldValue = fileMap.get(key);
            valueNode.newValue = value;
            changesMap.put(key, valueNode);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (key == null || key.trim().isEmpty() || key.contains("\\s+") || value == null) {
            throw new IllegalArgumentException("put: wrong key or value");
        }
        if (!isValid(value)) {
            throw new ColumnFormatException("invalid storeable");
        }
        Storeable oldValue = get(key);
        addChanges(key, value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null || key.trim().isEmpty() || key.contains("\\s+")) {
            throw new IllegalArgumentException("remove: wrong key");
        }
        Storeable oldValue = get(key);
        if (oldValue != null) {
            addChanges(key, null);
        }
        return oldValue;
    }

    @Override
    public int size() throws IndexOutOfBoundsException {
        return countSize() + fileMap.size();
    }

    @Override
    public int commit() throws IndexOutOfBoundsException {
        modifyFileMap();
        int count = getCountOfChanges();
        changesMap.clear();
        return count;
    }


    public void modifyFileMap() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, ValueNode>> fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (!equals(value.newValue, value.oldValue)) {
                if (value.newValue == null) {
                    fileMap.remove(currItem.getKey());
                } else {
                    fileMap.put(currItem.getKey(), value.newValue);
                }
            }
        }
    }

    public int countSize() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.isEmpty()) {
            return 0;
        }
        int size = 0;
        Set<Map.Entry<String, ValueNode>> fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (value.oldValue == null && value.newValue != null) {
                ++size;
            }
            if (value.oldValue != null && value.newValue == null) {
                --size;
            }
        }
        return size;
    }

    @Override
    public int rollback() throws IndexOutOfBoundsException {
        int count = getCountOfChanges();
        changesMap.clear();
        return count;
    }

    @Override
    public int getColumnsCount() {
        return type.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("wrong column index " + columnIndex);
        }
        return type.get(columnIndex);
    }


}
