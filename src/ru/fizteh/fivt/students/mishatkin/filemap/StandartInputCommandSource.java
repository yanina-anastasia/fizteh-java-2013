package ru.fizteh.fivt.students.mishatkin.filemap;

import java.util.Scanner;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class StandartInputCommandSource extends CommandSource {
	private Scanner in;

	public StandartInputCommandSource(Scanner in) {
		this.in = in;
	}
	@Override
	public boolean hasMoreData() {
		return in.hasNext();
	}

	@Override
	public String nextLine() {
		return in.nextLine();
	}
}
