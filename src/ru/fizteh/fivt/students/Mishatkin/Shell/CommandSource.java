package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public interface CommandSource {
	public boolean hasMoreData();
	public String nextWord();
	public Command nextCommand();
}
