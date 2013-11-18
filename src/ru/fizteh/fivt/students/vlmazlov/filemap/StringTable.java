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
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.StringTableProvider;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.TableReader;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.TableWriter;

public class StringTable extends GenericTable<String> implements DiffCountingTable, Cloneable {

	private StringTableProvider specificProvider;

	public StringTable(StringTableProvider provider, String name) {
		super(provider, name);
		specificProvider = provider;
	}

	public StringTable(StringTableProvider provider, String name, boolean autoCommit) {
		super(provider, name, autoCommit);
		specificProvider = provider;
	}

	public void read(String root, String fileName) 
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		}
 
		TableReader.readTable(new File(root), new File(root, fileName), this, specificProvider);
	}

	public void write(String root, String fileName)
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		} 

		TableWriter.writeTable(new File(root), new File(root, fileName), this, specificProvider);
	}

	@Override
	public StringTable clone() {
        return new StringTable(specificProvider, getName(), autoCommit);
    }
}