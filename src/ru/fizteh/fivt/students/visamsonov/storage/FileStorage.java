package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.Table;
import java.util.TreeMap;
import java.util.Map;
import java.math.BigInteger;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileStorage implements TableInterface {

	private final File dbFilePath;
	private final TreeMap<String, String> memoryStore;
	private final TreeMap<String, String> diffRemoved;
	private final TreeMap<String, String> diffAdded;
	private final int MAX_KEY_LENGTH = 1024*1024;
	private final int MAX_VAL_LENGTH = 1024*1024;
	private final int MAX_TOTAL_LENGTH = 500*1024*1024;

	public FileStorage (String directory, String fileName) throws IOException {
		dbFilePath = new File(directory, fileName);
		memoryStore = new TreeMap<String, String>();
		diffRemoved = new TreeMap<String, String>();
		diffAdded = new TreeMap<String, String>();
		loadDataToMemory();
	}

	private void loadDataToMemory () throws IOException {
		if (!dbFilePath.isFile()) {
			return;
		}
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
					memoryStore.put(key.getValue(), new String(valueBytes.toByteArray(), StandardCharsets.UTF_8));
				}
				else {
					final int length = offsets.firstKey() - key.getKey();
					if (length > MAX_VAL_LENGTH) {
						throw dataFormatError;
					}
					currentOffset += length;
					byte[] valueBytes = new byte[length];
					dbFile.readFully(valueBytes);
					memoryStore.put(key.getValue(), new String(valueBytes, StandardCharsets.UTF_8));
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
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return memoryStore.get(key);
	}

	public String put (String key, String value) {
		if (key == null || value == null || key.trim().isEmpty() || value.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (!value.equals(memoryStore.get(key))) {
			if (value.equals(diffRemoved.get(key))) {
				diffRemoved.remove(key);
			}
			else if (diffAdded.get(key) == null && diffRemoved.get(key) == null && memoryStore.get(key) != null) {
				diffRemoved.put(key, memoryStore.get(key));
			}
			else if (diffRemoved.get(key) == null) {
				diffAdded.put(key, value);
			}
		}
		return memoryStore.put(key, value);
	}

	public String remove (String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (memoryStore.get(key) == null) {
			return null;
		}
		if (diffAdded.get(key) == null && diffRemoved.get(key) == null) {
			diffRemoved.put(key, memoryStore.get(key));
		}
		diffAdded.remove(key);
		return memoryStore.remove(key);
	}

	public int rollback () {
		for (String key : diffAdded.keySet()) {
			memoryStore.remove(key);
		}
		for (Map.Entry<String, String> entry : diffRemoved.entrySet()) {
			memoryStore.put(entry.getKey(), entry.getValue());
		}
		int result = diffAdded.size() + diffRemoved.size();
		diffAdded.clear();
		diffRemoved.clear();
		return result;
	}

	public int size () {
		return memoryStore.size();
	}

	public int unsavedChanges () {
		return diffAdded.size() + diffRemoved.size();
	}

	public int commit () {
		int result = diffAdded.size() + diffRemoved.size();
		diffAdded.clear();
		diffRemoved.clear();
		if (memoryStore.size() == 0) {
			dbFilePath.delete();
			return result;
		}
		try {
			dbFilePath.createNewFile();
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
			for (Map.Entry<String, String> entry : memoryStore.entrySet()) {
				dbFile.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
			}
			dbFile.close();
		}
		catch (IOException e) {}
		return result;
	}
}