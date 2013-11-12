package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class CreateCommand extends Command {

	public CreateCommand() {
		super("create", 1);
	}

	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!shell.getState().isStoreable && !super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		
		if(shell.getState().isStoreable && argumentsList.length - 2 < 1) {
			Utils.generateAnError("Incorrect arguments amount.", this.getName(), shell.getIsInteractive());
			return;
		}
		
		if(shell.getState().isStoreable) {
			for(int i = 3; i < argumentsList.length; ++i) {
				argumentsList[2] += " " + argumentsList[i];
			}
		}
		
		Object newTable = null;
		String name = argumentsList[1];
		
		if(!shell.getState().isStoreable) {
			try {
				newTable = shell.getState().tableProvider.createTable(name);
			} catch (IllegalArgumentException e) {
				System.out.println("wrong type (" + e.getMessage() + ")");
				return;
			}
		} else {
			List<Class<?>> columnTypes;
			try {
				columnTypes = Utils.parseColumnTypes(argumentsList[2]);
			} catch (IOException e1) {
				System.out.println("wrong type (" + e1.getMessage() + ")");
				return;
			}
			try {
				newTable = shell.getState().storeableTableProvider.createTable(name, columnTypes);
			} catch(IOException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), false);
				return;
			} catch (IllegalArgumentException e) {
				System.out.println("wrong type (" + e.getMessage() + ")");
				return;
			}
		}
		
		if(newTable == null) {
			System.out.println(name + " exists");
		} else {
			System.out.println("created");
		}
	}

}
