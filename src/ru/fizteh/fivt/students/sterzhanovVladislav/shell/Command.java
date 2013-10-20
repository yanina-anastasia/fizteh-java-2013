package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.IOException;

public abstract class Command {
    protected Shell parentShell;
    public abstract Command newCommand();
    public abstract CommandParser getParser();
    public abstract void innerExecute() throws Exception, IOException;
    
    private int argc;
    public String[] args;
    
    void execute() throws IllegalArgumentException, Exception {
        if (args == null || args.length != argc)  {
            throw new IllegalArgumentException("Wrong number of arguments given");
        }
        if (parentShell == null) {
            throw new Exception("Cannot execute a command without a shell attached");
        }
        innerExecute();
    }
    
    private Command setArgc(int argCount) {
        argc = argCount;
        return this;
    }
    
    public Command setShell(Shell newShell) {
        parentShell = newShell;
        return this;
    }
    
 
    public Command(int argCount) {
        setArgc(argCount);
    }
    
    public Command(String[] newArgs, int argCount) {
        args = newArgs.clone();
        setArgc(argCount);
    }
}
