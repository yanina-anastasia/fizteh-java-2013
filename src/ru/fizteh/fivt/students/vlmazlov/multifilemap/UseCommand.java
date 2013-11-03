package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class UseCommand extends AbstractMultiTableDataBaseCommand {
	public UseCommand() {
		super("use", 1);
	}

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		String tablename = args[0];
		
		if (!state.use(tablename)) {
			displayMessage(tablename + " not exists" + SEPARATOR, out);
		} else {
			displayMessage("using " + tablename + SEPARATOR, out);
		}
	}
}