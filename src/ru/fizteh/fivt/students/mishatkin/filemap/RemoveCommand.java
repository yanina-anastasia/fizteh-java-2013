package ru.fizteh.fivt.students.mishatkin.filemap;
import ru.fizteh.fivt.students.mishatkin.shell.*;
/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class RemoveCommand extends ShellCommand {
	public RemoveCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}

//	@Override
//	public void execute() throws ShellException {
//		receiver.removeCommand(args[0]);
//	}
}
