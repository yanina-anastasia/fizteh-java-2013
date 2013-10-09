package ru.fizteh.fivt.students.isItJavaOrSomething.Shell;

import java.io.File;


public interface Commands {

    public String getCommandName();
    
    public int getArgumentQuantity();
    
    abstract public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrong;
}
