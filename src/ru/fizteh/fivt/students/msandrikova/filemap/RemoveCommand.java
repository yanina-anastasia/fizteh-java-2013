package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class RemoveCommand extends Command {

	public RemoveCommand() {
		super("remove", 1);
	}

	
	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		String key = argumentsList[1];
		if(shell.getState().currentTable == null && shell.getState().isMultiFileHashMap) {
			System.out.println("no table");
			return;
		}
		String oldValue = shell.getState().currentTable.remove(key);
		

		if(oldValue == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
		
	}

}
