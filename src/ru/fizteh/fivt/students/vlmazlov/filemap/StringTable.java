package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.StringTableProvider;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.TableReader;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.TableWriter;

public class StringTable extends GenericTable<String> implements DiffCountingTable, Cloneable {

	public StringTable(String name) {
		super(name);
	}

	public StringTable(String name, boolean autoCommit) {
		super(name, autoCommit);
	}

	public void read(String root, String fileName) 
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		} 

		File tempDir = FileUtils.createTempDir("readprovider", null);

		if (tempDir == null) {
			throw new FileNotFoundException("Unable to create a temporary directory");
		}
 
		TableReader.readTable(new File(root), new File(root, fileName), this, new StringTableProvider(tempDir.getPath(), true));
	}

	public void write(String root, String fileName)
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		} 

		File tempDir = FileUtils.createTempDir("writeprovider", null);

		if (tempDir == null) {
			throw new FileNotFoundException("Unable to create a temporary directory");
		}

		TableWriter.writeTable(new File(root), new File(root, fileName), this, new StringTableProvider(tempDir.getPath(), true));
	}

	@Override
	public StringTable clone() {
        return new StringTable(getName(), autoCommit);
    }
}