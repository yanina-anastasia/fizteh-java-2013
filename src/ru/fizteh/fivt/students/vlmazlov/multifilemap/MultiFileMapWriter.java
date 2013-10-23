package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.filemap.DataBaseWriter;
import ru.fizteh.fivt.students.vlmazlov.filemap.StorageNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class MultiFileMapWriter {
	private final File root;
	private final FileMap fileMap;
	private static final int FILES_QUANTITY = 16;
	private static final int DIRECTORIES_QUANTITY = 16;

	public MultiFileMapWriter(FileMap fileMap, File root) {
		this.root = root;
		this.fileMap = fileMap;
	}

	private void splitFileMap(FileMap[][] tableParts) throws ValidityCheckFailedException {
		Iterator<Map.Entry<String, String>> it = fileMap.getEntriesIterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			
			ValidityChecker.checkFileMapKey(entry.getKey());

			tableParts[entry.getKey().getBytes()[0] % DIRECTORIES_QUANTITY]
			[entry.getKey().getBytes()[0] / FILES_QUANTITY % FILES_QUANTITY].put(entry.getKey(), entry.getValue());
		}

	}

	public void write() throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiFileMapRoot(root);

		FileMap[][] tableParts = new FileMap[DIRECTORIES_QUANTITY][FILES_QUANTITY];
		for (int i = 0;i < tableParts.length;++i) {
			for (int j = 0;j < tableParts[i].length;++j) {
				tableParts[i][j] = new FileMap();
			}
		}

		splitFileMap(tableParts);

		for (int i = 0;i < DIRECTORIES_QUANTITY;++i) {
			File directory = new File(root, i + ".dir");

			if (!directory.exists()) {

				if (!directory.mkdir()) {
					throw new IOException();
				}
			}
			
			for (int j = 0;j < FILES_QUANTITY;++j) {
				
				DataBaseWriter writer = new DataBaseWriter(directory, j + ".dat", tableParts[i][j]);
				writer.write();
			}
		}

		dumpGarbage();
	}

	private void dumpGarbage() {
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