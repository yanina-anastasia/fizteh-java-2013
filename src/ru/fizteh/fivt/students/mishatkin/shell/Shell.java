package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.*;
import java.util.*;

/**
 * shell.java
 * shell
 *
 * Created by Vladimir Mishatkin on 9/21/13
 *
 */
public class Shell {
	public static boolean isArgumentsMode;
	public static void main(String args[]) {
		InputStream inputStream = System.in;
		isArgumentsMode = (args.length > 0);
		CommandSource in = isArgumentsMode ? new ArgumentsCommandSource(args) :
				new StandardInputCommandSource(new Scanner(inputStream));
		ShellReceiver receiver = new ShellReceiver();
		ShellRunner runner = new ShellRunner(in);
		runner.run(receiver);
	}
}
