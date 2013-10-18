package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", 1);
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
		String value;
		if((value = myShell.getMyDBMap().get(argumentsList[1])) == null){
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}
	}

}
