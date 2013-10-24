package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.demidov.shell.Utils;

public class MultiFileMap {
	public MultiFileMap() throws IOException {
		root = System.getProperty("fizteh.db.dir");
		if (root == null) {
			throw new IOException("problem with property");
		}
		
		if (!(new File(root).isDirectory())) {
			throw new IOException("wrong directory");
		}
		
		usedFileMap = null;
	}
	
	public void changeUsedFileMap(String newTableName) throws IOException {
		FileMap newUsedFileMap = tables.get(newTableName);
		if (newUsedFileMap == null) {
			throw new IOException(newTableName + " not exists");
		} else {
			usedFileMap = newUsedFileMap;			
		}
	}
	
	public void addTable(String newTableName) throws IOException {
		if (tables.containsKey(newTableName)) {
			throw new IOException(newTableName + " exists");
		} else {
			if (!(new File(root, newTableName)).mkdir()) {
				throw new IOException("unable to make directory " + newTableName);
			}
			tables.put(newTableName, new FileMap(root + File.separator + newTableName));
		}
	}
	
	public void deleteTable(String tableName) throws IOException {
		if (!(tables.containsKey(tableName))) {
			throw new IOException(tableName + " not exists");
		} else {
			Utils.deleteFileOrDirectory(new File(root, tableName));
			tables.remove(tableName);
		}
	}
	
	public FileMap getFileMap() throws IOException{
		if (usedFileMap == null) {
			throw new IOException("no table");
		} else {
			return usedFileMap;
		}
	}
	
	public void readFileMaps() throws IOException {
		for (String subdirectory : (new File(root)).list()) {
			if (!((new File(root, subdirectory)).isDirectory())) {
				throw new IOException("wrong directory " + subdirectory);
			} else {
				tables.put(subdirectory, new FileMap(root + File.separator + subdirectory));
				tables.get(subdirectory).readData();
			}
		}
	}
	
	public void writeFileMapsToFile() throws IOException {
		for (String key: tables.keySet()) {
			tables.get(key).writeData();
		}		
	}
	
	private final Map<String, FileMap> tables = new HashMap<String, FileMap>();
	private FileMap usedFileMap;
	private String root;
}
