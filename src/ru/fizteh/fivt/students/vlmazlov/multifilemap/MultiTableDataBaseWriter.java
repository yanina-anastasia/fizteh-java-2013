package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;
import ru.fizteh.fivt.students.vlmazlov.filemap.DataBaseWriter;
import ru.fizteh.fivt.students.vlmazlov.filemap.StorageNotFoundException;
import java.io.File;
import java.util.Iterator;
import java.io.IOException;

public class MultiTableDataBaseWriter {
	private final File root;
	private final MultiTableDataBase multiTableDataBase;

	public MultiTableDataBaseWriter(MultiTableDataBase multiTableDataBase) {
		root = multiTableDataBase.getRoot();
		this.multiTableDataBase = multiTableDataBase;
	}

	public void write() throws IOException, ValidityCheckFailedException {

		ValidityChecker.checkMultiTableRoot(root);

		for (File entry : root.listFiles()) {
			
			FileMap curTable = multiTableDataBase.getTable(entry.getName());

			if (curTable == null) {
				throw new IOException(entry.getName() + " doesn't match any database");
			}

			MultiFileMapWriter writer = new MultiFileMapWriter(curTable, entry);
			writer.write();
		}
	}
}