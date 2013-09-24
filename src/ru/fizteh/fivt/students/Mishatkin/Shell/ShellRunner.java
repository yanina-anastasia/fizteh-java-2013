package ru.fizteh.fivt.students.Mishatkin.Shell;

import java.io.PrintWriter;
import java.util.MissingFormatArgumentException;

/**
 * ShellRunner.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */

public class ShellRunner {
	private CommandSource in;
	private PrintWriter out;

	public ShellRunner(CommandSource _in, PrintWriter _out) {
		in = _in;
		out = _out;
	}

	public void run() {
		ShellReceiver.sharedInstance().showPrompt();
		while (in.hasMoreData()) {
			Command aCommand = null;
			try {
				aCommand = in.nextCommand();
				if (aCommand != null) {
					aCommand.execute();
				}
			} catch (TimeToExitException e) {
				break;
			} catch (MissingFormatArgumentException e) {
				System.err.println(e.getMessage());
			} catch (IllegalArgumentException e) {
				String enumName = "COMMAND_TYPE.";
				String type = e.getMessage().substring( e.getMessage().indexOf(enumName) + enumName.length()).toLowerCase();
				System.err.println("Invalid command: \'" + type + "\'.");
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			ShellReceiver.sharedInstance().showPrompt();
		}
	}
}
