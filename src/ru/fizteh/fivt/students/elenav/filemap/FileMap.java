package ru.fizteh.fivt.students.elenav.filemap;

import java.io.File;
import java.io.IOException;

public class FileMap {
	
	public static void main(String[] args) throws IOException {
		String property = System.getProperty("fizteh.db.dir");
		File in = new File(property, "db.dat");
		if (!in.exists()) {
			System.err.println("File doesn't exist, but I will create it for you");
			in.createNewFile();
		}
		FileMapState fileMap = new FileMapState(in, "My first table", System.out);
		fileMap.readFile();
		if (args.length == 0) {
			fileMap.interactiveMode();
		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : args) {
				sb.append(s);
				sb.append(" ");
			}
			String monoString = sb.toString(); 
			
			monoString = monoString.trim();
			String[] commands = monoString.split("\\s*;\\s*");
			for (String command : commands) {
				try {
					fileMap.execute(command);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		}
		fileMap.writeFile();
		System.exit(0);
	}

}
