package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */
import java.util.*;

public abstract class Command {
	private static  final Map<CommandType, Integer> inputArgumentsCount;
	static {
		Map<CommandType, Integer> aMap = new HashMap<CommandType, Integer>();
		aMap.put(CommandType.CD, 1);
		aMap.put(CommandType.MKDIR, 1);
		aMap.put(CommandType.PWD, 0);
		aMap.put(CommandType.RM, 1);
		aMap.put(CommandType.CP, 2);
		aMap.put(CommandType.MV, 2);
		aMap.put(CommandType.DIR, 0);
		aMap.put(CommandType.EXIT, 0);
		inputArgumentsCount = Collections.unmodifiableMap(aMap);
	}

	protected CommandType type;
	protected String[] args = new String[2];
	protected CommandReceiver receiver;

	public CommandType getType() {
		return type;
	}

	Command(CommandReceiver receiver) {
		this.receiver = receiver;
	}

	public static Command createCommand(ArrayList<String> buffer) throws ShellException {
		if (buffer.isEmpty()) {
			return null;
		}
		Command retValue =  null;
		String commandName = buffer.get(0);
		ShellReceiver receiver = ShellReceiver.sharedInstance();
		CommandType theType;
		try {
			theType = CommandType.valueOf(commandName.toUpperCase());
		} catch (IllegalArgumentException e) {
			buffer.removeAll(buffer);
			String enumName = "CommandType.";
			String type = e.getMessage().substring( e.getMessage().indexOf(enumName) + enumName.length()).toLowerCase();
			throw new ShellException("Invalid command: \'" + type + "\'.");
		}
		switch (theType) {
			case CD:
				retValue = new ChangeDirectoryCommand(receiver);
				break;
			case MKDIR:
				retValue = new MakeDirectoryCommand(receiver);
				break;
			case PWD:
				retValue = new PrintWorkingDirectoryCommand(receiver);
				break;
			case RM:
				retValue = new RemoveCommand(receiver);
				break;
			case CP:
				retValue = new CopyCommand(receiver);
				break;
			case MV:
				retValue = new MoveCommand(receiver);
				break;
			case DIR:
				retValue = new DirectoryCommand(receiver);
				break;
			case EXIT:
				retValue = new ExitCommand(receiver);
				break;
		}
		if (retValue != null) {
			readArgs(retValue, buffer);
		}
		return retValue;
	}

	public abstract void execute() throws ShellException;

	private static void readArgs(Command command, ArrayList<String> buffer) throws  ShellException {
		for (int argumentIndex = 0; argumentIndex < inputArgumentsCount.get(command.getType()); ++argumentIndex) {
			if (argumentIndex + 1 >= buffer.size()) {
				throw new ShellException("Not enough arguments for command \'"
						+ command.type.toString().toLowerCase() + "\'.");
			}
			command.args[argumentIndex] = buffer.get(argumentIndex + 1);
		}
	}

}
