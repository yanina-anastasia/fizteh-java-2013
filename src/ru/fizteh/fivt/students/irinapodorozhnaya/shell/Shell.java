package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.ExitRuntimeException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class Shell {
	 public static void main(String[] args) {
		try {
			StateShell st = new StateShell(System.in, System.out);
				if (args.length > 0) {
					Mode.batchMode(args, st);
				} else {
					Mode.interactiveMode(st);
				}
		} catch (ExitRuntimeException e) {
			System.exit(0);
		}
	 }
}