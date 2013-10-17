package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * Created by Vladimir Mishatkin on 10/18/13
 */
public abstract class ShellCommand implements Command {
	private static int inputArgumentsCount;
	protected String[] args = new String[2];
	protected ShellReceiver receiver;

	ShellCommand(ShellReceiver receiver) {	//	always implement this with super(receiver) in subclasses!
		this.receiver = receiver;
	}

	protected static void setInputArgumentsCount(int newInputArgumentsCount) {
		inputArgumentsCount = newInputArgumentsCount;
	}

	@Override
	public int getArgumentsCount() {
		return inputArgumentsCount;
	}

	@Override
	public String getName() {
		String className = getClass().getName().toLowerCase();
		return className.substring(className.lastIndexOf(".") + 1, className.length() - "Command".length());
	}

	@Override
	public void setArguments(String[] arguments) {
		this.args = arguments;
	}
}
