package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class UseCommand extends Command {

	public UseCommand() {
		super("use", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		ChangesCountingTable currentTable = null;
		String name = argumentsList[1];
		
		try {
			currentTable = shell.getState().tableProvider.getTable(name);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), shell.getIsInteractive());
			return;
		}
		
		if(currentTable == null) {
			System.out.println(name + " not exists");
		} else {
			if(shell.getState().currentTable != null && shell.getState().currentTable.unsavedChangesCount() != 0) {
				Utils.generateAnError(shell.getState().currentTable.unsavedChangesCount() + " unsaved changes", this.getName(), shell.getIsInteractive());
				return;
			}
			shell.getState().currentTable = currentTable;
			System.out.println("using " + name);
		}
	}

}
