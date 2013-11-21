package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class CreateCommand extends Command {

	public CreateCommand() {
		super("create", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		ChangesCountingTable newTable = null;
		String name = argumentsList[1];
		
		try {
			newTable = shell.getState().tableProvider.createTable(name);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), shell.getIsInteractive());
			return;
		}
		
		if(newTable == null) {
			System.out.println(name + " exists");
		} else {
			System.out.println("created");
		}
	}

}
