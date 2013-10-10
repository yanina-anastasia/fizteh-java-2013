package ru.fizteh.fivt.students.demidov.shell;

public class Pwd implements BasicCommand {
	public void executeCommand(String[] arguments) {    
		System.out.println(Shell.getCurrentDirectory());
	}
}
