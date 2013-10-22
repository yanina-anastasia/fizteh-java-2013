package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.*;

class MultiFileStorage implements Table {

	private final int NUMBER_OF_DIRS = 16;
	private final int NUMBER_OF_FILES = 16;
	private final String directory;
	private final String tableName;
	private final Table[][] database;

	public MultiFileStorage (String directory, String tableName) throws IOException {
		this.directory = directory;
		this.tableName = tableName;
		database = new FileStorage[NUMBER_OF_DIRS][NUMBER_OF_FILES];
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			File subdirectory = new File(directory, dirNumber + ".dir");
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				database[dirNumber][fileNumber] = new FileStorage(subdirectory.getAbsolutePath(), fileNumber + ".dat");
			}
		}
	}

	private Table getAppropriate (String key) {
		byte randomData = key.getBytes()[0];
		int dirNumber = randomData % NUMBER_OF_DIRS;
		int nfile = (randomData / NUMBER_OF_DIRS) % NUMBER_OF_FILES;
		File subdirectory = new File(directory, dirNumber + ".dir");
		subdirectory.mkdir();
		return database[dirNumber][nfile];
	}

	public String getName () {
		return tableName;
	}

	public String get (String key) {
		return getAppropriate(key).get(key);
	}

	public String put (String key, String value) {
		return getAppropriate(key).put(key, value);
	}

	public String remove (String key) {
		return getAppropriate(key).remove(key);
	}

	public int rollback () {
		throw new UnsupportedOperationException();
	}

	public int size () {
		throw new UnsupportedOperationException();
	}

	public int commit () {
		int result = 0;
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			File subdirectory = new File(directory, dirNumber + ".dir");
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				result += database[dirNumber][fileNumber].commit();
			}
			String[] list = subdirectory.list();
			if (list != null && list.length == 0) {
				subdirectory.delete();
			}
		}
		return result;
	}
}