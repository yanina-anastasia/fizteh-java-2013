package ru.fizteh.fivt.students.elenav.filemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class FileMapState extends FilesystemState {

	public HashMap<String, String> map = new HashMap<>();
	
	public FileMapState(String n, File wd, PrintStream s) {
		super(n, wd, s);
	}
	
	public void writeFile(File out) throws IOException {
		DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
		Set<Entry<String, String>> set = map.entrySet();
		for (Entry<String, String> element : set) {
			Writer.writePair(element.getKey(), element.getValue(), s);
		}
		s.close();
	}

	@Override
	public String get(String key) {
		return map.get(key);
	}

	@Override
	public String put(String key, String value) {
		return map.put(key, value);
	}

	@Override
	public String remove(String key) {
		return map.remove(key);
	}

	@Override
	public int commit() {
		System.err.println("Command can't be executed");
		return 0;
	}

	@Override
	public int rollback() {
		System.err.println("Command can't be executed");
		return 0;
	}

	@Override
	public int size() {
		System.err.println("Command can't be executed");
		return 0;
	}

	@Override
	public int getNumberOfChanges() {
		System.err.println("Command can't be executed");
		return 0;
	}

	@Override
	public void read() {
		System.err.println("Command can't be executed");
		
	}

	@Override
	public Storeable put(String string, Storeable string2) {
		System.err.println("Command can't be executed");
		return null;
	}
                                                                                                                                                                 

}
