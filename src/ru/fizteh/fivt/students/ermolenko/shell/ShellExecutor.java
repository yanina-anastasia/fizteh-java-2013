package ru.fizteh.fivt.students.ermolenko.shell;

public class ShellExecutor extends Executor {

    public ShellExecutor() {
        list();
    }

    public void list() {
        Command pwd = new pwd();
        mapOfCmd.put(pwd.getName(), pwd);
        Command cd = new cd();
        mapOfCmd.put(cd.getName(), cd);
        Command mkdir = new mkdir();
        mapOfCmd.put(mkdir.getName(), mkdir);
        Command cp = new cp();
        mapOfCmd.put(cp.getName(), cp);
        Command mv = new mv();
        mapOfCmd.put(mv.getName(), mv);
        Command dir = new dir();
        mapOfCmd.put(dir.getName(), dir);
        Command rm = new rm();
        mapOfCmd.put(rm.getName(), rm);
    }
}