package ru.fizteh.fivt.students.demidov.junit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.demidov.multifilehashmap.FilesMap;
import ru.fizteh.fivt.students.demidov.shell.Utils;

public class TableProviderImplementation implements TableProvider {
	public TableProviderImplementation(String root) {	
		tables = new HashMap<String, TableImplementation>();		

		this.root = root;		
		if (!(new File(root).isDirectory())) {
			throw new IllegalArgumentException("wrong directory: " + root);
		}
	}
	
    public TableImplementation getTable(String name) {
    	if ((name == null) || (!(name.matches("\\w+")))) {    	
    		throw new IllegalArgumentException("wrong table name: " + name);    	
    	}    
    	return tables.get(name);
    }

    public TableImplementation createTable(String name) {
    	if ((name == null) || (!(name.matches("\\w+")))) {    	
    		throw new IllegalArgumentException("wrong table name: " + name);    	
    	}    
    	
    	if (tables.containsKey(name)) {
			return null;
		} else {
			if (!(new File(root, name)).mkdir()) {
				throw new IllegalStateException("unable to make directory " + name);
			}
			try {
				tables.put(name, new TableImplementation(new FilesMap(root + File.separator + name), name));
			} catch(IOException catchedException) {
				throw new IllegalStateException(catchedException);
			}
		}
    	return tables.get(name);
    }

    public void removeTable(String name) {
    	if ((name == null) || (!(name.matches("\\w+")))) {    	
    		throw new IllegalArgumentException("wrong table name: " + name);    	
    	}        	
    	if (!(tables.containsKey(name))) {
    		throw new IllegalStateException(name + " not exists");
		}		
    	Utils.deleteFileOrDirectory(new File(root, name));
		tables.remove(name);
    }
    
    public void readFilesMaps() throws IOException {
		for (String subdirectory : (new File(root)).list()) {
			if (!((new File(root, subdirectory)).isDirectory())) {
				throw new IOException("wrong directory " + subdirectory);
			} else {
				tables.put(subdirectory, new TableImplementation(new FilesMap(root + File.separator + subdirectory), subdirectory));
				tables.get(subdirectory).getFilesMap().readData();
			}
		}
	}
	
	public void writeFilesMaps() throws IOException {
		for (String key: tables.keySet()) {
			tables.get(key).getFilesMap().writeData();
		}		
	}
    
    private Map<String, TableImplementation> tables;
    private String root;
}
