package ru.fizteh.fivt.students.Mishatkin.Shell;
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
};

public abstract class Command {
	private static  final Map<COMMAND_TYPE, Integer> inputArgumentsCount;
	static {
		Map<COMMAND_TYPE, Integer> aMap = new HashMap<>();
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
	protected ShellReceiver receiver;

	public COMMAND_TYPE getType() {
		return type;
	}

//	public String[] getArgs() {
//		return args;
//	}

	Command(ShellReceiver _receiver) {
		receiver = _receiver;
	}

	public static Command readCommand(CommandSource in) {
		Command retValue =  null;
		String commandName = in.nextWord().toUpperCase();
		ShellReceiver receiver = ShellReceiver.sharedInstance();
		switch (COMMAND_TYPE.valueOf(commandName)) {
			case CD:
				retValue = new ChangeDirectoryCommand(receiver);
				break;
//			case MKDIR:
//				retValue = new MakeDirectoryCommand(receiver);
//				break;
//			case PWD:
//				retValue = new PrintWorkingDirectoryCommand(receiver);
//				break;
//			case RM:
//				retValue = new RemoveCommand(receiver);
//				break;
//			case CP:
//				retValue = new CopyCommand(receiver);
//				break;
//			case MV:
//				retValue = new MoveCommand(receiver);
//				break;
			case DIR:
				retValue = new DirectoryCommand(receiver);
				break;
			case EXIT:
				retValue = new ExitCommand(receiver);
				break;
		}
		if (retValue != null) {
			readArgs(retValue, in);
		}
		return retValue;
	}

	public abstract void execute() throws Exception;

	private static void readArgs(Command command, CommandSource in) throws  MissingFormatArgumentException{
		for (int argumentIndex = 0; argumentIndex < inputArgumentsCount.get(command.getType()); ++argumentIndex) {
			if (!in.hasMoreData()) {
				throw new MissingFormatArgumentException("Not enough arguments for command \'"
						+ command.type.toString().toLowerCase() + "\'.");
			}
			command.args[argumentIndex] = in.nextWord();
		}
	}

}
