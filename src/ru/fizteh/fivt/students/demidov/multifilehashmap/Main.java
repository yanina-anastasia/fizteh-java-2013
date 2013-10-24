package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		MultiFileMap multifileMap = null;
		try {
			multifileMap = new MultiFileMap();
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Get(multifileMap));
		usedShell.curShell.loadCommand(new Put(multifileMap));
		usedShell.curShell.loadCommand(new Remove(multifileMap));
		usedShell.curShell.loadCommand(new Create(multifileMap));
		usedShell.curShell.loadCommand(new Drop(multifileMap));
		usedShell.curShell.loadCommand(new Use(multifileMap));
		usedShell.curShell.loadCommand(new Exit());
		
		try {
			multifileMap.readFileMaps();
			usedShell.startShell(arguments);
			multifileMap.writeFileMapsToFile();
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
