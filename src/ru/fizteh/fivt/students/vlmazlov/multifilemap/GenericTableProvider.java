package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;
import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.filemap.GenericTable;
import java.lang.reflect.InvocationTargetException;

public abstract class GenericTableProvider<V, T extends GenericTable<V>> {
    private Map<String, T> tables;
    private final String root;
    protected final boolean autoCommit;

	public GenericTableProvider(String root, boolean autoCommit) throws ValidityCheckFailedException {
        if (root == null) {
            throw new IllegalArgumentException("Directory not specified");
        }
        
		ValidityChecker.checkMultiTableDataBaseRoot(root);
		
        this.root = root;
        tables = new HashMap<String, T>();
        this.autoCommit = autoCommit;
	}
	
    protected abstract T instantiateTable(String name, Object[] args);

    public T getTable(String name) {
    	try {
            ValidityChecker.checkMultiTableName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	return tables.get(name);
    }

    public T createTable(String name, Object[] args) {
    	try {
            ValidityChecker.checkMultiTableName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	if (tables.get(name) != null) {
            return null;
        }

        T newTable = instantiateTable(name, args);

        tables.put(name, newTable);

        (new File(root, name)).mkdir();

        return newTable;
    }

    public void removeTable(String name) {
    	try {
            ValidityChecker.checkMultiTableName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	T oldTable = tables.remove(name);

        if (oldTable == null) {
            throw new IllegalStateException("Table " + name + " doesn't exist");
        } 

        FileUtils.recursiveDelete(new File(root, name));
    }

    public String getRoot() {
        return root;
    }

    public abstract void read() throws IOException, ValidityCheckFailedException;

    public abstract void write() throws IOException, ValidityCheckFailedException;

    abstract public V deserialize(T table, String value) throws ParseException; 
    
    abstract public String serialize(T table, V value);
}