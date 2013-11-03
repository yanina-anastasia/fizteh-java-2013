package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenav.commands.CreateTableCommand;
import ru.fizteh.fivt.students.elenav.commands.DropCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.GetCommand;
import ru.fizteh.fivt.students.elenav.commands.PutCommand;
import ru.fizteh.fivt.students.elenav.commands.RemoveCommand;
import ru.fizteh.fivt.students.elenav.commands.UseCommand;
import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.Shell;
import ru.fizteh.fivt.students.elenav.shell.ShellState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class MultiFileMapState extends MonoMultiAbstractState implements TableProvider {
	
	private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;
	private final ShellState shell;
	
	protected MultiFileMapState(String n, File wd, PrintStream s) {
		super(n, wd, s);
		shell = Shell.createShellState("WorkingShell", wd, s);
	}
	
	protected void init() {
		commands.add(new CreateTableCommand(this));
		commands.add(new DropCommand(this));
		commands.add(new UseCommand(this));
		
		commands.add(new GetCommand(this));
		commands.add(new PutCommand(this));
		commands.add(new RemoveCommand(this));
		commands.add(new ExitCommand(this));
	}
	

	public Table getTable(String name) {
		File f = new File(getWorkingDirectory(), name);
		if (!f.exists()) {
			return null;
		}
		return new FileMapState(name, f, getStream());
	}

	public Table createTable(String name) {
		File f = new File(getWorkingDirectory(), name);
		CreateTableCommand c = new CreateTableCommand(this);
		String[] args = {"create", name};
		c.execute(args, getStream());
		return new FileMapState(name, f, getStream());
	}

	public void removeTable(String name) {
		DropCommand c = new DropCommand(this);
		String[] args = {"drop", name};
		c.execute(args, getStream());
	}

	public ShellState getShell() {
		return shell;
	}

	public void read() throws IOException {
		File[] dirs = getWorkingTable().getWorkingDirectory().listFiles();
		if (dirs != null) {
			for (File file : dirs) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						getWorkingTable().readFile(f);
						f.delete();
					}
				}
				file.delete();
			}
				
		}
		
	}
	
	public void write() throws IOException {
		if (getWorkingTable() != null) {
			for (int i = 0; i < DIR_COUNT; ++i) {
				for (int j = 0; j < FILES_PER_DIR; ++j) {
					Map<String, String> toWriteInCurFile = new HashMap<>();
			
					for (String key : getWorkingTable().map.keySet()) {
						if (getDir(key) == i && getFile(key) == j) {
							toWriteInCurFile.put(key, getWorkingTable().map.get(key));
						}
					}
					
					if (toWriteInCurFile.size() > 0) {
						File out = new File(getWorkingTable().getWorkingDirectory()
								+ File.separator + i + ".dir" + File.separator + j + ".dat");
						DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
						Set<Entry<String, String>> set = toWriteInCurFile.entrySet();
						for (Entry<String, String> element : set) {
							getWorkingTable().writePair(element.getKey(), element.getValue(), s);
						}
						s.close();
					}
				}
		
			}
			getWorkingTable().map.clear();
		}
	}

	private int getDir(String key) throws IOException {
		int hashcode = Math.abs(key.hashCode());
		int ndirectory = hashcode % 16;
		File dir = new File(getWorkingTable().getWorkingDirectory(), ndirectory+".dir");
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new IOException("can't create file");
			}
		}
		return ndirectory;
	}

	private int getFile(String key) throws IOException {
		int hashcode = Math.abs(key.hashCode());
		int ndirectory = hashcode % 16;
		int nfile = hashcode / 16 % 16;
		File dir = new File(getWorkingTable().getWorkingDirectory(), ndirectory+".dir");
		File file = new File(dir.getCanonicalPath(), nfile + ".dat");
		if (!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("can't create file");
			}
		}
		return nfile;
	}
	
	
}

