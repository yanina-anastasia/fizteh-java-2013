package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AbstractCommand implements Command{
	private StateShell state;
	private int argsNumber;
	AbstractCommand() {
		argsNumber = 0;
	}
	public int getNumberOfArguments(){
		return argsNumber;
	}
}
