package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class UseCommand extends Command {

	public UseCommand() {
		super("use", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		
		Table currentTable = null;
		
		try {
			currentTable = myShell.getState().getTableProvider().getTable(argumentsList[1]);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
		}
		
		if(currentTable == null) {
			System.out.println(argumentsList[1] + " not exists");
		} else {
			if(myShell.getState().getCurrentTable() != null) {
				myShell.getState().getCurrentTable().commit();
			}
			myShell.getState().setCurrentTable(currentTable);
			System.out.println("using " + argumentsList[1]);
		}
	}

}
