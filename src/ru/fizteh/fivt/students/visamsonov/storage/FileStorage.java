package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.TreeMap;
import java.util.Map;
import java.math.BigInteger;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileStorage implements Table {

	private final File dbFilePath;
	private final TreeMap<String, String> memoryStore;
	private final int MAX_KEY_LENGTH = 1024*1024;
	private final int MAX_VAL_LENGTH = 1024*1024;
	private final int MAX_TOTAL_LENGTH = 500*1024*1024;

	public FileStorage (String directory, String fileName) throws IOException {
		dbFilePath = new File(directory, fileName);
		memoryStore = new TreeMap<String, String>();
		dbFilePath.createNewFile();
		loadDataToMemory();
	}

	private void loadDataToMemory () throws IOException {
		DataInputStream dbFile = new DataInputStream(new BufferedInputStream(new FileInputStream(dbFilePath)));
		IOException dataFormatError = new IOException("invalid data file");
		TreeMap<Integer, String> offsets = new TreeMap<Integer, String>();
		int currentOffset = 0;
		try {
			do {
				ByteArrayOutputStream keyBytes = new ByteArrayOutputStream();
				for (byte singleByte = dbFile.readByte(); singleByte != 0; singleByte = dbFile.readByte()) {
					keyBytes.write(singleByte);
					if (keyBytes.size() > MAX_KEY_LENGTH) {
						throw dataFormatError;
					}
				}
				currentOffset += keyBytes.size() + 1;
				int valueOffset = dbFile.readInt();
				currentOffset += 4;
				if (valueOffset < currentOffset) {
					throw dataFormatError;
				}
				offsets.put(valueOffset, new String(keyBytes.toByteArray(), StandardCharsets.UTF_8));
				if (currentOffset > offsets.firstKey() || currentOffset > MAX_TOTAL_LENGTH) {
					throw dataFormatError;
				}
			} while (currentOffset < offsets.firstKey());
			while (offsets.size() > 0) {
				Map.Entry<Integer, String> key = offsets.pollFirstEntry();
				if (key.getKey() != currentOffset || currentOffset > MAX_TOTAL_LENGTH) {
					throw dataFormatError;
				}
				if (offsets.size() == 0) {
					ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
					byte[] singleByte = new byte[1];
					for (int result = dbFile.read(singleByte); result == 1; result = dbFile.read(singleByte)) {
						valueBytes.write(singleByte);
						if (valueBytes.size() > MAX_VAL_LENGTH) {
							throw dataFormatError;
						}
					}
					currentOffset += valueBytes.size();
					if (currentOffset > MAX_TOTAL_LENGTH) {
						throw dataFormatError;
					}
					put(key.getValue(), new String(valueBytes.toByteArray(), StandardCharsets.UTF_8));
				}
				else {
					final int length = offsets.firstKey() - key.getKey();
					if (length > MAX_VAL_LENGTH) {
						throw dataFormatError;
					}
					currentOffset += length;
					byte[] valueBytes = new byte[length];
					dbFile.readFully(valueBytes);
					put(key.getValue(), new String(valueBytes, StandardCharsets.UTF_8));
				}
			}
		}
		catch (EOFException e) {
			if (currentOffset > 0) {
				throw dataFormatError;
			}
		}
	}

	public String getName () {
		throw new UnsupportedOperationException();
	}

	public String get (String key) {
		return memoryStore.get(key);
	}

	public String put (String key, String value) {
		return memoryStore.put(key, value);
	}

	public String remove (String key) {
		return memoryStore.remove(key);
	}

	public int rollback () {
		throw new UnsupportedOperationException();
	}

	public int size () {
		throw new UnsupportedOperationException();
	}

	public int commit () {
		int saved = 0;
		try {
			DataOutputStream dbFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dbFilePath)));
			int dataOffset = 0;
			for (String key : memoryStore.keySet()) {
				dataOffset += key.getBytes(StandardCharsets.UTF_8).length + 5;
			}
			for (Map.Entry<String, String> entry : memoryStore.entrySet()) {
				dbFile.write(entry.getKey().getBytes(StandardCharsets.UTF_8));
				dbFile.write(0);
				dbFile.writeInt(dataOffset);
				dataOffset += entry.getValue().getBytes(StandardCharsets.UTF_8).length;
			}
			while (memoryStore.firstEntry() != null) {
				dbFile.write(memoryStore.firstEntry().getValue().getBytes(StandardCharsets.UTF_8));
				memoryStore.pollFirstEntry();
				++saved;
			}
			dbFile.close();
		}
		catch (IOException e) {}
		return saved;
	}
}