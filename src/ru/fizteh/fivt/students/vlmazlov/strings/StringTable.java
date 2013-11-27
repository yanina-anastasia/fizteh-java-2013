package ru.fizteh.fivt.students.vlmazlov.strings;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import ru.fizteh.fivt.students.vlmazlov.utils.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.TableReader;
import ru.fizteh.fivt.students.vlmazlov.utils.TableWriter;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable; 

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
	public int commit() {
		try {
			return super.commit();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public StringTable clone() {
        return new StringTable(specificProvider, getName(), autoCommit);
    }

    @Override
    public void checkRoot(File root) throws ValidityCheckFailedException {
    	ValidityChecker.checkMultiTableRoot(root);
    }

    @Override
    protected void storeOnCommit() throws IOException, ValidityCheckFailedException {
    	ProviderWriter.writeMultiTable(this, new File(specificProvider.getRoot(), getName()), specificProvider);
    }
}