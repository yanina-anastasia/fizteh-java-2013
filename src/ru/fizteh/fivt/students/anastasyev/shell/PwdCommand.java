package ru.fizteh.fivt.students.anastasyev.shell;

public class PwdCommand implements Command {
    private static void pwd() {
        System.out.println(Shell.userDir.toPath().normalize());
    }

    @Override
    public boolean exec(String[] command) {
        if (command.length != 1) {
            System.err.println("pwd: Usage - pwd");
            return false;
        }
        try {
            pwd();
        } catch (Exception e) {
            System.err.println("pwd: wrong path");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "pwd";
    }
}
