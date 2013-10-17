package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandExit;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.State;

public class DbState extends State {

	private final Map<String, String> data = new HashMap<String, String>();
	private RandomAccessFile dbFile;
	
	DbState (InputStream in, PrintStream out) throws  IOException{
		super(in, out);
		openDataFile();
		add (new CommandExit(this));
		add (new CommandPut(this));		
		add (new CommandRemove(this));		
		add (new CommandGet(this));				
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
	private void openDataFile() throws IOException{
		String path = System.getProperty("fizteh.db.dir");
		if (path == null) {
			throw new IOException("can't get property");
		}
		setCurrentDir(new File(path, "db.dat"));
		if (getCurrentDir().exists()) {
			try {
				loadDataFromFile();
			} catch (EOFException e) {
				throw new IOException("File has wrong format");
			}
		}
	}
	
	private void loadDataFromFile() throws FileNotFoundException, IOException{
		dbFile = new RandomAccessFile(getCurrentDir(), "r");
		if (dbFile.length() == 0) {
			return;
		}
		long nextOffset = 0;
		
		dbFile.seek(0);
		String key = readKey();
		long firstOffset = dbFile.readInt();
		long currentOffset = firstOffset;
		long pos = dbFile.getFilePointer();
		String nextKey = key;
		
		while (pos < firstOffset) {
			nextKey = readKey();
			nextOffset = dbFile.readInt();
			pos = dbFile.getFilePointer();
			dbFile.seek(currentOffset);
			data.put(key, readValue(nextOffset - currentOffset));
			dbFile.seek(pos);
			key = nextKey;
			currentOffset = nextOffset;
		}
		data.put(nextKey, readValue(dbFile.length() - currentOffset));
		dbFile.close();
	}
	
	private String readValue(long l) throws IOException {
		int len = (int) l;
		if ( len < 0) {
			throw new IOException("File has incorrect format");
		}
		byte[] bytes = new byte[(int) l];
		dbFile.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	String readKey() throws IOException {
		byte c = dbFile.readByte();
		ArrayList<Byte> v =  new ArrayList<>();
		while (c != 0) {
			v.add(c);
			c = dbFile.readByte();
		}
		byte[] bytes = new byte[v.size()];
		for (int i = 0; i < v.size(); ++i) {
			bytes[i] = v.get(i);
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public void commitDiff() throws IOException {
		File tmp = new File (getCurrentDir().getName() + '~');
		tmp.createNewFile();
		RandomAccessFile tmpR = new RandomAccessFile(tmp, "rw");
		int offset = 0;
		long pos = 0;

		Set<String> keys = getData().keySet();
		for (String s: keys) {
			offset += s.getBytes(StandardCharsets.UTF_8).length + 5;
		}
		for (Map.Entry<String, String> s: getData().entrySet()) {
			tmpR.seek(pos);
			tmpR.write(s.getKey().getBytes(StandardCharsets.UTF_8));
			tmpR.writeByte(0);
			tmpR.writeInt(offset);
			pos = tmpR.getFilePointer();
			tmpR.seek(offset);
			tmpR.write(s.getValue().getBytes(StandardCharsets.UTF_8));
			offset = (int) tmpR.getFilePointer();
		}
		if (tmpR.length() == 0) {
			getCurrentDir().delete();
			tmpR.close();
			tmp.delete();
			return;
		}
		
		tmpR.close();		
		getCurrentDir().delete();
		tmp.renameTo(getCurrentDir());
	}
}