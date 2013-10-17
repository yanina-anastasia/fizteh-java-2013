package ru.fizteh.fivt.students.elenav.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.shell.State;
import sun.font.CreatedFontTracker;

public class FileMap {
	
	static FileMapState createFileMapState(String name) throws IOException {
		String property = System.getProperty("fizteh.db.dir");
		File in = new File(property, "db.dat");
		if (!in.exists()) {
			System.err.println("File doesn't exist, but I will create it for you");
			in.createNewFile();
		}
		FileMapState fileMap = new FileMapState(name, in, System.out);
		fileMap.readFile();
		return fileMap;
	}
	
	public static void main(String[] args) throws IOException {
		FileMapState fileMap = createFileMapState("My first table");
		fileMap.run(args);
		fileMap.writeFile();
		System.exit(0);
	}

}
