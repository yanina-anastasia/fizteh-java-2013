package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTable implements Table {
	private int size;
	private String name;
	private File tablePath;
	private Map<Integer, DBDirectory> mapOfDirectories = new HashMap<Integer, DBDirectory>();
	private int MAX_DIRECTORIES_AMOUNT = 16;
	private int MAX_TABLE_SIZE = 1000*1000*100;
	
	private void setSize() {
		this.size = 0;
		Set<Integer> keySet = this.mapOfDirectories.keySet();
		DBDirectory curDirectory = null;
		for(Integer key : keySet) {
			curDirectory = this.mapOfDirectories.get(key);
			this.size += curDirectory.getSize();
		}
	}
	
	private void getDirectory(int nameNumber) throws IOException, FileNotFoundException {
		String name = Integer.toString(nameNumber) + ".dir";
		File dirPath = new File(this.tablePath, name);
		if(dirPath.exists()) {
			if(!dirPath.isDirectory()) {
				Utils.generateAnError("File \"" + name + "\"should be directory in table \"" 
						+ this.name + ".", "create", false);
			}
			DBDirectory newDirectory = new DBDirectory(this.tablePath, name);
			if(newDirectory.getDBCount() == 0){
				newDirectory.delete();
			} else {
				this.mapOfDirectories.put(nameNumber, newDirectory);
			}
		}
	}

	public MyTable(File parentDirectory, String name) {
		this.name = name;
		this.tablePath = new File(parentDirectory, name);
		if(!tablePath.exists()) {
			this.tablePath.mkdir();
		} else {
			if(!this.tablePath.isDirectory()) {
				Utils.generateAnError("Table with name \"" + this.name + "\"should be directory.", "create", false);
			}
			for(int i = 0; i < this.MAX_DIRECTORIES_AMOUNT; i++) {
				try {
					this.getDirectory(i);
				} catch (IOException e) {
					Utils.generateAnError("Can not open or use required data base directory\"" + i 
							+ ".dir\" in table \"" + this.name + ".", "create", false);
				}
			}
		}
		this.setSize();
		if(this.size > this.MAX_TABLE_SIZE) {
			Utils.generateAnError("Table \"" + this.name + "\" is overly big.", "use", false);
		}
	}
	
	private DBDirectory createDirectory(int nameNumber) throws IOException, FileNotFoundException {
		DBDirectory newDirectory = null;
		String name = Integer.toString(nameNumber) + ".dir";
		newDirectory = new DBDirectory(this.tablePath, name);
		this.mapOfDirectories.put(nameNumber, newDirectory);
		return newDirectory;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String get(String key) throws IllegalArgumentException {
		int hashcode = key.hashCode();
		int ndirectory = hashcode % 16;
		ndirectory = Math.abs(ndirectory);
		String answer = null;
		DBDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
		if(currentDirectory != null) {
			answer = currentDirectory.get(key);
		}
		return answer;
	}

	@Override
	public String put(String key, String value) throws IllegalArgumentException {
		int hashcode = key.hashCode();
		int ndirectory = hashcode % 16;
		ndirectory = Math.abs(ndirectory);
		String answer = null;
		DBDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
		if(currentDirectory == null) {
			try {
				currentDirectory = this.createDirectory(ndirectory);
			} catch (IOException e) {
				Utils.generateAnError("Can not open or use required data base directory.", "put", false);
			}
		}
		answer = currentDirectory.put(key, value);
		if(answer == null) {
			this.size++;
			if(this.size > this.MAX_TABLE_SIZE) {
				Utils.generateAnError("Table \"" + this.name + "\" is overly big.", "use", false);
			}
		}
		return answer;
	}

	@Override
	public String remove(String key) throws IllegalArgumentException {
		int hashcode = key.hashCode();
		int ndirectory = hashcode % 16;
		ndirectory = Math.abs(ndirectory);
		String answer = null;
		DBDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
		if(currentDirectory != null) {
			answer = currentDirectory.remove(key);
		}
		if(answer != null) {
			if(currentDirectory.getDBCount() == 0){
				this.mapOfDirectories.remove(ndirectory);
				currentDirectory.delete();
			}
			this.size--;
		}
		return answer;
	}
	

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public int commit() {
		Set<Integer> keySet = this.mapOfDirectories.keySet();
		DBDirectory currentDirectory = null;
		for(Integer key : keySet) {
			currentDirectory = this.mapOfDirectories.get(key);
			currentDirectory.commit();
		}
		return this.size();
	}

	@Override
	public int rollback() {
		// TODO Auto-generated method stub
		return 0;
	}

}
