package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;
import java.io.IOException;

public class MultiFileMap {
	
	public static void main(String[] args) throws IOException {
		String property = System.getProperty("fizteh.db.dir");
		File f = new File(property);
		MultiFileMapState multi = new MultiFileMapState("MyFirstMultiFileMap", f, System.out);
		try {
			multi.run(args);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (OutOfMemoryError err) {
			multi.write();
		}
		System.exit(0);
	}
	
}
