package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import ru.fizteh.fivt.students.vlmazlov.shell.FileOperationFailException;
import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.util.Map;

public class DataBaseReader {
	private static final int FILES_QUANTITY = 16;
	private static final int DIRECTORIES_QUANTITY = 16;

	private static int getNum(File file) {
		String tokens[] = file.getName().split("\\.");
		return Integer.parseInt(tokens[0]);
	}

	private static void addTablePart(FileMap tablePart, FileMap whole) {

		for (Map.Entry<String, String> entry : tablePart) {
			whole.put(entry.getKey(), entry.getValue());
		}
	}

	private static void checkKeys(FileMap tablePart, File file, File directory) throws ValidityCheckFailedException {
		for (Map.Entry<String, String> entry : tablePart) {	
			ValidityChecker.checkKeyStorageAffiliation(entry.getKey(), getNum(file), getNum(directory), 
				FILES_QUANTITY, DIRECTORIES_QUANTITY);
		}
	}

	private static String readUTFString(RandomAccessFile dataBaseStorage, int readingPosition, int length) throws IOException {
		byte[] bytes = new byte[length];

		dataBaseStorage.seek(readingPosition);
		dataBaseStorage.read(bytes);
		return new String(bytes, "UTF-8");
	}

	public static void readMultiTableDataBase(FileMapProvider multiTableDataBase) 
	throws IOException, ValidityCheckFailedException {

		String rootPath = multiTableDataBase.getRoot();

		ValidityChecker.checkMultiTableRoot(rootPath);
		
		File root = new File(rootPath);

		for (File entry : root.listFiles()) {
			
			multiTableDataBase.createTable(entry.getName());

			FileMap curTable = multiTableDataBase.getTable(entry.getName());

			readMultiFileMap(curTable, entry);

			//read data has to be preserved
			curTable.commit();
		}
	}

	public static void readMultiFileMap(FileMap fileMap, File root) throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiFileMapRoot(root);

		FileMap[][] tableParts = new FileMap[DIRECTORIES_QUANTITY][FILES_QUANTITY];
		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				tableParts[i][j] = new FileMap(null);
			}
		}

		for (File directory : root.listFiles()) {

			ValidityChecker.checkMultiFileStorageName(directory, DIRECTORIES_QUANTITY);

			for (File file : directory.listFiles()) {
				ValidityChecker.checkMultiFileStorageName(file, FILES_QUANTITY);

				readFileMap(directory, file, tableParts[getNum(directory)][getNum(file)]);

				checkKeys(tableParts[getNum(directory)][getNum(file)], file, directory);
			}
		}

		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				//iterating is only possible over commited entries
				tableParts[i][j].commit();
				addTablePart(tableParts[i][j], fileMap);
			}
		}
	}

	public static void readFileMap(File root, File storage, FileMap fileMap) 
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		ValidityChecker.checkFileMapRoot(root);

		if ((!storage.exists()) || (storage.length() == 0)) {
			return;
		}

		RandomAccessFile dataBaseStorage = new RandomAccessFile(storage, "r");

		try {
			String key = null;
			int readPosition = 0, initialOffset = -1, prevOffset = -1;

			do {

				dataBaseStorage.seek(readPosition);

				while (dataBaseStorage.getFilePointer() < dataBaseStorage.length()) {
					if (dataBaseStorage.readByte() == '\0') {
						break;
					}
				}

				int keyLen = (int)dataBaseStorage.getFilePointer() - readPosition - 1;

				int curOffset = (int)dataBaseStorage.readInt();		
				
				ValidityChecker.checkFileMapOffset(curOffset);				

				if (prevOffset == -1) {
					initialOffset = curOffset;
				} else {
					String value = readUTFString(dataBaseStorage, prevOffset, curOffset - prevOffset);
					
					ValidityChecker.checkFileMapValue(value);

					fileMap.put(key, value);
				}
				prevOffset = curOffset;	
				//read key		
				key = readUTFString(dataBaseStorage, readPosition, keyLen);	
				ValidityChecker.checkFileMapKey(key);

				readPosition = (int)dataBaseStorage.getFilePointer() + 5;

			} while (readPosition < initialOffset);
			
			String value = readUTFString(dataBaseStorage, prevOffset, (int)dataBaseStorage.length() - prevOffset);
			
			ValidityChecker.checkFileMapValue(value);
			
			fileMap.put(key, value);
		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}
	}

}