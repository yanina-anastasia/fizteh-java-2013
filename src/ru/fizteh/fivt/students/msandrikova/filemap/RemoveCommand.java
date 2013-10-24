package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class RemoveCommand extends Command {

	public RemoveCommand() {
		super("remove", 1);
	}

	
	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		
		String oldValue = null;
		
		if(myShell.getState().getIsFileMap()) {
			if(myShell.getState().getDBMap() == null) {
				myShell.getState().setDBMap(myShell.getCurrentDirectory());
			}
			try {
				oldValue = myShell.getState().getDBMap().remove(argumentsList[1]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
			}
		} else if(myShell.getState().getIsMultiFileHashMap()) {
			if(myShell.getState().getCurrentTable() == null) {
				System.out.println("no table");
				return;
			}
			try {
				oldValue = myShell.getState().getCurrentTable().remove(argumentsList[1]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
			}
		} else {
			Utils.generateAnError("If you want to use this command shell's state should "
					+ "have type isFileMap or isMultiFileHashMap.", this.getName(), myShell.getIsInteractive());
			return;
		}
		

		if(oldValue == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
		
	}

}
