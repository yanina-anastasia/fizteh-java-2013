package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public abstract class FileMapCommand implements Command {
	protected FileMapReceiver receiver;
	protected String[] args;

	public FileMapCommand() {

	}

	public FileMapCommand(FileMapReceiver receiver) {	//	always call super(receiver) in subclasses!
		this.receiver = receiver;
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
