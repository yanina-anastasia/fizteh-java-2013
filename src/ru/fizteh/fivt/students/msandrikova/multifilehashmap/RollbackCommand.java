package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class RollbackCommand extends Command {

	public RollbackCommand() {
		super("rollback", 0);
	}

	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		if(shell.getState().isMultiFileHashMap && shell.getState().currentTable == null) {
			System.out.println("no table");
			return;
		}
		
		int changesCount = shell.getState().currentTable.rollback();
		System.out.println(changesCount);
	}

}
