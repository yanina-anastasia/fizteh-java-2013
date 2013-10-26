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

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class FileMapState extends MonoMultiAbstractState implements Table {
	
	public FileMapState(String n, File wd, PrintStream s) {
		super(n, wd, s);
		setWorkingTable(this);
	}
	
	public HashMap<String, String> map = new HashMap<>();
	
	public void readFile(File in) throws IOException {
		DataInputStream s = new DataInputStream(new FileInputStream(in));
        boolean flag = true;
        do {
                try {
                        int keyLength = s.readInt();
                        int valueLength = s.readInt();
                        if (keyLength <= 0 || valueLength <= 0 || keyLength >= 1024*1024 || valueLength >= 1024*1024) {
                                throw new IOException("Invalid input");
                        }
                        byte[] tempKey = new byte[keyLength];
                        s.read(tempKey);
                        String key = new String(tempKey, StandardCharsets.UTF_8);
                        byte[] tempValue = new byte[valueLength];
                        s.read(tempValue);
                        String value = new String(tempValue, StandardCharsets.UTF_8);
                        map.put(key, value);
                } catch (EOFException e) {
                        break;
                }
        } while (flag);
        s.close();
	
	}
	
	public void writeFile(File out) throws IOException {
		DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> element : set) {
			String key = element.getKey();
			String value = element.getValue();
			byte[] bkey = key.getBytes(StandardCharsets.UTF_8);
			s.writeInt(bkey.length);
			byte[] bvalue = value.getBytes(StandardCharsets.UTF_8);
			s.writeInt(bvalue.length);
			s.write(bkey);
			s.write(bvalue);
		}
		s.close();
	}

	public String get(String key) {
		GetCommand g = new GetCommand(this);
		String[] args = {"get", key};
		g.execute(args, getStream());
		return null;
	}                                                                                                                                                                                      

	public String put(String key, String value) {
		PutCommand c = new PutCommand(this);
		String[] args = {"put", key, value};
		c.execute(args, getStream());
		return null;
	}

	public String remove(String key) {
		RemoveCommand c = new RemoveCommand(this);
		String[] args = {"remove", key};
		c.execute(args, getStream());
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
