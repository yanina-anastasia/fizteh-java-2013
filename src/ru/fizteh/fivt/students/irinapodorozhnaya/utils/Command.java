package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.IOException;

public interface Command {
	String getName();
	void execute(String[] args) throws IOException;
	int getNumberOfArguments();
}