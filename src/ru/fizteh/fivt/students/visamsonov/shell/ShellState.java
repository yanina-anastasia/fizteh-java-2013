package ru.fizteh.fivt.students.visamsonov.shell;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.visamsonov.storage.TableFactory;

public class ShellState {

	public Table database;
	public final TableProvider tableProvider;

	public ShellState () {
		TableProviderFactory factory = new TableFactory();
		tableProvider = factory.create(System.getProperty("fizteh.db.dir"));
	}
}