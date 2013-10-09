package ru.fizteh.fivt.students.msandrikova.shell;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		Shell.isExit = true;
	}
}
