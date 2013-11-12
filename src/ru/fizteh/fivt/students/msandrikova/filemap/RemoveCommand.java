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
		if(shell.getState().currentTable == null && (shell.getState().isMultiFileHashMap || shell.getState().isStoreable)) {
			System.out.println("no table");
			return;
		}
		String oldValue = null;
		if(!shell.getState().isStoreable) {
			oldValue = shell.getState().currentTable.remove(key);
		} else {
			try {
				oldValue = shell.getState().storeableTableProvider.serialize(shell.getState().currentStoreableTable, shell.getState().currentStoreableTable.remove(key));
			} catch (IllegalArgumentException e) {
				System.out.println("wrong type (" + e.getMessage() + ")");
				return;
			}
		}

		if(oldValue == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
		
	}

}
