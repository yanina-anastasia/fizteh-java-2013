package ru.fizteh.fivt.students.paulinMatavina.utils;

import java.io.File;
import java.util.HashMap;

public abstract class State {
    public File currentDir;
    public HashMap<String, Command> commands;
    
    public State() {
        commands = new HashMap<String, Command>();
        currentDir = new File(".");
    }
    
    public void add(Command command) {
        commands.put(command.getName(), command);
    } 
    
    public void exitWithError(int errCode) {
        System.exit(errCode);
    }
}
