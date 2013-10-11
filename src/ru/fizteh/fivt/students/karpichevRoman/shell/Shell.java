package ru.fizteh.fivt.students.karpichevRoman.shell;


import java.io.Console;
import java.nio.file.Path;
import java.nio.file.Paths;

class Shell {
    private boolean terminated;
    private Console console;
    private Command[] cmdArray;
    private Path currentPath;

    public Shell() {
        console = System.console();
        terminated = false;
        currentPath = Paths.get(".").toAbsolutePath().normalize();
        cmdArray = new Command[]{new ExitCommand(), new PwdCommand(), new CdCommand(),
                    new MkdirCommand(), new LsCommand(), new RmCommand(), new CpCommand(),
                    new MvCommand() };
    }

    public void execShell() throws IllegalArgumentException {
        while (!terminated) {
            if (console == null) {
                System.err.println("too bad");
            }
            console.printf("%s ", currentPath.toString());
            console.printf("$ ");
            
            String commandSeq = console.readLine();

            if (commandSeq == null) {
                console.printf("\n");
                break;
            }
            try {
                runCommandSeq(commandSeq);
            } catch (IllegalArgumentException exception) {
                echo(exception.getMessage());
            }
        }
    }
    
    public void runCommandSeq(String commandSeq) {
        String[] commands = commandSeq.split(";");
        for (String i : commands) {
            runCommand(i);
        }
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path newPath) {
        currentPath = newPath;
    }

    private void runCommand(String command) throws IllegalArgumentException {
        if (terminated) {
            return;
        }
        
        boolean commandStarted = false;
        for (Command i : cmdArray) {
            if (i.isThatCommand(command)) {
                commandStarted = true;
                i.run(this, command);
                break;
            }
        }

        if (!commandStarted) {
            throw new IllegalArgumentException("Command does not exist " + command);
        }
    }

    public void echo(String str) {
        console.printf("%s\n", str);
    }

    public void terminate() {
        terminated = true;
    }
}
