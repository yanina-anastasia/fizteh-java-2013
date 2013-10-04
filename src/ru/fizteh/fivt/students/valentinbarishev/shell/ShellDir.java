package ru.fizteh.fivt.students.valentinbarishev.shell;

public class ShellDir implements ShellCommand{
    static String name = "dir";

    private Context context;
    private String[] args;

    public ShellDir(Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        String[] content = context.getDirContent();
        for (int i = 0; i < content.length; ++i) {
            System.out.println(content[i]);
        }
    }

    @Override
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 1) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            args = command;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }
}
