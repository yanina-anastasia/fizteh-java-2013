package ru.fizteh.fivt.students.demidov.shell;

public class Pwd implements BasicCommand {
	public void executeCommand(String[] arguments, Shell.CurrentShell curShell) {    
		System.out.println(curShell.getCurrentDirectory());
	}
}
