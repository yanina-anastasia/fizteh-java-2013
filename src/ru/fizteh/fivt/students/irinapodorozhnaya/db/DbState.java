package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.State;

public class DbState extends State {

	private final HashMap<String, String> data = new HashMap<String, String>();
	private RandomAccessFile dbFile;
	
	RandomAccessFile getDbFile() {
		return dbFile;
	}
	DbState () throws  IOException{
		openDataFile();
		add (new CommandExit(this));
		add (new CommandPut(this));		
		add (new CommandRemove(this));		
		add (new CommandGet(this));				
	}
	
	
	public HashMap<String, String> getData() {
		return data;
	}
	
	private void openDataFile() throws IOException{
		String path = System.getProperty("fizteh.db.dir");
		if (path == null) {
			throw new IOException("can't get property");
		}
		currentDir = new File(path, "db.dat");
		if (!currentDir.exists()) {
			if (!currentDir.createNewFile()) {
				throw new IOException("can't create db.dat");
			} else {
				dbFile = new RandomAccessFile(currentDir, "rw");
			}
		} else {
			try {
				loadDataFromFile();
			} catch (EOFException e) {
				throw new IOException("File is emply or have wrong format");
			}
		}
	}
	
	private void loadDataFromFile() throws FileNotFoundException, IOException{
		dbFile = new RandomAccessFile(currentDir, "rw");
		
		long currentOffset;
		long firstOffset = 0;
		boolean isFirst = true;
		long pos = 0;
		String key = null;
		String value = null;
		do {
			dbFile.seek(pos);
			key = dbFile.readUTF();
			dbFile.readChar();
			currentOffset = dbFile.readInt();
			if (isFirst) {
				firstOffset = currentOffset;
				isFirst = false;
			}
			pos = dbFile.getFilePointer();
			dbFile.seek(currentOffset);
			value = dbFile.readUTF();
			data.put(key, value);
		} while (pos < firstOffset);
	}
}