package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.util.Vector;

public class StateShell {
	File currentDir = new File(".");	
	Vector <Command> commands = new Vector<Command>();
	StateShell() {
		new Dir(this).init(this);
		new Cd(this).init(this);
		new Rm(this).init(this);
		new Mv(this).init(this);
		new Pwd(this).init(this);
		new Cp(this).init(this);
		new Exit(this).init(this);
		new MkDir(this).init(this);
	}
}