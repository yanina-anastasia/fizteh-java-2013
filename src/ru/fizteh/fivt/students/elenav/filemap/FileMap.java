package ru.fizteh.fivt.students.elenav.filemap;

import java.io.File;
import java.io.IOException;

public class FileMap {
	
	static FileMapState createFileMapState(String name) throws IOException {
		String property = System.getProperty("fizteh.db.dir");
		File in = new File(property, "db.dat");
		if (!in.exists()) {
			System.err.println("File was created");
			if (!in.createNewFile())
				throw new IOException("Can't create file");
		}
		FileMapState fileMap = new FileMapState(name, in, System.out);
		fileMap.readFile();
		return fileMap;
	}
	
	public static void main(String[] args) throws IOException {
		try {
			FileMapState fileMap = createFileMapState("My first table");
			fileMap.run(args);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.exit(0);
	}

}
