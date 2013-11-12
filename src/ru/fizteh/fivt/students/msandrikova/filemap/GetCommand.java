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
		if(shell.getState().currentTable == null && (shell.getState().isMultiFileHashMap || shell.getState().isStoreable)) {
			System.out.println("no table");
			return;
		}
		String value = null;
		if(!shell.getState().isStoreable) {
			value = shell.getState().currentTable.get(key);
		} else {
			try {
				value = shell.getState().storeableTableProvider.serialize(shell.getState().currentStoreableTable, shell.getState().currentStoreableTable.get(key));
			} catch (IllegalArgumentException e) {
				System.out.println("wrong type (" + e.getMessage() + ")");
				return;
			}
		}
		
		if(value == null){
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}	
	}
}
