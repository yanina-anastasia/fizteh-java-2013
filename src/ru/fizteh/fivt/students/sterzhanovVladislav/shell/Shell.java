package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.util.HashMap;
import java.util.Scanner;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Shell {
    public PrintStream out = System.out;
    public PrintStream err = System.err;
    private Path workingDir;
    
    private HashMap<String, Command> cmdMap;
    
    public void execCommandStream(InputStream cmdStream, boolean isInteractiveMode) throws Exception {
        Scanner cmdReader = new Scanner(cmdStream);
        try {
            maybePrintPrompt(isInteractiveMode);
            while (cmdReader.hasNextLine()) {
                try {
                    String[] cmdList = cmdReader.nextLine().split(";");
                    for (String cmdString : cmdList) {
                        Command cmd = parseNextCommand(cmdString);
                        if (cmd != null) {
                            cmd.execute();
                        }
                    }
                } catch (Exception e) {
                    if (isInteractiveMode) {
                        out.println(e.getMessage());
                    } else {
                        throw e;
                    }
                }
                maybePrintPrompt(isInteractiveMode);
            }
        } finally {
            cmdReader.close();
        }
    }
    
    public void exit(int exitCode) {
        System.exit(exitCode);
    }
    
    public Path getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(Path newWorkingDir) {
        workingDir = newWorkingDir.normalize();
    }

    public Path getAbsolutePath(Path path) {
        return getWorkingDir().resolve(path).normalize();
    }

    public static boolean isSubdirectory(Path dirToCheck, Path relativeTo) {
        dirToCheck = dirToCheck.normalize().getParent();
        relativeTo = relativeTo.normalize();
        while (!dirToCheck.equals(dirToCheck.getRoot())) {
            if (dirToCheck.equals(relativeTo)) {
                return true;
            }
            dirToCheck = dirToCheck.getParent();
        }
        return false;
    }
    
    private Command parseNextCommand(String cmdLine) {
        if (cmdLine.trim().isEmpty()) {
            return null;
        }
        String[] tokens = cmdLine.trim().split("[\t ]+");
        String cmdName = tokens[0];
        if (!cmdMap.containsKey(cmdName)) {
            throw new IllegalArgumentException("Illegal command");
        }
        Command cmd = cmdMap.get(cmdName).newCommand().setShell(this);
        cmd.args = tokens;
        return cmd;
    }
    
    private void maybePrintPrompt(boolean doPrintPrompt) {
        if (doPrintPrompt) {
            out.print("$ ");
        }
    }
    
    public Shell(HashMap<String, Command> commandMap) {
        cmdMap = commandMap;
        workingDir = Paths.get(System.getProperty("user.dir"));
    }
}
