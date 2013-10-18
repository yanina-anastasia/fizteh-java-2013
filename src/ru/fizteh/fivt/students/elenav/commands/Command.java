package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

public interface Command {
	
	String getName();
	
	int getArgNumber();
	
	void execute(String args[], PrintStream s) throws IOException;
}
