package ru.fizteh.fivt.students.mishatkin.filemap;


/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class FileMapRunner {
	private CommandSource in;

	public FileMapRunner(CommandSource in) {
		this.in = in;
	}

	public void runReceiver(FileMapReceiver receiver) {
		receiver.showPrompt();
		boolean shouldBreakRunLoop = false;
		while (!shouldBreakRunLoop) {
			Command aCommand = null;
			do {
				try {
					aCommand = in.nextCommand(receiver);
					if (aCommand != null) {
						aCommand.execute();
					}
				} catch (TimeToExitException e) {
					shouldBreakRunLoop = true;
				} catch (FileMapException e) {
					System.err.println(e.getMessage());
					in.clearBuffers();
					if (!receiver.isInteractiveMode()) {
						System.exit(1);
					}
				}
			} while (in.hasUnexecutedCommands());
			if (!shouldBreakRunLoop) {
				receiver.showPrompt();
			}
		}
	}
}
