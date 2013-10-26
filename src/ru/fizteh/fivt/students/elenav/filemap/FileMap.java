package ru.fizteh.fivt.students.elenav.filemap;

import java.io.File;
import java.io.IOException;

public class FileMap {
	
	static FileMapState createFileMapState(String name, File in) throws IOException {
		if (!in.exists()) {
			System.err.println("File was created");
			if (!in.createNewFile())
				throw new IOException("Can't create file");
		}
		FileMapState fileMap = new FileMapState(name, in, System.out);
		fileMap.readFile(in);
		return fileMap;
	}
	
	public static void main(String[] args) throws IOException {
		FileMapState fileMap = null;
		String property = System.getProperty("fizteh.db.dir");
		File in = new File(property, "db.dat");
		try {
			fileMap = createFileMapState("My first table", in);
			fileMap.run(args);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (OutOfMemoryError f) {
			fileMap.writeFile(in);
			System.exit(0);
		}
		
	}

}
