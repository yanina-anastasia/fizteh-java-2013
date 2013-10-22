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
	
	
	public DBMap(File currentDirectory, boolean isInteractive) throws FileNotFoundException, IOException {
		this.currentFile = new File(currentDirectory, "db.dat");
		if(!this.currentFile.exists()) {
			try {
				this.currentFile.createNewFile();
			} catch (IOException e) {}
		}
		this.readFile();
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
				int counter = 0;
				try {
					keyLength = reader.readInt();
					if(keyLength <= 0 || keyLength >= 10*10*10*10*10*10) {
						Utils.generateAnError("Incorrect length of key.", "DBMap", false);
					}
					counter++;
					
					valueLength = reader.readInt();
					if(valueLength <= 0 || valueLength >= 10*10*10*10*10*10) {
						Utils.generateAnError("Incorrect length of value.", "DBMap", false);
					}
					counter++;
					
					byte[] keyByteArray = new byte[keyLength];
					reader.read(keyByteArray, 0, keyLength);
					key = new String(keyByteArray);
					counter++;
					
					byte[] valueByteArray = new byte[valueLength];
					reader.read(valueByteArray, 0, valueLength);
					value = new String(valueByteArray);
					counter++;
					
					mapDB.put(key, value);
				} catch(EOFException e) {
					if(counter != 0) {
						Utils.generateAnError("Incorrect amount of tokens in given file", "DBMap", false);
					}
					break;
				}
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
				writer.writeInt(key.getBytes(StandardCharsets.UTF_8).length);
				writer.writeInt(value.getBytes(StandardCharsets.UTF_8).length);
				writer.write(key.getBytes(StandardCharsets.UTF_8), 0, key.getBytes(StandardCharsets.UTF_8).length);
				writer.write(value.getBytes(StandardCharsets.UTF_8), 0, value.getBytes(StandardCharsets.UTF_8).length);
			}
		} finally {
			writer.close();
		}
	}
	
	public String put(String key, String value) {
		return mapDB.put(key, value);
	}
	
	public String get(String key) {
		return(mapDB.get(key));
	}
	
	public String remove(String key) {
		return mapDB.remove(key);
	}

}
