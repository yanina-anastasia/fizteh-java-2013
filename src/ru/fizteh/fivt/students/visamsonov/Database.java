package ru.fizteh.fivt.students.visamsonov;

import java.util.TreeMap;
import java.math.BigInteger;
import java.io.*;

public class Database {

	public TreeMap<String, String> database = new TreeMap<String, String>();
	private final String dbFileName = "db.dat";

	public void loadDataToMemory () throws FileNotFoundException, IOException {
		String directory = System.getProperty("fizteh.db.dir");
		FileInputStream dbFile = new FileInputStream(new File(directory, dbFileName));
		IOException ioFormatError = new IOException("invalid format of database");
		for (;;) {
			int keyLength, valueLength, result;
			byte[] data = new byte[4];
			result = dbFile.read(data);
			if (result == -1) {
				break;
			}
			if (result < data.length) {
				throw ioFormatError;
			}
			keyLength = new BigInteger(data).intValue();
			result = dbFile.read(data);
			if (result < data.length) {
				throw ioFormatError;
			}
			valueLength = new BigInteger(data).intValue();
			byte[] keyRaw = new byte[keyLength];
			result = dbFile.read(keyRaw);
			if (result < keyLength) {
				throw ioFormatError;
			}
			byte[] valueRaw = new byte[valueLength];
			result = dbFile.read(valueRaw);
			if (result < valueLength) {
				throw ioFormatError;
			}
			database.put(new String(keyRaw), new String(valueRaw));
		}
	}

	private final byte[] intToBytes (int value) {
		return new byte[] {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
	}

	public void saveDataToFile () throws IOException {
		String directory = System.getProperty("fizteh.db.dir");
		FileOutputStream dbFile = new FileOutputStream(new File(directory, dbFileName));
		while (database.firstEntry() != null) {
			String key = database.firstEntry().getKey();
			String value = database.firstEntry().getValue();
			database.pollFirstEntry();
			dbFile.write(intToBytes(key.length()));
			dbFile.write(intToBytes(value.length()));
			dbFile.write(key.getBytes());
			dbFile.write(value.getBytes());
		}
		dbFile.close();
	}
}