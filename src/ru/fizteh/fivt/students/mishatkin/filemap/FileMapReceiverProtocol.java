package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapException;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public interface FileMapReceiverProtocol {
	public void putCommand(String key, String value) throws MultiFileHashMapException;
	public void getCommand(String key) throws MultiFileHashMapException;
	public void removeCommand(String key) throws MultiFileHashMapException;
}
