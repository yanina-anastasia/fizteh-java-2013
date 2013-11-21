package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DropCommand extends Command {

	public DropCommand() {
		super("drop", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		String name = argumentsList[1];
		
		if(shell.getState().currentTable != null && shell.getState().currentTable.getName().equals(name)) {
			shell.getState().currentTable = null;
		}
		
		try {
			shell.getState().tableProvider.removeTable(name);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), shell.getIsInteractive());
			return;
		} catch (IllegalStateException e) {
			System.out.println(name + " not exists");
			return;
		}
		
		System.out.println("dropped");

	}

}
