package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.filemap.DataBaseWriter;
import ru.fizteh.fivt.students.vlmazlov.filemap.StorageNotFoundException;
import java.io.File;
import java.util.Iterator;
import java.io.IOException;

public class MultiTableDataBaseReader {
	private final File root;
	private final MultiTableDataBase multiTableDataBase;

	public MultiTableDataBaseReader(MultiTableDataBase multiTableDataBase) {
		root = multiTableDataBase.getRoot();
		this.multiTableDataBase = multiTableDataBase;
	}

	public void read() throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiTableRoot(root);

		for (File entry : root.listFiles()) {
			
			multiTableDataBase.create(entry.getName());

			FileMap curTable = multiTableDataBase.getTable(entry.getName());

			MultiFileMapReader reader = new MultiFileMapReader(curTable, entry);
			reader.read();
		}
	}
}