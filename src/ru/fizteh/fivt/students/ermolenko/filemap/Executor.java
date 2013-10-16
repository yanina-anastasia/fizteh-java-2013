package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Executor {

    private Map<String, Command> mapOfCmd = new HashMap<String, Command>();

    public Executor() {
        list();
    }

    public void list() {

        Command put = new Put();
        mapOfCmd.put(put.getName(), put);
        Command get = new Get();
        mapOfCmd.put(get.getName(), get);
        Command remove = new Remove();
        mapOfCmd.put(remove.getName(), remove);
    }

    String cmdCheck(String cmd) {
        String tmp;
        int space = cmd.indexOf(" ");
        if (-1 == space)
            space = cmd.length();
        tmp = cmd.substring(0, space);
        return tmp;
    }

    String[] argsCheck(String inCommand) {
        int space = inCommand.indexOf(" ");
        if (-1 == space) {
            return new String[0];
        }
        String substr = inCommand.substring(space + 1);
        return substr.trim().split("\\ ");
    }

    public void execute(Map<String, String> dataBase, String cmd) throws IOException {
        if (!mapOfCmd.containsKey(cmdCheck(cmd))) {
            throw new IOException("Can't find key");
        }
        mapOfCmd.get(cmdCheck(cmd)).executeCmd(dataBase, argsCheck(cmd));
    }

}
