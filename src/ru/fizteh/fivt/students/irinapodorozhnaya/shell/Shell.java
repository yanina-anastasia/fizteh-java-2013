package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class Shell {
	 public static void main(String[] args) {
		 StateShell st = new StateShell();
		 if (args.length > 0) {
			 Mode.batchMode(args, st);
		 } else {
			 Mode.interactiveMode(st);
		 }
	 }
}