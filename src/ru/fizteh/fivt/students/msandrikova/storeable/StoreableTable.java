package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTable implements Table {
	private TableProvider tableProvider;
	private String name;
	private File tablePath;
	private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
	private Map<String, Storeable> originalDatabase = new HashMap<String, Storeable>();
	private Map<String, Storeable> newDatabase = new HashMap<String, Storeable>();
	private final int MAX_DIRECTORIES_AMOUNT = 16;
	private final int MAX_DATABASES_IN_DIRECTORY_AMOUNT = 16;
	private final int MAX_TABLE_SIZE = 1000*1000*100;
	
	private void writeInFile(File currentFile, Set<String> keys) throws IOException {
		if(!currentFile.createNewFile()) {
			throw new IOException("Can not create file '" + currentFile.getName() + "'.");
		}
		DataOutputStream writer = null;
		try {
			writer = new DataOutputStream(new FileOutputStream(currentFile));
			String value;
			for(String key : keys) {
				value = tableProvider.serialize(this, this.newDatabase.get(key));
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
		File signature = new File(this.tablePath, "signature.tsv");
		if(signature.exists()) {
			return;
		} 
		if(!signature.createNewFile()) {
			throw new IOException("Can not create 'signature.tsv' file.");
		}
		DataOutputStream writer = null;
		List<String> columnTypesNames = Utils.getColumnTypesNames(this.columnTypes);
		try {
			writer = new DataOutputStream(new FileOutputStream(signature));
			for(int i = 0; i < this.getColumnsCount(); ++i) {
				writer.writeUTF(columnTypesNames.get(i));
				if(i != this.getColumnsCount() - 1) {
					writer.writeUTF(" ");
				}
			}
		} finally {
			writer.close();
		}
	}
	
	private void write() throws IOException {
		writeSignature();
		File directory;
		for(int i = 0; i < this.MAX_DIRECTORIES_AMOUNT; ++i) {
			directory = new File(this.tablePath, i + ".dir");
			if(!directory.exists()) {
				continue;
			}
			try {
				Utils.remover(directory, "commit", false);
			} catch (IOException e) {
				throw new IOException("Can not clean table direcory.");
			}
		}
		Map<Integer, Map<Integer, Set<String>>> keysDueTheirHash = new HashMap<Integer,Map<Integer, Set<String>>>();
		for(int i = 0; i < this.MAX_DIRECTORIES_AMOUNT; ++i) {
			keysDueTheirHash.put(i, new HashMap<Integer, Set<String>>());
			for(int j = 0; j < this.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
				keysDueTheirHash.get(i).put(j, new HashSet<String>());
			}
		}
		int dir;
		int dat;
		for(String key : this.newDatabase.keySet()) {
			dir = Utils.getNDirectory(key);
			dat = Utils.getNFile(key);
			keysDueTheirHash.get(dir).get(dat).add(key);
		}
		File currentFile;
		for(int i = 0; i < this.MAX_DIRECTORIES_AMOUNT; ++i) {
			if(keysDueTheirHash.get(i).size() == 0) {
				continue;
			}
			directory = new File(this.tablePath, i + ".dir");
			for(int j = 0; j < this.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
				if(keysDueTheirHash.get(i).get(j).size() == 0) {
					continue;
				}
				if(!directory.exists()) {
					if(!directory.mkdir()) {
						throw new IOException("Can not create directory '" + i + ".dir'.");
					}
				}
				currentFile = new File(directory, j + ".dat");
				this.writeInFile(currentFile, keysDueTheirHash.get(i).get(j));
			}
		}
	}
	
	private int countChanges() {
		int changesCount  = 0;
		int intersectionSize = 0;
		Storeable originalValue = null;
		Storeable newValue = null;
		for(String key : this.originalDatabase.keySet()) {
			originalValue = this.originalDatabase.get(key);
			newValue = this.newDatabase.get(key);
			if(newValue != null) {
				++intersectionSize;
				if(!newValue.equals(originalValue)) {
					++changesCount;
				}
			}
		}
		changesCount += this.originalDatabase.size() + this.newDatabase.size() - 2 * intersectionSize;
		return changesCount;
	}
	
	private boolean checkColumnTypes(Storeable value) {
		for(int i = 0; i < this.getColumnsCount(); ++i) {
			try {
				switch(this.columnTypes.get(i).getName()) {
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
			value.getColumnAt(this.getColumnsCount());
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
		return false;
	}
	
	private boolean checkHash(int directory, int database, String key) {
		if(directory != Utils.getNDirectory(key) || database != Utils.getNFile(key)) {
			return false;
		}
		return true;
	}
	
	private void readFile(int directory, int database) throws IOException {
		File currentFile = new File(this.tablePath, directory + ".dir");
		if(!currentFile.exists()) {
			return;
		}
		if(!currentFile.isDirectory()) {
			throw new IOException("File '" + directory + ".dir' must be directory.");
		}
		currentFile = new File(currentFile, database + ".dat");
		if(!currentFile.exists()) {
			return;
		}
		if(!currentFile.isFile()) {
			throw new IOException("File '" + database + ".dat' in directory '" + directory + ".dir' can not be be directory.");
		}
		
		int keyLength;
		int valueLength;
		String key;
		String value;
		DataInputStream reader = null;
		try {
			reader = new DataInputStream(new FileInputStream(currentFile));
			while(true) {
				try {
					keyLength = reader.readInt();
				} catch(EOFException e) {
					break;
				}
					if(keyLength <= 0 || keyLength >= 1000*1000) {
						throw new IOException("reader: Invalid key length.");
					}
					
					valueLength = reader.readInt();
					if(valueLength <= 0 || valueLength >= 1000*1000) {
						throw new IOException("reader: Invalid value length.");
					}
					
					byte[] keyByteArray = new byte[keyLength];
					reader.read(keyByteArray, 0, keyLength);
					key = new String(keyByteArray);
					
					byte[] valueByteArray = new byte[valueLength];
					reader.read(valueByteArray, 0, valueLength);
					value = new String(valueByteArray);
					
					if(!this.checkHash(directory, database, key)) {
						throw new IOException("Key " + key + " can not be in '" +
								directory + ".dir/" + database + ".dat'.");
					}
					
					try {
						this.originalDatabase.put(key, this.tableProvider.deserialize(this, value));
					} catch (ParseException e) {
						throw new IOException(e.getMessage());
					}
					
					if(this.originalDatabase.size() >= this.MAX_TABLE_SIZE) {
						throw new IOException("Table '" + this.name + "' is overly big.");
					}
				
			}
		} finally {
			reader.close();
		}
		
	}
	
	
	public StoreableTable(File dir, String name, List<Class<?>> columnTypes, TableProvider tableProvider) throws IOException {
		this.name = name;
		this.tableProvider = tableProvider;
		this.columnTypes = columnTypes;
		this.tablePath = new File(dir, name);
		if(!this.tablePath.exists()) {
			if(!this.tablePath.mkdir()) {
				throw new IOException("Can not create directory for table " + this.name);
			}
		} else {
			for(int i = 0; i < this.MAX_DIRECTORIES_AMOUNT; ++i) {
				for(int j = 0; j < this.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
					this.readFile(i, j);
				}
			}
		}
		this.newDatabase = this.originalDatabase;
	}
	
	

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Storeable get(String key) throws IllegalArgumentException {
		if(Utils.isEmpty(key)) {
			throw new IllegalArgumentException("Key can not be null");
		}
		return this.newDatabase.get(key);
	}

	@Override
	public Storeable put(String key, Storeable value) throws ColumnFormatException, IllegalArgumentException {
		if(Utils.isEmpty(key) || value == null) {
			throw new IllegalArgumentException("Key and name can not be null or newline");
		}
		if(!this.checkColumnTypes(value)) {
			throw new ColumnFormatException("Incorrent column types in given storeable.");
		}
		return this.newDatabase.put(key, value);
	}

	@Override
	public Storeable remove(String key) throws IllegalArgumentException {
		if(Utils.isEmpty(key)) {
			throw new IllegalArgumentException("Key can not be null");
		}
		return this.newDatabase.remove(key);
	}

	@Override
	public int size() {
		return this.newDatabase.size();
	}

	@Override
	public int commit() throws IOException {
		int changesCount = this.countChanges();
		this.write();
		this.originalDatabase = this.newDatabase;
		return changesCount;
	}

	@Override
	public int rollback() {
		int changesCount = this.countChanges();
		this.newDatabase = this.originalDatabase;
		return changesCount;
	}

	@Override
	public int getColumnsCount() {
		return this.columnTypes.size();
	}

	@Override
	public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
		if(columnIndex < 0 || columnIndex >= this.getColumnsCount()) {
			throw new IndexOutOfBoundsException("Column index can not be less then 0 and more then types amount.");
		}
		return this.columnTypes.get(columnIndex); 
	}

}
