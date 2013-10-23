package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.filemap.RemoveCommand;
import java.io.OutputStream;

public class MultiTableRemoveCommand extends AbstractMultiTableDataBaseCommand {
	public MultiTableRemoveCommand() {
		super("remove", 1);
	};

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		new RemoveCommand().execute(args, state.getActiveTable(), out);
	}
}