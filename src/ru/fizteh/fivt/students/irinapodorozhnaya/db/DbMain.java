package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.ExitRuntimeException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class DbMain {
	public static void main(String[] args) {
		DbState st = null;
		try {
			st = new DbState(System.in, System.out);
			
			if (args.length > 0) {
				 Mode.batchMode(args, st);
			 } else {
				 Mode.interactiveMode(st);
			 }
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (ExitRuntimeException d) {
			try {
				st.commitDiff(); 
			} catch (IOException e) {
				System.err.println("can't write data to file");
				System.exit(1);
			}
			System.exit(0);
		}
	}
}

