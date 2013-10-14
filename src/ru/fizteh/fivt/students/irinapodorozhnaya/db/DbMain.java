package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class DbMain {
	public static void main(String[] args) {
		DbState st = null;
		try {
			 st = new DbState();
			
			if (args.length > 0) {
				 Mode.batchMode(args, st);
			 } else {
				 Mode.interactiveMode(st);
			 }
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (ExitRuntimeException d) {
			try {
				st.commitDiff (); 
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
			System.exit(0);
		}
	}
}

