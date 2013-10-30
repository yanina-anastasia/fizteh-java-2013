package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.BatchCommandSource;
import ru.fizteh.fivt.students.mishatkin.shell.CommandSource;
import ru.fizteh.fivt.students.mishatkin.shell.StandardInputCommandSource;

import java.io.File;
import java.util.Scanner;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMap {
	public static final int TABLE_OWNING_DIRECTORIES_COUNT = 16;

	public static void main(String[] args) {
//		System.setProperty("fizteh.db.dir", "/home/sane/Documents/JavaMipt/DBTest");
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
				: new StandardInputCommandSource(new Scanner(System.in));
		MultiFileHashMapRunner runner = new MultiFileHashMapRunner(source);
		String dbDirectory = System.getProperty("fizteh.db.dir");
		if (dbDirectory == null) {
			System.err.println("set \"fizteh.db.dir\" property before launch.");
			System.exit(1);
		} else {
			File dbFile = new File(dbDirectory);
			if (!dbFile.exists() || !dbFile.isDirectory()) {
				System.err.println("Specified dbDirectory is simply not a directory. Make sure your keyboard is not ejected, just in case.");
				System.exit(1);
			}
		}
		MultiFileHashMapReceiver receiver = new MultiFileHashMapReceiver(System.out, (args.length == 0), dbDirectory);
			runner.run(receiver);
	}
}
