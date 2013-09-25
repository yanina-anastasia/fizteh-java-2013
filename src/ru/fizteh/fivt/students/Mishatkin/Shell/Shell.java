package ru.fizteh.fivt.students.Mishatkin.Shell;

import java.io.*;
import java.util.*;

/**
 * Shell.java
 * Shell
 *
 * Created by Vladimir Mishatkin on 9/21/13
 *
 */
public class Shell {
	public static CommandSource initialCommandSource;
	public static PrintWriter initialOutput;
	public static boolean isArgumentsMode;
	public static void main(String args[]) {
		InputStream inputStream = System.in;
		OutputStream outputStream = System.out;
		isArgumentsMode = (args.length > 0);
		initialCommandSource = isArgumentsMode ? new ArgumentsCommandSource(args) :
				new StandardInputCommandSource(new Scanner(inputStream));
		initialOutput = isArgumentsMode ? null : new PrintWriter(outputStream);

		ShellRunner runner = new ShellRunner(initialCommandSource, initialOutput);
		runner.run();
		if (initialOutput != null) {
			initialOutput.close();
		}
	}
}
