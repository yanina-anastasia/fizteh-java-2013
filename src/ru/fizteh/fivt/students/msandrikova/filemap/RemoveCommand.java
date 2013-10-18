package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class RemoveCommand extends Command {

	public RemoveCommand() {
		super("remove", 1);
	}

	
	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		if(!myShell.getIsFileMap()) {
			myShell.setIsFileMap(true);
			myShell.initMyDBMap();
		}
		if(myShell.getMyDBMap().remove(argumentsList[1]) == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}

}
