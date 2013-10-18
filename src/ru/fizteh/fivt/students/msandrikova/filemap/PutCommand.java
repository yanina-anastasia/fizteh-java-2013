package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class PutCommand extends Command {

	public PutCommand() {
		super("put", 2);
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
		String oldValue;
		if((oldValue = myShell.getMyDBMap().put(argumentsList[1], argumentsList[2])) == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(oldValue);
		}
	}

}
