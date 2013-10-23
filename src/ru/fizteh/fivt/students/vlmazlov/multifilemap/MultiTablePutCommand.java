package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.filemap.PutCommand;
import java.io.OutputStream;

public class MultiTablePutCommand extends AbstractMultiTableDataBaseCommand {
	public MultiTablePutCommand() {
		super("put", 2);
	};

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		new PutCommand().execute(args, state.getActiveTable(), out);
	}
}