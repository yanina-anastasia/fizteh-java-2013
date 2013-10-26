package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.irinapodorozhnaya.db.DbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;

public class Table {
	
	public final String name;
	private final File tableDiectory;
	private final Map<Integer, Map<String, String>> database = new HashMap<>();
	private final Map<Integer, File> files = new HashMap<>();
	
	public Table(String name, File rootDir) throws IOException {
		this.name = name;
		tableDiectory = new File (rootDir, name);		
		if (!tableDiectory.isDirectory()) {
			throw new IOException("no such table");
		}
	}
	
	public String get(String key) throws IOException {
		int nfile = getFileNumber(key);
		if (check(nfile)) {
			return database.get(nfile).get(key);
		} else { 
			return null;
		}
	}
	
	private boolean check(int nfile) throws IOException {
		if (database.get(nfile) == null) {
			File dir = new File(tableDiectory, nfile/16 +".dir");
			if (!dir.isDirectory()) {
				return false;
			}
			File db = new File(dir, nfile%16 + ".dat");
			if (!db.exists()) {
				return false;
			} else {
				files.put(nfile, db);
				database.put(nfile, FileStorage.openDataFile(db, nfile));
			}
		}
		return true;
	}
	
	public String remove(String key) throws IOException {
		int nfile = getFileNumber(key);
		if (check(nfile)) {
			return database.get(nfile).remove(key);
		} else { 
			return null;
		}
	}

	public String put(String key, String value) throws IOException {
		int nfile = getFileNumber(key);
		if (check(nfile)) {
			return database.get(nfile).put(key, value);
		} else { 
			database.put(nfile, new HashMap<String, String>());
			return database.get(nfile).put(key, value);
		}
	}
	
	public static int getFileNumber(String key){
		int hashcode = key.hashCode();
		int ndirectory = hashcode % 16;
		int nfile = hashcode / 16 % 16;
		return ndirectory*16 + nfile;
	}
	
	public void commitDif() throws IOException {
		for (int i = 0 ; i < 256; ++i) {
			if (database.get(i) != null) {
				if (files.get(i) == null) {
					Integer in = i/16;
					File dir = new File(tableDiectory, in.toString() +".dir");	
					if (!dir.isDirectory()) {
						if (!dir.mkdir()) {
							throw new IOException("can't create directory");
						}
					}
					in = i%16;
					File db = new File(dir, in.toString() + ".dat");
					if (!db.exists()) {
						if (!db.createNewFile()) {
							throw new IOException("can't create file");
						}
					}
					files.put(i, db);
				}
				FileStorage.commitDiff(files.get(i), database.get(i));
			}
			if (i != 0 && i%16 == 0) {
				File dir = new File(tableDiectory, i/16 +".dir");
				dir.delete();
			}
		}
	}
}
