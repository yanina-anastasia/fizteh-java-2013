package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		MultiFileMap multifileMap = null;
		try {
			multifileMap = new MultiFileMap(System.getProperty("fizteh.db.dir"));
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
		
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new MultiGet(multifileMap));
		usedShell.curShell.loadCommand(new MultiPut(multifileMap));
		usedShell.curShell.loadCommand(new MultiRemove(multifileMap));
		usedShell.curShell.loadCommand(new Create(multifileMap));
		usedShell.curShell.loadCommand(new Drop(multifileMap));
		usedShell.curShell.loadCommand(new Use(multifileMap));
		usedShell.curShell.loadCommand(new Exit());
		
		try {
			multifileMap.readFilesMaps(usedShell);
			usedShell.startShell(arguments);
			multifileMap.writeFilesMaps(usedShell);
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
