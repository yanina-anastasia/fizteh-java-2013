package ru.fizteh.fivt.students.msandrikova.shell;


public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}
	
	@Override
	public void execute(String[] argumentsList, Shell shell) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
			return;
		}
		if(shell.getState().currentTable != null) {
			shell.getState().currentTable.commit();
		}
		
		Thread.currentThread().interrupt();
	}
}
