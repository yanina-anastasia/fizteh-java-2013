package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.filemap.Get;
import ru.fizteh.fivt.students.demidov.filemap.Put;
import ru.fizteh.fivt.students.demidov.filemap.Remove;
import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		MultiFileMap multiFileMap = null;
		try {
			multiFileMap = new MultiFileMap(System.getProperty("fizteh.db.dir"));
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
		
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Get(multiFileMap));
		usedShell.curShell.loadCommand(new Put(multiFileMap));
		usedShell.curShell.loadCommand(new Remove(multiFileMap));
		usedShell.curShell.loadCommand(new Create(multiFileMap));
		usedShell.curShell.loadCommand(new Drop(multiFileMap));
		usedShell.curShell.loadCommand(new Use(multiFileMap));
		usedShell.curShell.loadCommand(new Exit());
		
		try {
			multiFileMap.readFilesMaps();
			usedShell.startShell(arguments);
			multiFileMap.writeFilesMaps();
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
