package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.TreeMap;
import java.math.BigInteger;
import java.io.*;

public class FileStorage extends TreeMap<String, String> implements Table {

	private final File dbFilePath;

	public FileStorage (String directory, String fileName) throws FileNotFoundException, IOException {
		dbFilePath = new File(directory, fileName);
		loadDataToMemory();
	}

	private void loadDataToMemory () throws FileNotFoundException, IOException {
		DataInputStream dbFile = new DataInputStream(new FileInputStream(dbFilePath));
		for (;;) {
			int keyLength, valueLength;
			try {
				keyLength = dbFile.readInt();
			}
			catch (EOFException e) { // It's okay
				break;
			}
			valueLength = dbFile.readInt();
			byte[] keyRaw = new byte[keyLength];
			dbFile.readFully(keyRaw);
			byte[] valueRaw = new byte[valueLength];
			dbFile.readFully(valueRaw);
			put(new String(keyRaw), new String(valueRaw));
		}
	}

	public String getName () {
		throw new UnsupportedOperationException();
	}

	public String get (String key) {
		return super.get(key);
	}

	public String put (String key, String value) {
		return super.put(key, value);
	}

	public String remove (String key) {
		return super.remove(key);
	}

	public int rollback () {
		throw new UnsupportedOperationException();
	}

	public int commit () {
		int saved = 0;
		try {
			DataOutputStream dbFile = new DataOutputStream(new FileOutputStream(dbFilePath));
			while (firstEntry() != null) {
				byte[] key = firstEntry().getKey().getBytes();
				byte[] value = firstEntry().getValue().getBytes();
				pollFirstEntry();
				dbFile.writeInt(key.length);
				dbFile.writeInt(value.length);
				dbFile.write(key);
				dbFile.write(value);
				++saved;
			}
			dbFile.close();
		}
		catch (IOException e) {}
		return saved;
	}
}