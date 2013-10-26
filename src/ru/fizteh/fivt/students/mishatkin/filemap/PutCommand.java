package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

import java.lang.reflect.Method;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class PutCommand extends ShellCommand {
	public PutCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(2);
	}

//	@Override
//	public void execute() {
////		Method q = ((FileMapReceiver)receiver).getClass().getMethod(PutCommand.class.getName(), String.class, String.class );
//		receiver.putCommand(args[0], args[1]);
//	}
}
