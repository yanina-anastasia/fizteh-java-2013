package ru.fizteh.fivt.students.msandrikova.filemap;

import java.nio.charset.StandardCharsets;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class PutCommand extends Command {

	public PutCommand() {
		super("put", 2);
	}

	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}

		if(argumentsList[1].getBytes(StandardCharsets.UTF_8).length >= 10*10*10*10*10*10) {
			Utils.generateAnError("Key length must be less than 1 MB.", this.getName(), myShell.getIsInteractive());
			return;
		}
		if(argumentsList[2].getBytes(StandardCharsets.UTF_8).length >= 10*10*10*10*10*10) {
			Utils.generateAnError("Value length must be less than 1 MB.", this.getName(), myShell.getIsInteractive());
			return;
		}
		
		String oldValue = null;
		
		if(myShell.getState().getIsFileMap()) {
			if(myShell.getState().getDBMap() == null) {
				myShell.getState().setDBMap(myShell.getCurrentDirectory());
			}
			try {
				oldValue = myShell.getState().getDBMap().put(argumentsList[1], argumentsList[2]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
				return;
			}
		} else if(myShell.getState().getIsMultiFileHashMap()) {
			if(myShell.getState().getCurrentTable() == null) {
				System.out.println("no table");
				return;
			}
			try {
				oldValue = myShell.getState().getCurrentTable().put(argumentsList[1], argumentsList[2]);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), this.getName(), myShell.getIsInteractive());
				return;
			}
		} else {
			Utils.generateAnError("If you want to use this command shell's state should "
					+ "have type isFileMap or isMultiFileHashMap.", this.getName(), myShell.getIsInteractive());
			return;
		}

		if(oldValue == null) {
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(oldValue);
		}

	}

}
