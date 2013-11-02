package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class CreateCommand extends Command {

	public CreateCommand() {
		super("create", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		
		Table newTable = null;
		
		try {
			newTable = myShell.getState().getTableProvider().createTable(argumentsList[1]);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
			return;
		}
		
		if(newTable == null) {
			System.out.println(argumentsList[1] + " exists");
		} else {
			System.out.println("created");
		}
	}

}
