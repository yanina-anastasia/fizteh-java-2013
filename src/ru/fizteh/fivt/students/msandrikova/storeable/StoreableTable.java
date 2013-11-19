package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTable implements ChangesCountingTable {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private TableProvider tableProvider;
    private String name;
    private File tablePath;
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
    private Map<String, Storeable> originalDatabase = new HashMap<String, Storeable>();
    
    private static final int MAX_DIRECTORIES_AMOUNT = 16;
    private static final int MAX_DATABASES_IN_DIRECTORY_AMOUNT = 16;
    private static final int MAX_TABLE_SIZE = 1000 * 1000 * 100;
    private static final Pattern P = Pattern.compile("\\s+");
    public class TransactionHolder {
        private Map<String, Storeable> newDatabase;
        
        public TransactionHolder() {
            this.newDatabase = new HashMap<String, Storeable>(StoreableTable.this.originalDatabase);
        }
        
        private void writeInFile(File currentFile, Set<String> keys) throws IOException {
            if (!currentFile.createNewFile()) {
                throw new IOException("Can not create file '" + currentFile.getName() + "'.");
            }
            DataOutputStream writer = null;
            try {
                writer = new DataOutputStream(new FileOutputStream(currentFile));
                String value;
                for (String key : keys) {
                    value = tableProvider.serialize(StoreableTable.this, this.newDatabase.get(key));
                    byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                    writer.writeInt(keyBytes.length);
                    writer.writeInt(valueBytes.length);
                    writer.write(keyBytes, 0, keyBytes.length);
                    writer.write(valueBytes, 0, valueBytes.length);
                }
            } finally {
                writer.close();
            }
        }
        
        private void writeSignature() throws IOException {
            File signature = new File(StoreableTable.this.tablePath, "signature.tsv");
            if (signature.exists()) {
                return;
            } 
            if (!signature.createNewFile()) {
                throw new IOException("Can not create 'signature.tsv' file.");
            }
            List<String> columnTypesNames = Utils.getColumnTypesNames(StoreableTable.this.columnTypes);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(signature));
                for (int i = 0; i < StoreableTable.this.getColumnsCount(); ++i) {
                    writer.write(columnTypesNames.get(i));
                    if (i != StoreableTable.this.getColumnsCount() - 1) {
                        writer.write(" ");
                    }
                }
            } finally {
                writer.close();
            }
        }
        
        private void write() throws IOException {
            this.writeSignature();
            File directory;
            for (int i = 0; i < MAX_DIRECTORIES_AMOUNT; ++i) {
                directory = new File(tablePath, i + ".dir");
                if (!directory.exists()) {
                    continue;
                }
                try {
                    Utils.remover(directory, "commit", false);
                } catch (IOException e) {
                    throw new IOException("Can not clean table direcory.");
                }
            }
            Map<Integer, Map<Integer, Set<String>>> 
                keysDueTheirHash = new HashMap<Integer,Map<Integer, Set<String>>>();
            for (int i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                keysDueTheirHash.put(i, new HashMap<Integer, Set<String>>());
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    keysDueTheirHash.get(i).put(j, new HashSet<String>());
                }
            }
            int dir;
            int dat;
            for (String key : this.newDatabase.keySet()) {
                dir = Utils.getNDirectory(key);
                dat = Utils.getNFile(key);
                keysDueTheirHash.get(dir).get(dat).add(key);
            }
            File currentFile;
            for (int i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                directory = new File(StoreableTable.this.tablePath, i + ".dir");
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    if (keysDueTheirHash.get(i).get(j).size() == 0) {
                        continue;
                    }
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            throw new IOException("Can not create directory '" 
                                    + i + ".dir'.");
                        }
                    }
                    currentFile = new File(directory, j + ".dat");
                    this.writeInFile(currentFile, keysDueTheirHash.get(i).get(j));
                }
            }
        }
        
        private boolean checkColumnTypes(Storeable value) {
            for (int i = 0; i < StoreableTable.this.getColumnsCount(); ++i) {
                try {
                    switch(StoreableTable.this.columnTypes.get(i).getName()) {
                    case "java.lang.Integer":
                        value.getIntAt(i);
                        break;
                    case "java.lang.Byte":
                        value.getByteAt(i);
                        break;
                    case "java.lang.Long":
                        value.getLongAt(i);
                        break;
                    case "java.lang.Float":
                        value.getFloatAt(i);
                        break;
                    case "java.lang.Boolean":
                        value.getBooleanAt(i);
                        break;
                    case "java.lang.String":
                        value.getStringAt(i);
                        break;
                    case "java.lang.Double":
                        value.getDoubleAt(i);
                        break;
                    }
                } catch (ColumnFormatException| IndexOutOfBoundsException e) {
                    return false;
                }
            }
            try {
                value.getColumnAt(StoreableTable.this.getColumnsCount());
            } catch (IndexOutOfBoundsException e) {
                return true;
            }
            return false;
        }
        
        public String getName() throws IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            return StoreableTable.this.name;
        }
        
        public Storeable get(String key) throws IllegalArgumentException, IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException("Key can not be null");
            }
            return this.newDatabase.get(key);
        }
            
        public Storeable put(String key, Storeable value) 
                throws IllegalArgumentException, IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            if (Utils.isEmpty(key) || value == null || P.matcher(key).find()) {
                throw new IllegalArgumentException("Key and name can not be null or "
                        + "newline and key can not contain whitespace");
            }
            if (!this.checkColumnTypes(value)) {
                throw new ColumnFormatException("Incorrent column types in given storeable.");
            }
            return this.newDatabase.put(key, value);
        }
        
        public Storeable remove(String key) throws IllegalArgumentException, IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException("Key can not be null");
            }
            return this.newDatabase.remove(key);
        }
        
        public int size() throws IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            return this.newDatabase.size();
        }
        
        public int commit() throws IOException, IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            int changesCount = this.unsavedChangesCount();
            this.write();
            StoreableTable.this.originalDatabase.clear();
            StoreableTable.this.originalDatabase.putAll(this.newDatabase);
            return changesCount;
        }
        
        public int rollback() throws IllegalStateException {
            if (StoreableTable.this.tableProvider.getTable(StoreableTable.this.name) == null) {
                throw new IllegalStateException("Table was removed.");
            }
            int changesCount = this.unsavedChangesCount();
            this.newDatabase.clear();
            this.newDatabase.putAll(StoreableTable.this.originalDatabase);
            return changesCount;
        }
        
        public int unsavedChangesCount() {
        int changesCount  = 0;
        int intersectionSize = 0;
        Storeable originalValue = null;
        Storeable newValue = null;
        for (String key : StoreableTable.this.originalDatabase.keySet()) {
            originalValue = StoreableTable.this.originalDatabase.get(key);
            newValue = this.newDatabase.get(key);
            if (newValue != null) {
                ++intersectionSize;
                if (!newValue.equals(originalValue)) {
                    ++changesCount;
                }
            }
        }
        changesCount += StoreableTable.this.originalDatabase.size() + this.newDatabase.size() 
                - 2 * intersectionSize;
        return changesCount;
    }
    }
    
    private ThreadLocal<TransactionHolder> transaction;
        
    
    
    private boolean checkHash(int directory, int database, String key) {
        if (directory != Utils.getNDirectory(key) || database != Utils.getNFile(key)) {
            return false;
        }
        return true;
    }
    
    private void readFile(int directory, int database) throws IOException {
        File currentFile = new File(this.tablePath, directory + ".dir");
        if (!currentFile.isDirectory()) {
            throw new IOException("File '" + directory + ".dir' must be directory.");
        }
        currentFile = new File(currentFile, database + ".dat");
        if (!currentFile.isFile()) {
            throw new IOException("File '" + database + ".dat' in directory '" 
                    + directory + ".dir' can not be be directory.");
        }
        
        int keyLength;
        int valueLength;
        String key;
        String value;
        DataInputStream reader = null;
        int count = 0;
        try {
            reader = new DataInputStream(new FileInputStream(currentFile));
            while (true) {
                try {
                    keyLength = reader.readInt();
                } catch (EOFException e) {
                    if (count == 0) {
                        throw new IOException("empty dat");
                    }
                    break;
                }
                    ++count;
                    if (keyLength <= 0 || keyLength >= 1000 * 1000) {
                        throw new IOException("reader: Invalid key length.");
                    }
                    
                    valueLength = reader.readInt();
                    if (valueLength <= 0 || valueLength >= 1000 * 1000) {
                        throw new IOException("reader: Invalid value length.");
                    }
                    
                    byte[] keyByteArray = new byte[keyLength];
                    reader.read(keyByteArray, 0, keyLength);
                    key = new String(keyByteArray);
                    
                    byte[] valueByteArray = new byte[valueLength];
                    reader.read(valueByteArray, 0, valueLength);
                    value = new String(valueByteArray);
                    
                    if (!this.checkHash(directory, database, key)) {
                        throw new IOException("Key " + key + " can not be in '" 
                                + directory + ".dir/" + database + ".dat'.");
                    }
                    
                    try {
                        this.originalDatabase.put(key, this.tableProvider.deserialize(this, value));
                    } catch (ParseException e) {
                        throw new IOException(e.getMessage());
                    }
                    
                    if (this.originalDatabase.size() >= StoreableTable.MAX_TABLE_SIZE) {
                        throw new IOException("Table '" + this.name + "' is overly big.");
                    }
                
            }
        } finally {
            reader.close();
        }
        
    }
    
    public StoreableTable(File dir, String name, List<Class<?>> columnTypes, 
            TableProvider tableProvider) throws IOException {
        this.name = name;
        this.tableProvider = tableProvider;
        this.columnTypes = columnTypes;
        this.tablePath = new File(dir, name);
        if (!this.tablePath.exists()) {
            if (!this.tablePath.mkdir()) {
                throw new IOException("Can not create directory for table " + this.name);
            }
        } else {
            int i;
            for (i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                File directory = new File(this.tablePath, i + ".dir");
                if (!directory.exists()) {
                    continue;
                }
                int count = 0;
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    File currentFile = new File(directory, j + ".dat");
                    if (!currentFile.exists()) {
                        continue;
                    }
                    ++count;
                    this.readFile(i, j);
                }
                if (count == 0) {
                    throw new IOException("empty dir");
                }
            }
        }
        this.transaction = new ThreadLocal<TransactionHolder>() {
            @Override
            protected TransactionHolder initialValue() {
                return new TransactionHolder();
            }
        };
    }

    @Override
    public String getName() throws IllegalStateException { 
        lock.readLock().lock();
        String answer = null;
        try {
            answer = this.transaction.get().getName();
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().get(key);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().put(key, value);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().remove(key);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int size() throws IllegalStateException {
        lock.readLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().size();
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int commit() throws IOException, IllegalStateException {
        lock.writeLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().commit();
        } catch (IOException | IllegalStateException e) {
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
        return answer;
    }

    @Override
    public int rollback() throws IllegalStateException {
        lock.readLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().rollback();
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int getColumnsCount() {
        lock.readLock().lock();
        int answer = 0;
        answer = this.columnTypes.size();
        lock.readLock().unlock();
        return answer;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        lock.readLock().lock();
        Class<?> answer = null;
        
        if (columnIndex < 0 || columnIndex >= this.getColumnsCount()) {
            lock.readLock().unlock();
            throw new IndexOutOfBoundsException("Column index can not be less then "
                    + "0 and more then types amount.");
        }
        answer = this.columnTypes.get(columnIndex);
        lock.readLock().unlock();
        return answer;
    }

    @Override
    public int unsavedChangesCount() {
        return this.transaction.get().unsavedChangesCount();
    }
}
