package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;
public class MyTableProviderFactory implements TableProviderFactory{
	
	public TableProvider create(String directory){
		if ((directory == null) || directory.trim().equals("")) {
			throw new IllegalArgumentException(" Directory cannot be null");
		}
		File directoryFile = new File(directory);
		if (!directoryFile.exists()){
			if (!directoryFile.mkdir()){
				try {
					throw new IllegalArgumentException("Cannot create such directory " + directoryFile.getCanonicalPath());
				} catch (IOException e) {
					throw new RuntimeException("mkdir failed");
				}
					
			}
		}
		if (!directoryFile.isDirectory()){
			throw new IllegalArgumentException("Wrong directory");
		}
		return new DataBaseTable(directory);
	}

	

}
