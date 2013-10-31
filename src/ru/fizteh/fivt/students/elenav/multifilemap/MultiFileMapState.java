package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

	
	private File getWhereWrite(String key) throws IOException {
		int hashcode = Math.abs(key.hashCode());
		int ndirectory = hashcode % 16;
		int nfile = hashcode / 16 % 16;
		File dir = new File(getWorkingTable().getWorkingDirectory(), ndirectory+".dir");
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new IOException("can't create file");
			}
		}
		File file = new File(dir.getCanonicalPath(), nfile + ".dat");
		if (!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("can't create file");
			}
		}
		return file;
	}
	
	public void write() throws IOException {
		if (getWorkingTable() != null) {
			Set<Entry<String, String>> set = getWorkingTable().map.entrySet();
			for (Entry<String, String> element : set) {
				String key = element.getKey();
				String value = element.getValue();

				File out = getWhereWrite(key);
				DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
				byte[] bkey = key.getBytes(StandardCharsets.UTF_8);
				s.writeInt(bkey.length);
				byte[] bvalue = value.getBytes(StandardCharsets.UTF_8);
				s.writeInt(bvalue.length);
				s.write(bkey);
				s.write(bvalue);
				s.close();
			}
		}
	}
	
	
}

