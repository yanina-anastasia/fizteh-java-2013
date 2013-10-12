package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

public class Execurtor {

    private Map<String, Command> mapOfCmd = new HashMap<String, Command>();

    public Execurtor() {
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

    String[] argsCheck(String inCommand) {
        int space = inCommand.indexOf(" ");
        if (-1 == space) {
            return new String[0];
        }
        String substr = inCommand.substring(space + 1);
        String[] tmp = substr.trim().split("\\ ");
        return tmp;
    }

    String cmdCheck(String cmd) {
        String tmp;
        int space = cmd.indexOf(" ");
        if (-1 == space)
            space = cmd.length();
        tmp = cmd.substring(0, space);
        return tmp;
    }

    public void execute(Shell shell, String cmd) throws IOException {
        if (!mapOfCmd.containsKey(cmdCheck(cmd))) {
            throw new IOException("Can't find key");
        }
        mapOfCmd.get(cmdCheck(cmd)).executeCmd(shell, argsCheck(cmd));
    }
}