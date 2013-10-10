package ru.fizteh.fivt.students.demidov.shell;

public class Exit implements BasicCommand {
	public void executeCommand(String[] arguments) throws InterruptionException {    
		System.out.println("Succesful exited. Gl");
		throw new InterruptionException();
	}
}
