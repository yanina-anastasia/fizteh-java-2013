package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.FileNotFoundException;
import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.msandrikova.filemap.DBMap;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}
	
	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		if(myShell.getState().getIsFileMap()) {
			if(myShell.getState().getDBMap() != null) {
				DBMap curDBMap = myShell.getState().getDBMap();
				try {
					curDBMap.writeFile();
				} catch (FileNotFoundException e) {
					Utils.generateAnError("Fatal error during writing", "DBMap", false);
				} catch (IOException e) {
					Utils.generateAnError("Fatal error during writing", "DBMap", false);
				}
			}
		}
		if(myShell.getState().getIsMultiFileHashMap()) {
			if(myShell.getState().getCurrentTable() != null) {
				Table currentTable = myShell.getState().getCurrentTable();
				currentTable.commit();
			}
		}
		Thread.currentThread().interrupt();
	}
}
