package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 02.10.13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */

final class ShellPwd implements ShellCommand {
    static String name = "pwd";

    private Context context;
    private String[] args;

    public ShellPwd(Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        System.out.println(context.getCurrentDir());
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
