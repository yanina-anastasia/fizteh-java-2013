package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;

public class FileMapProvider implements DiffCountingTableProvider {
    private Map<String, FileMap> tables;
	private final String root;
    private final boolean autoCommit;

	public FileMapProvider(String root, boolean autoCommit) throws ValidityCheckFailedException {
		ValidityChecker.checkMultiTableRoot(root);
		
        this.root = root;
        tables = new HashMap<String, FileMap>();
        this.autoCommit = autoCommit;
	}
	
    @Override
    public FileMap getTable(String name) {
    	try {
            ValidityChecker.checkMultiFileMapName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	return tables.get(name);
    }

    @Override
    public FileMap createTable(String name) {
    	try {
            ValidityChecker.checkMultiFileMapName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	if (tables.get(name) != null) {
            return null;
        }

        FileMap newTable = new FileMap(name, this.autoCommit);
        tables.put(name, newTable);

        (new File(root, name)).mkdir();

        return newTable;
    }

    @Override
    public void removeTable(String name) {
    	try {
            ValidityChecker.checkMultiFileMapName(name);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    	FileMap oldTable = tables.remove(name);

        if (oldTable == null) {
            throw new IllegalStateException("Table " + name + " doesn't exist");
        } 

        FileUtils.recursiveDelete(new File(root, name));
    }

    public String getRoot() {
        return root;
    }
}