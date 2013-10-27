package ru.fizteh.fivt.students.mishatkin.shell;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Vladimir Mishatkin on 10/18/13
 */
public abstract class ShellCommand implements Command {
	private static int inputArgumentsCount;
	protected String[] args = new String[2];
	protected ShellReceiver receiver;

	protected ShellCommand(ShellReceiver receiver) {	//	always implement this with super(receiver) in subclasses!
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

	@Override
	public void execute() throws ShellException {
//		System.err.println(getClass().getName());
		Method receiverMethod = null;
		String methodName = getClass().getName();
		methodName = methodName.substring(methodName.lastIndexOf(".") + 1, methodName.length());
		methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1);
		try {
			switch (getArgumentsCount()) {
			case 0:
				receiverMethod = receiver.getClass().getMethod(methodName);
				receiverMethod.invoke(receiver);
				break;
			case 1:
				receiverMethod = receiver.getClass().getMethod(methodName, String.class);
				receiverMethod.invoke(receiver, args[0]);
				break;
			case 2:
				receiverMethod = receiver.getClass().getMethod(methodName, String.class, String.class);
				receiverMethod.invoke(receiver, args[0], args[1]);
				break;
			}
		} catch (NoSuchMethodException e) {
			throw new ShellException(methodName.substring(0, methodName.indexOf("Command") + 1) + ": invalid command.");
		} catch (IllegalAccessException | InvocationTargetException e) {
		}
	}
}
