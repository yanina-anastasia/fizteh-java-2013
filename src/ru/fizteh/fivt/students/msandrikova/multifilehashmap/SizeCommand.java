package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class SizeCommand extends Command {

	public SizeCommand() {
		super("size", 0);
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
		
		int size = shell.getState().currentTable.size();
		System.out.println(size);
	}

}
