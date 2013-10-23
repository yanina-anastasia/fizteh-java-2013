package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.filemap.GetCommand;
import java.io.OutputStream;

public class MultiTableGetCommand extends AbstractMultiTableDataBaseCommand {
	public MultiTableGetCommand() {
		super("get", 1);
	};

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		new GetCommand().execute(args, state.getActiveTable(), out);
	}
}