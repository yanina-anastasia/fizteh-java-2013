package ru.fizteh.fivt.students.mishatkin.filemap;

import java.util.Scanner;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public class FileMap {
	public static void main(String[] args) {
		//System.setProperty("fizteh.db.dir", "/home/sane/Documents/JavaMipt/DBTest");
		CommandSource source = (args.length > 0) ? new BatchCommandSource(args)
		                                         : new StandartInputCommandSource(new Scanner(System.in));
		FileMapRunner runner = new FileMapRunner(source);
		String dbDirectory = System.getProperty("fizteh.db.dir");
		String dbFileName = "db.dat";
		FileMapReceiver receiver = null;
		try {
			receiver = new FileMapReceiver(dbDirectory, dbFileName, (args.length > 0), System.out);
			runner.runReceiver(receiver);
		} catch (MissingFileMapDatabaseException e) {
			System.err.println(e.getMessage());
		}
	}
}
