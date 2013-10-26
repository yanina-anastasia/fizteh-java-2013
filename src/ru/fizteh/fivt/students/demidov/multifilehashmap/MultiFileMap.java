package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.demidov.filemap.FileMap;
import ru.fizteh.fivt.students.demidov.filemap.FileMapState;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.Utils;

public class MultiFileMap implements FileMapState {
	public MultiFileMap(String root) throws IOException {
		tables = new HashMap<String, FilesMap>();
		
		if (root == null) {
			throw new IOException("problem with property");
		}
		this.root = root;
		
		if (!(new File(root).isDirectory())) {
			throw new IOException("wrong directory");
		}
			
		usedFilesMap = null;
	}
	
	public FileMap getCurrentFileMap(String key) throws IOException {
		return this.getFilesMap().getFileMapForKey(key);
	}
	
	public void changeUsedFilesMap(String newTableName) throws IOException {
		FilesMap newUsedFilesMap = tables.get(newTableName);
		if (newUsedFilesMap == null) {
			throw new IOException(newTableName + " not exists");
		} else {
			usedFilesMap = newUsedFilesMap;			
		}
	}
	
	public void addTable(String newTableName) throws IOException {
		if (tables.containsKey(newTableName)) {
			throw new IOException(newTableName + " exists");
		} else {
			if (!(new File(root, newTableName)).mkdir()) {
				throw new IOException("unable to make directory " + newTableName);
			}
			tables.put(newTableName, new FilesMap(root + File.separator + newTableName));
		}
	}
	
	public void deleteTable(String tableName) throws IOException {
		if (!(tables.containsKey(tableName))) {
			throw new IOException(tableName + " not exists");
		} else {
			Utils.deleteFileOrDirectory(new File(root, tableName));
			if (tables.get(tableName) == usedFilesMap) {
				usedFilesMap = null;
			}
			tables.remove(tableName);
		}
	}
	
	public FilesMap getFilesMap() throws IOException{
		if (usedFilesMap == null) {
			throw new IOException("no table");
		} else {
			return usedFilesMap;
		}
	}
	
	public void readFilesMaps(Shell usedShell) throws IOException {
		for (String subdirectory : (new File(root)).list()) {
			if (!((new File(root, subdirectory)).isDirectory())) {
				throw new IOException("wrong directory " + subdirectory);
			} else {
				tables.put(subdirectory, new FilesMap(root + File.separator + subdirectory));
				tables.get(subdirectory).readData(usedShell);
			}
		}
	}
	
	public void writeFilesMaps(Shell usedShell) throws IOException {
		for (String key: tables.keySet()) {
			tables.get(key).writeData(usedShell);
		}		
	}
	
	private Map<String, FilesMap> tables;
	private FilesMap usedFilesMap;
	private String root;
}
