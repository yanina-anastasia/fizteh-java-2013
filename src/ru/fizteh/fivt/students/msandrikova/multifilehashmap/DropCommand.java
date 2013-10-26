package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DropCommand extends Command {

	public DropCommand() {
		super("drop", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		
		try {
			if(myShell.getState().getCurrentTable() != null && myShell.getState().getCurrentTable().getName().equals(argumentsList[1])) {
				myShell.getState().setCurrentTable(null);
			}
			myShell.getState().getTableProvider().removeTable(argumentsList[1]);
			
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
			return;
		} catch (IllegalStateException e) {
			System.out.println(argumentsList[1] + " not exists");
		}
		
		System.out.println("dropped");

	}

}
