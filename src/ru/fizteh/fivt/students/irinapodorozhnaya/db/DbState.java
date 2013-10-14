package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		setCurrentDir(new File(path, "db.dat"));
		if (!getCurrentDir().exists()) {
			if (!getCurrentDir().createNewFile()) {
				throw new IOException("can't create db.dat");
			} else {
				dbFile = new RandomAccessFile(getCurrentDir(), "rw");
			}
		} else {
			try {
				loadDataFromFile();
			} catch (EOFException e) {
				throw new IOException("File have wrong format");
			}
		}
	}
	
	private void loadDataFromFile() throws FileNotFoundException, IOException{
		dbFile = new RandomAccessFile(getCurrentDir(), "rw");
		if (dbFile.length() == 0) {
			return;
		}
		long currentOffset;
		long firstOffset = 0;
		long pos = 0;
		String key = null;
		String value = null;
		do {
			dbFile.seek(pos);
			key = dbFile.readUTF();
			dbFile.readChar();
			currentOffset = dbFile.readInt();
			if (firstOffset == 0) {
				firstOffset = currentOffset;
			}
			pos = dbFile.getFilePointer();
			dbFile.seek(currentOffset);
			value = dbFile.readUTF();
			data.put(key, value);
		} while (pos < firstOffset);
	}
	

	public void commitDiff() throws IOException {
		RandomAccessFile dbFile = getDbFile();
		int offset = 0;
		long pos = 0;
		
		Set<String> keys = getData().keySet();
		for (String s: keys) {
			offset += s.getBytes("UTF-8").length + 8;
		}
		
		for (Map.Entry<String, String> s: getData().entrySet()) {
			dbFile.seek(pos);
			dbFile.writeUTF(s.getKey());
			dbFile.writeChar('\0');
			dbFile.writeInt(offset);
			pos = dbFile.getFilePointer();
			dbFile.seek(offset);
			dbFile.writeUTF(s.getValue());
			offset = (int) dbFile.getFilePointer();
		}
		
		if (dbFile.length() == 0) {
			getCurrentDir().delete();
		}
	}

}