package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;

import java.lang.reflect.Method;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class PutCommand extends ShellCommand {
	public PutCommand(FileMapReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(2);
	}

//	@Override
//	public void execute() {
////		Method q = ((FileMapReceiver)receiver).getClass().getMethod(PutCommand.class.getName(), String.class, String.class );
//		receiver.putCommand(args[0], args[1]);
//	}
}
