package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * ShellRunner.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */

public class ShellRunner {
	private CommandSource in;

	public ShellRunner(CommandSource _in) {
		in = _in;
	}

	public void run() {
		ShellReceiver.sharedInstance().showPrompt();
		boolean shouldBreakRunLoop = false;
		while (!shouldBreakRunLoop) {
			Command aCommand = null;
			do {
				try {
					aCommand = in.nextCommand();
					if (aCommand != null) {
						aCommand.execute();
					}
				} catch (TimeToExitException e) {
					shouldBreakRunLoop = true;
					break;
				} catch (Exception e) {
					System.err.println(e.getMessage());
					shouldBreakRunLoop = true;
					break;
				}
			} while (in.hasUnexecutedCommands());
			if (!shouldBreakRunLoop) {
				ShellReceiver.sharedInstance().showPrompt();
			}
		}
	}
}
