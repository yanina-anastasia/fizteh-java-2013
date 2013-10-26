package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.demidov.filemap.FileMap;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.Utils;

public class FilesMap {	
	public FilesMap(String newDirectoryPath) throws IOException { 
		baseFileMaps = new HashMap<String, FileMap>();
		
		if (new File(newDirectoryPath).isDirectory()) {
			directoryPath = newDirectoryPath;
		} else {
			throw new IOException("wrong directory");
		}
	}
	
	public FileMap getFileMapForKey(String key) throws IOException {
		Integer ndirectory = MultiFileMapUtils.getNDirectory(key.hashCode());
		Integer nfile = MultiFileMapUtils.getNFile(key.hashCode());
		String baseFileKey = MultiFileMapUtils.makeKey(ndirectory, nfile);
		if (baseFileMaps.get(baseFileKey) == null) {
			baseFileMaps.put(baseFileKey, new FileMap(directoryPath + File.separator + ndirectory.toString() + ".dir" + File.separator + nfile.toString() + ".dat"));
		}
		return baseFileMaps.get(baseFileKey);
	}
	
	public void clearFilesMapDirectory() throws IOException {
		for (String subdirectory: (new File(directoryPath)).list()) {
			Utils.deleteFileOrDirectory(new File(directoryPath, subdirectory));
		}	
	}
	
	public void readData(Shell usedShell) throws IOException {	
		for (String subdirectoryName : (new File(directoryPath)).list()) {
			File subdirectory = new File(directoryPath, subdirectoryName);
			if ((!(subdirectory.isDirectory())) || (!(subdirectoryName.matches("([0-9]|1[0-5])[.]dir")))) {
				throw new IOException("wrong subdirectory " + subdirectory.getPath());
			} else {
				for (String baseFileName : subdirectory.list()) {
					File baseFile = new File(subdirectory, baseFileName);
					if ((!(baseFile.isFile())) || (!(baseFileName.matches("([0-9]|1[0-5])[.]dat")))) {
						throw new IOException("wrong baseFile " + baseFile.getPath());
					} else {
						int ndirectory = MultiFileMapUtils.getNumber(subdirectoryName);
						int nfile = MultiFileMapUtils.getNumber(baseFileName);
						String key = MultiFileMapUtils.makeKey(ndirectory, nfile);
						baseFileMaps.put(key, new FileMap(directoryPath + File.separator + subdirectoryName + File.separator + baseFileName)); 
						baseFileMaps.get(key).readDataFromFile();
					}
				}
			}
		}
	}

	public void writeData(Shell usedShell) throws IOException {	
		clearFilesMapDirectory();
		
		for (String key: baseFileMaps.keySet()) {
			FileMap baseFileMap = baseFileMaps.get(key);			
			if (baseFileMap.getCurrentTable().isEmpty()) {
				return;
			}
			
			String ndirectory = key.substring(0, key.indexOf(" "));
			String nfile = key.substring(key.indexOf(" ") + 1);
			
			File currentDirectory = new File(directoryPath + File.separator + ndirectory + ".dir");
			
			if (!currentDirectory.exists()) {			
				if (!currentDirectory.mkdir()) {
					throw new IOException("unable to create " + currentDirectory.getPath());
				}
			}
			
			File currentFile = new File(currentDirectory.getPath() + File.separator + nfile + ".dat");
						
			if (!currentFile.exists()) {			
				if (!currentFile.createNewFile()) {
					throw new IOException("unable to create " + currentFile.getPath());
				}
			}	
			
			baseFileMap.writeDataToFile();
		}
	}

	private Map<String, FileMap> baseFileMaps;
	private String directoryPath;
}
