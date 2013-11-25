package ru.fizteh.fivt.students.vlmazlov.storeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.File;
import ru.fizteh.fivt.students.vlmazlov.filemap.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

public class StoreableTable extends GenericTable<Storeable> implements Table, Cloneable {

	private StoreableTableProvider specificProvider;

	private final List<Class<?>> valueTypes;

	public StoreableTable(StoreableTableProvider provider, String name, List<Class<?>> valueTypes) {
		super(provider, name);
		if (valueTypes == null) {
			throw new IllegalArgumentException("Value types not specified");
		}

		specificProvider = provider;
		///questionable
		this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
	}

	public StoreableTable(StoreableTableProvider provider, String name, boolean autoCommit, List<Class<?>> valueTypes) {
		super(provider, name, autoCommit);
		specificProvider = provider;
		this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
	}

	@Override
	public Storeable put(String key, Storeable value) throws ColumnFormatException {
		try {
			ValidityChecker.checkValueFormat(this, value);
		} catch (ValidityCheckFailedException ex) {
			throw new ColumnFormatException(ex.getMessage());
		}

		return super.put(key, value);
	}

	@Override
    public int getColumnsCount() {
    	return valueTypes.size();
    }

    @Override
	public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
    	return valueTypes.get(columnIndex);
    }

    @Override
	public StoreableTable clone() {
        return new StoreableTable(specificProvider, getName(), autoCommit, valueTypes);
    }

    @Override
    protected boolean isValueEqual(Storeable first, Storeable second) {
	   	return specificProvider.serialize(this, first).equals(specificProvider.serialize(this, second));
    }

    @Override
    public void checkRoot(File root) throws ValidityCheckFailedException {
    	ValidityChecker.checkMultiStoreableTableRoot(root);
    }
}