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
		if(!myShell.getIsFileMap()) {
			myShell.setIsFileMap(true);
			myShell.initMyDBMap();
		}
		if(argumentsList[1].getBytes(StandardCharsets.UTF_8).length >= 10*10*10*10*10*10) {
			Utils.generateAnError("Key length must be less than 1 MB.", this.getName(), myShell.getIsInteractive());
			return;
		}
		if(argumentsList[2].getBytes(StandardCharsets.UTF_8).length >= 10*10*10*10*10*10) {
			Utils.generateAnError("Value length must be less than 1 MB.", this.getName(), myShell.getIsInteractive());
			return;
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
