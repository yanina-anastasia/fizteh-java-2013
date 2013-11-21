package ru.fizteh.fivt.students.msandrikova.filemap; 

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", 1);
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
		String value = shell.getState().currentTable.get(key);
		
		if(value == null){
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}	
	}
}
