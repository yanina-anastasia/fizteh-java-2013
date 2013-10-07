package ru.fizteh.fivt.students.vlmazlov.shell;

public class PWD extends Command {
	PWD() {
		super("pwd", 0);
	};	

	public void execute(String[] args, Shell.ShellState state) {		
		System.out.println(state.getCurDir());
	}
}