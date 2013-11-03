package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;

public interface BasicState {
	Table getUsedTable() throws IOException;
}

