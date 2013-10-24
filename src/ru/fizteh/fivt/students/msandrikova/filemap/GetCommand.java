package ru.fizteh.fivt.students.msandrikova.filemap; 

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		
		String value = null;
		
		if(myShell.getState().getIsFileMap()) {
			if(myShell.getState().getDBMap() == null) {
				myShell.getState().setDBMap(myShell.getCurrentDirectory());
			}
			try {
				value = myShell.getState().getDBMap().get(argumentsList[1]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
				return;
			}
		} else if(myShell.getState().getIsMultiFileHashMap()) {
			if(myShell.getState().getCurrentTable() == null) {
				System.out.println("no table");
				return;
			}
			try {
				value = myShell.getState().getCurrentTable().get(argumentsList[1]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
				return;
			}
		} else {
			Utils.generateAnError("If you want to use this command shell's state should "
					+ "have type isFileMap or isMultiFileHashMap.", this.getName(), myShell.getIsInteractive());
			return;
		}
		
		if(value == null){
			System.out.println("not found");
		} else {
				System.out.println("found");
				System.out.println(value);
		}	
	}
}
