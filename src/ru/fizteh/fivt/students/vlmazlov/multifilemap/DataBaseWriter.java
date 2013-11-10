package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.util.Map;

public class DataBaseWriter {

	private static final int FILES_QUANTITY = 16;
	private static final int DIRECTORIES_QUANTITY = 16;

	public static void writeMultiTableDataBase(FileMapProvider multiTableDataBase) throws IOException, ValidityCheckFailedException {

		String rootPath = multiTableDataBase.getRoot();

		ValidityChecker.checkMultiTableRoot(rootPath);

		File root = new File(rootPath);

		for (File entry : root.listFiles()) {
			
			FileMap curTable = multiTableDataBase.getTable(entry.getName());

			if (curTable == null) {
				throw new IOException(entry.getName() + " doesn't match any database");
			}

			//Autocommit is performed before writing
			curTable.commit();

			writeMultiFileMap(curTable, entry);
		}
	}
	
	private static void splitFileMap(FileMap[][] tableParts, FileMap toSplit) throws ValidityCheckFailedException {
		for (Map.Entry<String, String> entry : toSplit) {
			
			ValidityChecker.checkFileMapKey(entry.getKey());

			tableParts[Math.abs(entry.getKey().getBytes()[0]) % DIRECTORIES_QUANTITY]
			[Math.abs(entry.getKey().getBytes()[0]) / FILES_QUANTITY % FILES_QUANTITY].put(entry.getKey(), entry.getValue());
		}

		//only commited changes will be written to the disc
		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				tableParts[i][j].commit();
			}
		}

	}

	public static void writeMultiFileMap(FileMap fileMap, File root) throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiFileMapRoot(root);

		FileMap[][] tableParts = new FileMap[DIRECTORIES_QUANTITY][FILES_QUANTITY];
		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				tableParts[i][j] = new FileMap(null);
			}
		}

		splitFileMap(tableParts, fileMap);

		for (int i = 0;i < DIRECTORIES_QUANTITY;++i) {
			File directory = new File(root, i + ".dir");

			if (!directory.exists()) {

				if (!directory.mkdir()) {
					throw new IOException("Unable to create directory "  + directory.getName());
				}
			}
			
			for (int j = 0;j < FILES_QUANTITY;++j) {
				writeFileMap(directory, new File(directory, j + ".dat"), tableParts[i][j]);
			}
		}

		dumpGarbage(root);
	}

	private static int countFirstOffSet(FileMap fileMap) throws IOException {
		int curOffset = 0;

		for (Map.Entry<String, String> entry : fileMap) {
			curOffset += entry.getKey().getBytes("UTF-8").length + 1 + 4;
		}

		return curOffset;

	}

	private static void storeKey(RandomAccessFile dataBaseStorage, String key, int offSet) throws IOException {
		dataBaseStorage.write(key.getBytes("UTF-8"));
		dataBaseStorage.writeByte('\0');
		dataBaseStorage.writeInt(offSet);
	}

	public static void writeFileMap(String root, String storage, FileMap fileMap) throws IOException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}
		if (storage == null) {
			throw new FileNotFoundException("Storage file not specified");
		}

		File rootFile = new File(root), storageFile = new File(rootFile, storage);
		writeFileMap(rootFile, storageFile, fileMap);
	}

	public static void writeFileMap(File root, File storage, FileMap fileMap) throws IOException {	
		
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		storage.delete();

		RandomAccessFile dataBaseStorage = new RandomAccessFile(storage, "rw");

		try {

			long curOffset = countFirstOffSet(fileMap), writePosition;

			for (Map.Entry<String, String> entry : fileMap) {
				if (entry.getValue() == null) {
					continue;
				}

				storeKey(dataBaseStorage, entry.getKey(), (int)curOffset);
				writePosition = dataBaseStorage.getFilePointer();

				dataBaseStorage.seek(curOffset);
				dataBaseStorage.write(entry.getValue().getBytes("UTF-8"));
				curOffset = dataBaseStorage.getFilePointer();
				
				dataBaseStorage.seek(writePosition);
			}

		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}

		if (storage.length() == 0) {
			storage.delete();
		}
	}

	private static void dumpGarbage(File root) {
		for (File directory : root.listFiles()) {
			for (File file : directory.listFiles()) {
				if (file.length() == 0) {
					file.delete();
				}
			}

			if (directory.listFiles().length == 0) {
				directory.delete();
			}
		}

	}
}