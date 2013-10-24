package ru.fizteh.fivt.students.msandrikova.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DBMap {
	private File currentFile;
	Map<String, String> mapDB = new HashMap<String, String>();
	private String name;
	
	
	public DBMap(File currentDirectory, String name) throws FileNotFoundException, IOException {
		this.name  = name;
		this.currentFile = new File(currentDirectory, name);
		if(!this.currentFile.exists()) {
			this.currentFile.createNewFile();
		} else if(this.currentFile.isDirectory()) {
			Utils.generateAnError("Table \"" + name + "\" can not be a directory.", "DBMap", false);
		}
		this.readFile();
	}
	
	public boolean checkHash(int dirNumber, int DBNumber) {
		Set<String> keySet = this.mapDB.keySet();
		for(String key : keySet) {
			int hashcode = Math.abs(key.hashCode());
			int ndirectory = hashcode % 16;
			int nfile = hashcode / 16 % 16;
			if(dirNumber != ndirectory || DBNumber != nfile) {
				return false;
			}
		}
		return true;
	}
	
	private void readFile() throws IOException, FileNotFoundException {
		int keyLength;
		int valueLength;
		String key;
		String value;
		DataInputStream reader = null;
		try {
			reader = new DataInputStream(new FileInputStream(this.currentFile));
			while(true) {
				try {
					keyLength = reader.readInt();
				} catch(EOFException e) {
					break;
				}
					if(keyLength <= 0 || keyLength >= 10*10*10*10*10*10) {
						Utils.generateAnError("Incorrect length of key.", "DBMap", false);
					}
					
					valueLength = reader.readInt();
					if(valueLength <= 0 || valueLength >= 10*10*10*10*10*10) {
						Utils.generateAnError("Incorrect length of value.", "DBMap", false);
					}
					
					byte[] keyByteArray = new byte[keyLength];
					reader.read(keyByteArray, 0, keyLength);
					key = new String(keyByteArray);
					
					byte[] valueByteArray = new byte[valueLength];
					reader.read(valueByteArray, 0, valueLength);
					value = new String(valueByteArray);
					
					mapDB.put(key, value);
				
			}
		} finally {
			reader.close();
		}
	}
	
	public void writeFile() throws IOException, FileNotFoundException {
		this.currentFile.delete();
		try {
			this.currentFile.createNewFile();
		} catch (IOException e) {}
		DataOutputStream writer = null;
		try {
			writer = new DataOutputStream(new FileOutputStream(this.currentFile));
			Set<String> keySet = mapDB.keySet();
			String value;
			for(String key : keySet) {
				value = mapDB.get(key);
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
	
	public String put(String key, String value) throws IllegalArgumentException {
		if(key == null || value == null) {
			throw new IllegalArgumentException("Key and name can not be null");
		}
		return mapDB.put(key, value);
	}
	
	public String get(String key) throws IllegalArgumentException {
		if(key == null) {
			throw new IllegalArgumentException("Key can not be null");
		}
		return(mapDB.get(key));
	}
	
	public String remove(String key) throws IllegalArgumentException {
		if(key == null) {
			throw new IllegalArgumentException("Key can not be null");
		}
		return mapDB.remove(key);
	}

	public String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.mapDB.size();
	}
	
	public void delete() {
		this.currentFile.delete();
	}

}
