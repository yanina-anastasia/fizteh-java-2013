package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.filemap.DataBaseReader;
import ru.fizteh.fivt.students.vlmazlov.filemap.StorageNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class MultiFileMapReader {
	private final File root;
	private final FileMap fileMap;
	private static final int FILES_QUANTITY = 16;
	private static final int DIRECTORIES_QUANTITY = 16;

	public MultiFileMapReader(FileMap fileMap, File root) {
		this.root = root;
		this.fileMap = fileMap;
	}

	private int getNum(File file) {
		String tokens[] = file.getName().split("\\.");
		return Integer.parseInt(tokens[0]);
	}

	private void addTablePart(FileMap tablePart) {
		Iterator<Map.Entry<String, String>> it = tablePart.getEntriesIterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			fileMap.put(entry.getKey(), entry.getValue());
		}
	}

	private void checkKeys(FileMap tablePart, File file, File directory) throws ValidityCheckFailedException {
		Iterator<Map.Entry<String, String>> it = tablePart.getEntriesIterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();

			ValidityChecker.checkKeyStorageAffiliation(entry.getKey(), getNum(file), getNum(directory), 
				FILES_QUANTITY, DIRECTORIES_QUANTITY);
		}
	}

	public void read() throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiFileMapRoot(root);

		FileMap[][] tableParts = new FileMap[DIRECTORIES_QUANTITY][FILES_QUANTITY];
		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				tableParts[i][j] = new FileMap();
			}
		}

		for (File directory : root.listFiles()) {

			ValidityChecker.checkMultiFileStorageName(directory, DIRECTORIES_QUANTITY);

			for (File file : directory.listFiles()) {
				ValidityChecker.checkMultiFileStorageName(file, FILES_QUANTITY);

				DataBaseReader reader = new DataBaseReader(directory, file, tableParts[getNum(directory)][getNum(file)]);
				reader.read();

				checkKeys(tableParts[getNum(directory)][getNum(file)], file, directory);
			}
		}

		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				addTablePart(tableParts[i][j]);
			}
		}
	}
}