package ru.fizteh.fivt.students.elenav.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.shell.State;

public class FileMapState extends State implements Table {
	
	public FileMapState(String n, File f, PrintStream s) {
		super(n, f, s);
	}
	
	public HashMap<String, String> map = new HashMap<>();
	
	public void readFile() throws IOException {
		DataInputStream s = new DataInputStream(new FileInputStream(getWorkingDirectory()));
		boolean flag = true;
		do {
			try {
				int keyLength = s.readInt();
				int valueLength = s.readInt();
				if (keyLength <= 0 || valueLength <= 0) {
					throw new IOException("Invalid input");
				}
				byte[] tempArr = null;
				s.read(tempArr, 0, keyLength);
				String key = new String(tempArr, StandardCharsets.UTF_8);
				s.read(tempArr, 0, valueLength);
				String value = new String(tempArr, StandardCharsets.UTF_8);
				map.put(key, value);
			} catch (EOFException e) {
				break;
			}
		} while (flag);
		s.close();
	}
	
	public void writeFile() throws IOException {
		DataOutputStream s = new DataOutputStream(new FileOutputStream(getWorkingDirectory()));
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> element : set) {
			String key = element.getKey();
			String value = element.getValue();
			s.writeInt(key.getBytes(StandardCharsets.UTF_8).length);
			s.writeInt(value.getBytes(StandardCharsets.UTF_8).length);
			s.write(key.getBytes(StandardCharsets.UTF_8));
			s.write(value.getBytes(StandardCharsets.UTF_8));
		}
		s.close();
	}

	public String get(String key) {
		GetCommand g = new GetCommand(this);
		g.execute(key.split("\\s+"), getStream());
		return null;
	}

	public String put(String key, String value) {
		PutCommand c = new PutCommand(this);
		String[] args = {key, value};
		c.execute(args, getStream());
		return null;
	}

	public String remove(String key) {
		RemoveCommand c = new RemoveCommand(this);
		c.execute(key.split("\\s+"), getStream());
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

	protected void init() {
		commands.add(new GetCommand(this));
		commands.add(new PutCommand(this));
		commands.add(new RemoveCommand(this));
		commands.add(new ExitCommand(this));
	}
	
}
