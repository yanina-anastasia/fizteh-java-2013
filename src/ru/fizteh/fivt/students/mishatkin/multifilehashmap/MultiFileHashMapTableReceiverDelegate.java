package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import java.io.PrintStream;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public interface MultiFileHashMapTableReceiverDelegate {
	public String getDbDirectoryName();
	public PrintStream getOut();//lol
	public boolean isInteractiveMode();
}
