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
	public static void main(String args[]) {
		InputStream inputStream = System.in;
		CommandSource in = (args.length > 0) ? new BatchCommandSource(args) :
				new StandardInputCommandSource(new Scanner(inputStream));
		ShellReceiver receiver = new ShellReceiver(System.out, args.length == 0);
		ShellRunner runner = new ShellRunner(in);
		runner.run(receiver);
	}
}
