package ru.fizteh.fivt.students.elenav.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;


public class FileMapState implements Table {
	private final String name;
	private final File currentFile;
	private final PrintStream stream;
	
	FileMapState(File f, String n, PrintStream s) {
		currentFile = f;
		name = n;
		stream = s;
	}
	
	HashMap<String, String> map = new HashMap<>();

	public void readFile() throws IOException {
		DataInputStream s = new DataInputStream(new FileInputStream(currentFile));
		boolean flag = true;
		do {
			try {
				s.readInt();
				s.readInt();
				String key = s.readUTF();
				String value = s.readUTF();
				map.put(key, value);
			} catch (EOFException e) {
				break;
			}
		} while (flag);
		s.close();
	}
	
	public void writeFile() throws IOException {
		DataOutputStream s = new DataOutputStream(new FileOutputStream(currentFile));
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> element : set) {
			String key = element.getKey();
			String value = element.getValue();
			s.writeInt(key.length());
			s.writeInt(value.length());
			s.writeUTF(key);
			s.writeUTF(value);
		}
		s.close();
	}
	
	public void interactiveMode() {
		String command = "";
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		do {
			command = sc.nextLine();
			command = command.trim();
			String[] commands = command.split("\\s*;\\s*");
			for (String c : commands) {
				try {
					execute(c);
				}
				catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} while(flag);
	}
	
	public void execute(String command) throws IOException {
		String[] args = command.split("\\s+");
		switch (args[0]) {
		case "put":
			if (args.length != 3) {
				throw new IOException("Invalid number of arguments");
			} else {
				put(args[1], args[2]);
			}
			break;
			
		case "get":
			if (args.length != 2) {
				throw new IOException("Invalid number of arguments");
			} else {
				get(args[1]);
			}
			break;
			
		case "remove":
			if (args.length != 2) {
				throw new IOException("Invalid number of arguments");
			} else {
				remove(args[1]);
			}
			break;
			
		case "exit": 
			System.exit(0);
			break;
			
		default: 
			throw new IOException("Invalid input");
			
		}
	}

	public String getName() {
		return name;
	}

	public String get(String key) {
		if (map.containsKey(key)) {
			stream.println("found");
			stream.println(map.get(key));
		}
		else {
			stream.println("not found");
		}
		return null;
	}

	public String put(String key, String value) {
		String result = map.put(key, value);
		if (result != null) {
			stream.println("overwrite");
			stream.println(result);
		}
		else {
			stream.println("new");
		}
		return null;
	}

	public String remove(String key) {
		if (map.containsKey(key)) {
			map.remove(key);
			stream.println("removed");
		} else {
			stream.println("not found");
		}
		return null;
	}

	public int size() {
		return map.size();
	}

	public int commit() {
		return 0;
	}

	public int rollback() {
		return 0;
	}
	
}
