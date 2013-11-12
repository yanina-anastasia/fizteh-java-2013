package ru.fizteh.fivt.students.elenav.storeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Functions;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class StoreableTableState extends FilesystemState implements Table {

	private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;
    
	private List<Class<?>> columnTypes = new ArrayList<>();
	private final HashMap<String, Storeable> startMap = new HashMap<>();
	public final HashMap<String, Storeable> map = new HashMap<>();
	private int numberOfChanges = 0;
	
	public StoreableTableState(String n, File wd, PrintStream out, StoreableTableProvider provider2) {
		super(n, wd, out);
		provider = provider2;
		try {
			if (wd != null) {
				getColumnTypes();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void getColumnTypes() throws IOException {
		File f = new File(getWorkingDirectory(), "signature.tsv");
		if (!f.exists()) {
			throw new IOException("can't get " + getName() + "'s signature: file doesn't exist");
		}
		
		Scanner sc = new Scanner(f);
		StringBuilder sb = new StringBuilder();
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
			sb.append(" ");
		}
		String monoString = sb.toString(); 
		monoString = monoString.trim();
		String[] types = monoString.split("\\s+");
		for (String type : types) {
			columnTypes.add(Functions.getClassFromString(type));
		}
		
		sc.close();
		if (columnTypes.isEmpty()) {
			throw new IOException(getName() + " has empty signature");
		} 

	}

	@Override
	public Storeable get(String key) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("can't get null key");
		}
		return map.get(key);
	} 

	@Override
	public Storeable put(String key, Storeable value1) throws ColumnFormatException {
		if (key == null || value1 == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("can't put null key or(and) value");
		}
		if (key.split("\\s+").length != 1) {
			throw new IllegalArgumentException("can't put key with spaces inside");
		}
		checkStoreable(value1);
		Storeable value;
		try {
			value = Deserializer.run(this, Serializer.run(this, value1));
		} catch (ParseException | XMLStreamException e) {
			throw new ColumnFormatException(e);
		}
		Storeable currentValue = map.put(key, value);
		Storeable oldValue = startMap.get(key);
		if (currentValue == null) {
			if (oldValue == null)
				setNumberOfChanges(getNumberOfChanges() + 1);
			else {
				if (oldValue.equals(value)) {
					setNumberOfChanges(getNumberOfChanges() - 1);
				}
			}
		} else {
			if (!value.equals(currentValue)) {
				if (oldValue != null && oldValue.equals(currentValue)) {
					setNumberOfChanges(getNumberOfChanges() + 1);
				}
				if (oldValue != null && oldValue.equals(value)) {
					setNumberOfChanges(getNumberOfChanges() - 1);
				} 
			}
		}
		return currentValue;
	}
	
	private void checkStoreable(Storeable s) {
		int i = 0;
		try {
			for (; i < columnTypes.size(); ++i) {
				if (s.getColumnAt(i) != null && !columnTypes.get(i).isAssignableFrom(s.getColumnAt(i).getClass())) {
					throw new ColumnFormatException("column types are not similar");
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new ColumnFormatException("size is not similar");
		}
		try {
			s.getColumnAt(i);
			throw new ColumnFormatException("size is not similar");
		} catch (IndexOutOfBoundsException e) {
			// do nothing
		}
			
	}

	@Override
	public Storeable remove(String key) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("can't remove null key");
		}
		Storeable oldValue = startMap.get(key);
		Storeable value = map.remove(key);
		if (value != null) {
			if (oldValue == null) {
				setNumberOfChanges(getNumberOfChanges() - 1);
			} else {
				if (oldValue.equals(value)) {
					setNumberOfChanges(getNumberOfChanges() + 1);
				}
			}
		}
		return value;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public int commit() {
		int result = numberOfChanges;
		startMap.clear();
		startMap.putAll(map);
		numberOfChanges = 0;
		try {
			write();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public int rollback() {
		int result = numberOfChanges;
		map.clear();
		map.putAll(startMap);
		numberOfChanges = 0;
		return result;
	}

	public int getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(int numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	private int getDir(String key) throws IOException {
		int hashcode = Math.abs(key.hashCode());
		int ndirectory = hashcode % 16;
		if (!getWorkingDirectory().exists()) {
			getWorkingDirectory().mkdir();
		}
		File dir = new File(getWorkingDirectory(), ndirectory+".dir");
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new IOException("can't create dir");
			}
		}
		return ndirectory;
	}

	private int getFile(String key) throws IOException {
		int hashcode = Math.abs(key.hashCode());
		int ndirectory = hashcode % 16;
		int nfile = hashcode / 16 % 16;
		File dir = new File(getWorkingDirectory(), ndirectory+".dir");
		File file = new File(dir.getCanonicalPath(), nfile + ".dat");
		if (!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("can't create file");
			}
		}
		return nfile;
	}	

	@Override
	public void read() throws IOException {
		map.clear();
		File[] dirs = getWorkingDirectory().listFiles();
		if (dirs != null) {
			if (dirs.length == 0) {
				throw new IOException("can't read files: empty table " + getName());
			}
			for (File file : dirs) {
				File[] files = file.listFiles();
				if (files != null) {
					if (files.length == 0) {
						throw new IOException("can't read files: empty dir " + file.getName());
					}
					for (File f : files) {
						if (f.length() == 0) {
							throw new IOException("can't read files: empty file " + f.getName());
						}
						try {
							readFile(f, this);
						} catch (ParseException e) {
							throw new IOException("can't deserialize");
						}
						f.delete();
					}
				}
				file.delete();
			}
			startMap.clear();
			startMap.putAll(map);
		}
		
	}
	
	public void readFile(File in, StoreableTableState table) throws IOException, ParseException {
		DataInputStream s = new DataInputStream(new FileInputStream(in));
		boolean flag = true;
		do {
			try {
				int keyLength = s.readInt();
				int valueLength = s.readInt();
				if (keyLength <= 0 || valueLength <= 0 || keyLength >= 1024*1024 || valueLength >= 1024*1024) {
					throw new IOException("Invalid input");
				}
				byte[] tempKey = new byte[keyLength];	
				s.read(tempKey);
				String key = new String(tempKey, StandardCharsets.UTF_8);
				
				if (!in.getName().equals(getFile(key)) || !in.getParentFile().getName().equals(getDir(key))) {
					throw new IOException("wrong key placement");
				}
				
				byte[] tempValue = new byte[valueLength];
				s.read(tempValue);
				String value = new String(tempValue, StandardCharsets.UTF_8);
				try {
					table.map.put(key, Deserializer.run(table, value));
				} catch (XMLStreamException e) {
					throw new RuntimeException(e);
				}
			} catch (EOFException e) {
				break;
			}
		} while (flag);
		s.close();
		
	}
	
	public void write() throws IOException {
		if (getWorkingDirectory() != null) {
			for (int i = 0; i < DIR_COUNT; ++i) {
				for (int j = 0; j < FILES_PER_DIR; ++j) {
					Map<String, Storeable> toWriteInCurFile = new HashMap<>();
			
					for (String key : map.keySet()) {
						if (getDir(key) == i && getFile(key) == j) {
							toWriteInCurFile.put(key, map.get(key));
						}
					}
					
					if (toWriteInCurFile.size() > 0) {
						File dir = new File(getWorkingDirectory(), i + ".dir"); 
						File out = new File(dir, j + ".dat");
						DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
						Set<Entry<String, Storeable>> set = toWriteInCurFile.entrySet();
						for (Entry<String, Storeable> element : set) {
							try {
								Writer.writePair(element.getKey(), Serializer.run(this, element.getValue()), s);
							} catch (XMLStreamException e) {
								throw new IOException(e);
							}
						}
						s.close();
					}
				}
			}
		}
	}

	@Override
	public int getColumnsCount() {
		return columnTypes.size();
	}

	@Override
	public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
		return columnTypes.get(columnIndex);
	}

	@Override
	public String put(String key, String value) throws XMLStreamException, ParseException {
		Storeable storeable = put(key, Deserializer.run(this, value));
		if (storeable == null) {
			return null;
		}
		return Serializer.run(this, storeable);
	}

}
