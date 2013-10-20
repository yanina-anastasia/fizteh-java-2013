package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.File;
import java.util.HashMap;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellState extends State{
    public ShellState() {
        commands = new HashMap<String, Command>();
        currentDir = new File(".");
    }
    
    public String makeNewSource(final String source) {
        File newFile = new File(source);
        if (newFile.isAbsolute()) {
            return newFile.getAbsolutePath();
        } else {
            return currentDir.getAbsolutePath() + File.separator + source;
        }
    }
    
    public int cd(final String source) {
        File newDir = new File(makeNewSource(source));
        if (!newDir.exists() || !newDir.isDirectory()) {
            System.err.println("cd: " + source
                                 + ": is not a directory");
            return 1;
        }
        try {
            if (newDir.isAbsolute()) {
                currentDir = newDir;
            } else {
                currentDir = new File(currentDir.getCanonicalPath()
                            + File.separator + newDir);
            }
        } catch (Exception e) {
            System.err.println("cd: " + source
                    + ": is not a correct directory");
            return 1;
        }
        return 0;
    }
}
