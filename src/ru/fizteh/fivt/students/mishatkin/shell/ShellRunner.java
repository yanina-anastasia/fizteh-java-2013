package ru.fizteh.fivt.students.mishatkin.shell;

import ru.fizteh.fivt.students.kamilTalipov.shell.PrintDirContain;

/**
 * ShellRunner.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */

public class ShellRunner {
	private CommandSource in;

	public ShellRunner(CommandSource in) {
		this.in = in;
	}

	public void run(ShellReceiver receiver) {
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
					if (!e.getMessage().equals("")) {
						System.err.println(e.getMessage());
					}
					shouldBreakRunLoop = true;
				} catch (ShellException e) {
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
