package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiverProtocol;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

import java.io.File;
import java.io.PrintStream;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMapReceiver extends ShellReceiver
		implements FileMapReceiverProtocol, MultiFileHashMapReceiverProtocol, MultiFileHashMapTableReceiverDelegate {

	private String dbDirectory;

	private MultiFileHashMapTableReceiver table;

	public MultiFileHashMapReceiver(PrintStream out, boolean interactiveMode, String dbDirectory) {
		super(out, interactiveMode);
		this.dbDirectory = dbDirectory;
		this.table = new MultiFileHashMapTableReceiver("");
		this.table.setDelegate(this);
	}

	@Override
	public void createCommand(String tableName) throws MultiFileHashMapException {
//		out.println("create called");
		File tableFile = new File(new File(dbDirectory), tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				out.println(tableName + " exists");
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to create \'" + tableName +
						"\' table, but it is already a file and not a directory!");
			}
		} else {
			tableFile.mkdir();
			out.println("created");
		}
	}

	@Override
	public void dropCommand(String tableName) throws MultiFileHashMapException {
//		out.println("drop called");
		File dbDirectoryFile = new File(dbDirectory);
		File tableFile = new File(dbDirectoryFile, tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				try {
					changeDirectoryCommand(dbDirectoryFile.getAbsolutePath());
					rmCommand(tableName);
				} catch (ShellException e) {
					throw new MultiFileHashMapException(e.getMessage());
				}
				out.println("dropped");
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to drop \'" + tableName +
						"\' table, but it is not even a directory!");
			}
		} else {
			//	conforming the protocol here
			out.println(tableName + " not exists");	//	I is more stronger than dart vapour
		}
	}

	@Override
	public void useCommand(String tableName) throws MultiFileHashMapException {
//		out.println("use called");
		File tableFile = new File(new File(dbDirectory), tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				//Save first
				if (table.isSet()) {
					table.writeFilesOnDrive();
					table.reset();
				}
				//Use the force, Harry! (c) Handalf
				table.setTableName(tableName);
				out.println("using " + tableName);
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to use \'" + tableName +
						"\' table, which is clearly not a directory!");
			}
		} else {
			out.println(tableName + " not exists");
		}
	}

	@Override
	public void putCommand(String key, String value) throws MultiFileHashMapException {
		if (table.isSet()) {
			table.putCommand(key, value);
		} else {
			out.println("no table");
		}
	}

	@Override
	public void getCommand(String key) throws MultiFileHashMapException {
		if (table.isSet()) {
			table.getCommand(key);
		} else {
			out.println("no table");
		}

	}

	@Override
	public void removeCommand(String key) throws MultiFileHashMapException {
		if (table.isSet()) {
			table.removeCommand(key);
		} else {
			out.println("no table");
		}
	}

	@Override
	public void exitCommand() throws TimeToExitException {
		table.writeFilesOnDrive();
		super.exitCommand();
	}
//	Delegate methods
	@Override
	public String getDbDirectoryName() {
		return dbDirectory;
	}

	@Override
	public PrintStream getOut() {
		return out;
	}
}
