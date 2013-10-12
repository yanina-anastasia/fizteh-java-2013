package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;

class Main {
	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();

		if (args.length == 0) {
			shell.startInteractive();
		} else {
			StringBuilder commands = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				commands.append(args[i] + " ");
			}

			shell.startBatch(commands.toString());
		}
	}
}

