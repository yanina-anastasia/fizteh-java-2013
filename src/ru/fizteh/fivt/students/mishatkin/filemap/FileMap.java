package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.BatchCommandSource;
import ru.fizteh.fivt.students.mishatkin.shell.CommandSource;
import ru.fizteh.fivt.students.mishatkin.shell.StandardInputCommandSource;

import java.util.*;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public class FileMap {
	public static void main(String[] args) {
		System.setProperty("fizteh.db.dir", "/home/sane/Documents/JavaMipt/DBTest");
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
		                                         : new StandardInputCommandSource(new Scanner(System.in));
		FileMapRunner runner = new FileMapRunner(source);
		String dbDirectory = System.getProperty("fizteh.db.dir");
		String dbFileName = "db.dat";
		FileMapReceiver receiver = null;
		try {
			receiver = new FileMapReceiver(dbDirectory, dbFileName, (args.length == 0), System.out);
			runner.run(receiver);
		} catch (MissingFileMapDatabaseException e) {
			System.err.println(e.getMessage());
		}
	}


}
