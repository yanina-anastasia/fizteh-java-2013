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
	public static boolean isArgumentsMode;
	public static void main(String args[]) {
		InputStream inputStream = System.in;
		isArgumentsMode = (args.length > 0);
		initialCommandSource = isArgumentsMode ? new ArgumentsCommandSource(args) :
				new StandardInputCommandSource(new Scanner(inputStream));
		ShellRunner runner = new ShellRunner(initialCommandSource);
		runner.run();
	}
}
