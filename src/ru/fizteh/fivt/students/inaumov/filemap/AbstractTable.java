package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractTable implements Table {
	protected HashMap<String, String> tableHash = new HashMap<String, String>();
	protected HashMap<String, String> modifiedTableHash = new HashMap<String, String>();
	protected HashSet<String> deleted = new HashSet<String>();
	
	private String tableName;
	private String dir;
	
	private int tableSize = 0;
	private int unsavedChangesNumber = 0;
	
	public WriteHandler writeHandler;
	public ReadHandler readHandler;
	
	private class WriteHandler implements Closeable {
		
		private RandomAccessFile outputFile = null;
		
		public WriteHandler(String fileName) throws IOException {
			try {
				outputFile = new RandomAccessFile(fileName, "rw");
			} catch (FileNotFoundException exception) {
			    throw new IOException("can't create file " + fileName);
			}
			outputFile.setLength(0);
		}
		
		public void writeEntry(String key, String value) throws IOException {
			outputFile.writeInt(key.length());
			outputFile.write(key.getBytes("UTF-8"));
			
			outputFile.writeInt(value.length());
			outputFile.write(value.getBytes("UTF-8"));
		}
		
		public void close() throws IOException {
			outputFile.close();
		}
		
	}
	private class ReadHandler implements Closeable {
		
		private RandomAccessFile inputFile = null;
		
		public ReadHandler(String fileName) throws IOException {
			try {
				inputFile = new RandomAccessFile(fileName, "r");
			} catch (FileNotFoundException exception) {
				
			}
		}
		
		private int readInteger() throws IOException {
			int result = inputFile.readInt();
			
			return result;
		}
		
		private String readString(int stringLength) throws IOException {
			byte[] stringBytes = new byte[stringLength];
			inputFile.read(stringBytes);
			
			return new String(stringBytes, "UTF-8");
		}

		public String readEntry() throws IOException {
			int stringLength = readInteger();
			//System.out.println("::readEntry(): length = " + stringLength);
			String entry = readString(stringLength);
			//System.out.println("::readEntry(): entry = " + entry);
			return entry;
		}
		
		public boolean readEnd() throws IOException {
			if (inputFile == null) {
				//System.err.println("AbstractTable::readEnd(): inputFile == null");
				return true;
			}
			
			if (inputFile.getFilePointer() <= inputFile.length() - 1) {
				//System.out.println("AbstractTable::readEnd(): fileptr = " + inputFile.getFilePointer());
				//System.out.println("AbstractTable::readEnd(): filelength = " + inputFile.length());
				return false;
			}
			
			return true;
		}
		
		public void close() throws IOException {
			inputFile.close();
		}
		
	}
	
	public abstract void loadTable() throws IOException;
	public abstract void saveTable() throws IOException;
	
	public AbstractTable(String dir, String tableName) throws IOException, IllegalArgumentException {
		if (dir == null) {
			throw new IllegalArgumentException("directory can't be null");
		}
		if (tableName == null) {
			throw new IllegalArgumentException("table name can't be null");
		}
		
		this.tableName = tableName;
		this.dir = dir;
		
		loadTable();
	}
	
	public String getName() {
		return tableName;
	}

	public String getDir() {
		return dir;
	}
	
	public String get(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		
		if (modifiedTableHash.containsKey(key)) {
			return modifiedTableHash.get(key);
		}
		if (tableHash.containsKey(key) && !deleted.contains(key)) {
			return tableHash.get(key);
		}

		return null;
	}

	public String put(String key, String value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		if (value == null) {
			throw new IllegalArgumentException("value can't be null");
		}
		
		if (!modifiedTableHash.containsKey(key) && !tableHash.containsKey(key)
			|| tableHash.containsKey(key) && deleted.contains(key)) {
			tableSize += 1;
		}

		String oldValue = getOldValue(key);
        modifiedTableHash.put(key, value);
		unsavedChangesNumber += 1;
		
		return oldValue;
	}

	public String remove(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		
		String oldValue = null;
		if (modifiedTableHash.containsKey(key)) {
			oldValue = modifiedTableHash.get(key);
			modifiedTableHash.remove(key);
			unsavedChangesNumber += 1;
			tableSize -= 1;
			
			return oldValue;
		}
		if (tableHash.containsKey(key) && !deleted.contains(key)) {
			oldValue = tableHash.get(key);
			deleted.add(key);
			unsavedChangesNumber += 1;
			tableSize -= 1;
			
			return oldValue;
		}
		
		return oldValue;
	}

	public int size() {
		return tableSize;
	}

	public int getUnsavedChangesNumber() {
		return unsavedChangesNumber;
	}

    private String getOldValue(String key) {
        String oldValue = modifiedTableHash.get(key);
        if (oldValue == null && !deleted.contains(key)) {
            oldValue =  tableHash.get(key);
        }

        return oldValue;
    }

	public int commit() {
		for (Map.Entry<String, String> nextEntry: modifiedTableHash.entrySet()) {
			tableHash.put(nextEntry.getKey(), nextEntry.getValue());
		}
		for (String nextEntry: deleted) {
			tableHash.remove(nextEntry);
		}
		modifiedTableHash.clear();
		deleted.clear();
		
		try {
			saveTable();
		} catch (IOException exception) {
			System.err.println(exception.getMessage());
			return 0;
		}
		
		int savedChangesNumber = unsavedChangesNumber;
		unsavedChangesNumber = 0;
		
		return savedChangesNumber;
	}

	public int rollback() {
		modifiedTableHash.clear();

		tableSize += deleted.size();
		
		deleted.clear();
		
		int rollbackedChangesNumber = unsavedChangesNumber;
		unsavedChangesNumber = 0;
		
		return rollbackedChangesNumber;
	}
	
	protected void loadFromFile(String fileName) throws IOException {
		//System.out.println("AbstractTable::loadFromFile: looking for file " + fileName + "...");
		File file = new File(fileName);
		if (!file.exists()) {
			//System.out.println("AbstractTable::loadFromFile: file " + fileName + " doesn't exist");
			return;
		}
		
		//System.out.println("AbstractTable::loadFromFile: file " + fileName + " found");
		//System.out.println("AbstractTable::loadFromFile: loading " + fileName + "...");
		
		ReadHandler readHandler = new ReadHandler(fileName);
		while (!readHandler.readEnd()) {
			String key = readHandler.readEntry();
			String value = readHandler.readEntry();
			tableHash.put(key, value);
			//System.out.println("AbstractTable::loadFromFile: loaded: (" + key + ", " + value + ")");
		}
		
		//System.out.println("AbstractTable::loadFromFile: loading complete");
		
		tableSize = tableHash.size();
		readHandler.close();
	}
	
	protected void saveToFile(String fileName) throws IOException {
		//System.out.println("AbstractTable::saveToFile: saving " + fileName + "...");
		
		WriteHandler writeHandler = new WriteHandler(fileName);
		for (Map.Entry<String, String> nextEntry: tableHash.entrySet()) {
			writeHandler.writeEntry(nextEntry.getKey(), nextEntry.getValue());
			//System.out.println("AbstractTable::saveToFile: saved: (" + nextEntry.getKey() + ", " + nextEntry.getValue() + ")");
		}
		
		//System.out.println("AbstractTable::saveToFile: saving complete");
		writeHandler.close();
	}
}