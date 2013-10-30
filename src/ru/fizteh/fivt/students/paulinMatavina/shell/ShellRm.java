package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.File;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellRm implements Command {
    @Override
    public int execute(String[] args, State state) {
        try {
            File source = new File(((ShellState) state).makeNewSource(args[0]));
            File[] dirList = source.listFiles();
            if (!source.exists()) {
                System.err.println("rm: no file: " + source);
                throw new IllegalArgumentException();
            }
            if (source.isDirectory()) {
                for (int i = 0; i < dirList.length; i++) {
                    String[] arg = new String[1];
                    arg[0] = dirList[i].getAbsolutePath();
                    execute(arg, state);
                }
            }
            if (!source.delete()) {
                System.err.println("rm: cannot delete: " + source);
                throw new IllegalArgumentException();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("rm: error: " + e.getMessage());
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getName() {
        return "rm";
    }
    
    @Override 
    public int getArgNum() {
        return 1;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
