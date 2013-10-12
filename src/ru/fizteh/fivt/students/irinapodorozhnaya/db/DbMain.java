package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class DbMain {
	public static void main(String[] args) {
		try {
			DbState st = new DbState();
			
			if (args.length > 0) {
				 Mode.batchMode(args, st);
			 } else {
				 Mode.interactiveMode(st);
			 }
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		} catch (ExitRuntimeException e) {
			System.exit(0);
		}
	}
}

