package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;

public interface BasicCommand {
	abstract public void executeCommand(String[] arguments, Shell.CurrentShell curShell) throws IOException, InterruptionException;
}