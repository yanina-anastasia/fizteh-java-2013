package ru.fizteh.fivt.students.karpichevRoman.shell;

class ExitCommand implements Command {
    private static String name;
    
    public ExitCommand() {
        name = "exit";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim());
    }
    
    public void run(Shell shell, String command) throws IllegalArgumentException {
        shell.terminate();
    }
}
