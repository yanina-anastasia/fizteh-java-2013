package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapProvider;

public abstract class MonoMultiAbstractState extends FilesystemState {
	
	public MultiFileMapProvider provider = null;
	
	protected MonoMultiAbstractState(String n, File wd, PrintStream s) {
		super(n, wd, s);
	}

	public abstract String put(String key, String value);

	public abstract String remove(String key);

	public abstract String get(String key);
	
}
