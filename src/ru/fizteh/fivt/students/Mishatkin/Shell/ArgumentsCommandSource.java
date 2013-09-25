package ru.fizteh.fivt.students.Mishatkin.Shell;
/**
 * ArgumentsCommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */
public class ArgumentsCommandSource extends CommandSource {
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
	public String nextLine() {
		return args[nextArgumentIndex++];
	}

}
