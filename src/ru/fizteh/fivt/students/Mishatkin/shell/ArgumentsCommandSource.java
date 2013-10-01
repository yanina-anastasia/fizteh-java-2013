package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * ArgumentsCommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */
public class ArgumentsCommandSource extends CommandSource {
	private int nextArgumentIndex = 0;
	private String[] args;

	public ArgumentsCommandSource(String[] args) {
		this.args = args;
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
