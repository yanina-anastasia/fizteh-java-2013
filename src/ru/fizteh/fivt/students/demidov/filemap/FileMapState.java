package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;

public interface FileMapState {
	public FileMap getCurrentFileMap(String key) throws IOException;
}
