package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DBDirectory {
	private String name;
	private File directoryPath;
	private Map<Integer, DBMap> mapOfDB = new HashMap<Integer, DBMap>();
	private int MAX_DB_AMOUNT = 16;
	
	private void getDB(int nameNumber) throws IOException, FileNotFoundException {
		String name = Integer.toString(nameNumber) + ".dat";
		File DBPath = new File(this.directoryPath, name);
		if(DBPath.exists()) {
			if(DBPath.isDirectory()){
				Utils.generateAnError("File \"" + name + "\" in directory \"" 
						+ this.name + "\" can not be a directory.", "DBDirectory", false);
			}
			DBMap newDB = new DBMap(this.directoryPath, name);
			if(!newDB.checkHash(Utils.getNameNumber(this.name), nameNumber)) {
				Utils.generateAnError("Incorrect keys in directory \"" + this.name + "\" in data base \"" + name + "\".", "use", false);
			}
			if(newDB.getSize() == 0) {
				newDB.delete();
			} else {
				this.mapOfDB.put(nameNumber, newDB);
			}
		}
	}

	public DBDirectory (File tableDirectory, String name) {
		this.name = name;
		this.directoryPath = new File(tableDirectory, name);
		if(!this.directoryPath.exists()) {
			this.directoryPath.mkdir();
		} else {
			if(!this.directoryPath.isDirectory()) {
				Utils.generateAnError("File \"" + this.name + "\" should be directory.", "DBDirectory", false);
			}
			for(int i = 0; i < this.MAX_DB_AMOUNT; i++) {
				try {
					this.getDB(i);
				} catch (IOException e) {
					Utils.generateAnError("Can not open or use required data base in directory \"" 
							+ this.name + "\".", "DBDirectory", false);
				}
			}
		}
	}
	
	private DBMap createDB(int nameNumber) throws IOException, FileNotFoundException {
		DBMap newDB = null;
		String name = Integer.toString(nameNumber) + ".dat";
		newDB = new DBMap(this.directoryPath, name);
		this.mapOfDB.put(nameNumber, newDB);
		return newDB;
	}

	public String getName() {
		return this.name;
	}

	public String get(String key) throws IllegalArgumentException {
		int nfile = Utils.getNFile(key);
		String answer = null;
		DBMap currentDB = this.mapOfDB.get(nfile);
		if(currentDB != null) {
			answer = currentDB.get(key);
		}
		return answer;
	}

	public String put(String key, String value) throws IllegalArgumentException {
		int nfile = Utils.getNFile(key);
		String answer = null;
		DBMap currentDB = this.mapOfDB.get(nfile);
		if(currentDB == null) {
			try {
				currentDB = this.createDB(nfile);
			} catch (IOException e) {
				Utils.generateAnError("Can not open or use required data base.", "put", false);
			}
		}
		answer = currentDB.put(key, value);
		return answer;
	}

	public String remove(String key) throws IllegalArgumentException {
		int nfile = Utils.getNFile(key);
		String answer = null;
		DBMap currentDB = this.mapOfDB.get(nfile);
		if(currentDB != null) {
			answer = currentDB.remove(key);
			if(currentDB.getSize() == 0) {
				this.mapOfDB.remove(nfile);
				currentDB.delete();
			}
		}
		return answer;
	}
	
	public int getSize() {
		int result = 0;
		Set<Integer> keySet = this.mapOfDB.keySet();
		DBMap curDB = null;
		for(Integer key : keySet) {
			curDB = this.mapOfDB.get(key);
			result += curDB.getSize();
		}
		return result;
	}
	
	public int getDBCount() {
		return this.mapOfDB.size();
	}
	
	public void commit() {
		Set<Integer> keySet = this.mapOfDB.keySet();
		DBMap currentDB = null;
		for(Integer key : keySet) {
			currentDB = this.mapOfDB.get(key);
			try {
				currentDB.writeFile();
			} catch (FileNotFoundException e) {
				Utils.generateAnError("Fatal error during writing", "commit", false);
			} catch (IOException e) {
				Utils.generateAnError("Fatal error during writing", "commit", false);
			}
		}
	}
	
	public void delete() {
		try {
			Utils.remover(this.directoryPath, "remove", false);
		} catch (IOException e) {
			Utils.generateAnError("Fatal error during deleting", "remove", false);
		}
	}
}
