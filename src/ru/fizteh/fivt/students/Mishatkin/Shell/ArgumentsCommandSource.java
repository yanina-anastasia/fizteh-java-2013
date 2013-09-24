package ru.fizteh.fivt.students.Mishatkin.Shell;
/**
 * ArgumentsCommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */
public class ArgumentsCommandSource implements CommandSource {
	private int nextArgumentIndex = 0;
	private String[] args;


	public ArgumentsCommandSource(String[] _args) {
		args = _args;
	}

	@Override
	public boolean hasMoreData() {
		return nextArgumentIndex < args.length;
	}

	@Override
	public String nextWord() {
		return args[nextArgumentIndex++];
	}

	@Override
	public Command nextCommand() {
		Command fullCommand = Command.readCommand(this);
		return fullCommand;
	}
}
