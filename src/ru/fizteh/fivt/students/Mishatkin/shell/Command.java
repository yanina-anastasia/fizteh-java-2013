package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */
import java.util.*;

enum COMMAND_TYPE {
	CD,
	MKDIR,
	PWD,
	RM,
	CP,
	MV,
	DIR,
	EXIT
}

public abstract class Command {
	private static  final Map<COMMAND_TYPE, Integer> inputArgumentsCount;
	static {
		Map<COMMAND_TYPE, Integer> aMap = new HashMap<COMMAND_TYPE, Integer>();
		aMap.put(COMMAND_TYPE.CD, 1);
		aMap.put(COMMAND_TYPE.MKDIR, 1);
		aMap.put(COMMAND_TYPE.PWD, 0);
		aMap.put(COMMAND_TYPE.RM, 1);
		aMap.put(COMMAND_TYPE.CP, 2);
		aMap.put(COMMAND_TYPE.MV, 2);
		aMap.put(COMMAND_TYPE.DIR, 0);
		aMap.put(COMMAND_TYPE.EXIT, 0);
		inputArgumentsCount = Collections.unmodifiableMap(aMap);
	}

	protected COMMAND_TYPE type;
	protected String[] args = new String[2];
	protected CommandReceiver receiver;

	public COMMAND_TYPE getType() {
		return type;
	}

	Command(CommandReceiver receiver) {
		this.receiver = receiver;
	}

	public static Command createCommand(Vector<String> buffer) throws Exception {
		if (buffer.isEmpty()) {
			return null;
		}
		Command retValue =  null;
		String commandName = buffer.firstElement();
		ShellReceiver receiver = ShellReceiver.sharedInstance();
		COMMAND_TYPE theType;
		try {
			theType = COMMAND_TYPE.valueOf(commandName.toUpperCase());
		} catch (IllegalArgumentException e) {
			buffer.removeAllElements();
			String enumName = "COMMAND_TYPE.";
			String type = e.getMessage().substring( e.getMessage().indexOf(enumName) + enumName.length()).toLowerCase();
			throw new Exception("Invalid command: \'" + type + "\'.");
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

	public abstract void execute() throws Exception;

	private static void readArgs(Command command, Vector<String> buffer) throws  MissingFormatArgumentException{
		for (int argumentIndex = 0; argumentIndex < inputArgumentsCount.get(command.getType()); ++argumentIndex) {
			if (argumentIndex + 1 >= buffer.size()) {
				throw new MissingFormatArgumentException("Not enough arguments for command \'"
						+ command.type.toString().toLowerCase() + "\'.");
			}
			command.args[argumentIndex] = buffer.elementAt(argumentIndex + 1);
		}
	}

}
