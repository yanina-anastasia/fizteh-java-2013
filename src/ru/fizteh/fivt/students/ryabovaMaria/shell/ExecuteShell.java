package ru.fizteh.fivt.students.ryabovaMaria.shell;

public class ExecuteShell {
    static ShellCommands commands = new ShellCommands();
    static Shell shell = new Shell(commands, "user.dir");
    
    public static void main(String[] args) {
        int argc = args.length;
        if (argc == 0) {
            shell.interactive();
        } else {
            shell.packet(args);
        }
    }
}
