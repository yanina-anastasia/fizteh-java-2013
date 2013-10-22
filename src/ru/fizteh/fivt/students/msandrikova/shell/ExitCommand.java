package ru.fizteh.fivt.students.msandrikova.shell;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}
	
	@Override
	public void execute(String[] argumentsList, Shell myShell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
			return;
		}
		Thread.currentThread().interrupt();
	}
}
