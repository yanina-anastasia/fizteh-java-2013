package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		FileMap fileMap = new FileMap();
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Get(fileMap));
		usedShell.curShell.loadCommand(new Put(fileMap));
		usedShell.curShell.loadCommand(new Remove(fileMap));
		usedShell.curShell.loadCommand(new Exit());
		
		try {
			fileMap.readDataFromFile(usedShell);
			usedShell.startShell(arguments);
			fileMap.writeDataToFile(usedShell);
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
