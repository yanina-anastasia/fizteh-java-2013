package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.kislenko.filemap.*;

public class CmdLauncher {
    private Map<String, Command> commandList = new HashMap<String, Command>();

    private void fillCmdList() {
        /*Command cd = new CommandCd();
        Command cp = new CommandCp();
        Command dir = new CommandDir();
        Command mkdir = new CommandMkdir();
        Command pwd = new CommandPwd();
        Command mv = new CommandMv();
        Command rm = new CommandRm();
        commandList.put(cd.getName(), cd);
        commandList.put(cp.getName(), cp);
        commandList.put(dir.getName(), dir);
        commandList.put(mkdir.getName(), mkdir);
        commandList.put(pwd.getName(), pwd);
        commandList.put(mv.getName(), mv);
        commandList.put(rm.getName(), rm);*/

        Command put = new CommandPut();
        Command get = new CommandGet();
        Command rem = new CommandRemove();
        commandList.put(put.getName(), put);
        commandList.put(get.getName(), get);
        commandList.put(rem.getName(), rem);
    }

    private String getCommand(String inputString) {
        int start = 0;
        int finish = inputString.indexOf(" ");
        if (finish == -1) {
            finish = inputString.length();
        }
        return inputString.substring(start, finish);
    }

    private String[] getArgs(String inputString) {
        int start = inputString.indexOf(" ");
        if (start == -1) {
            return new String[0];
        }
        String[] result = inputString.substring(start + 1, inputString.length()).trim().split("\\s+");
        for (int i = 0; i < result.length; ++i) {
            result[i] = result[i].trim();
        }
        return result;
    }

    public void launch(State state, String input) throws IOException {
        fillCmdList();
        String command = getCommand(input.trim());
        String[] args = getArgs(input.trim());
        if (command.isEmpty()) {
            throw new IOException("Empty input.");
        }
        if (!commandList.containsKey(command)) {
            throw new IOException("Wrong command.");
        }
        if (args.length != commandList.get(command).getArgCount()) {
            throw new IOException("Incorrect argument count.");
        }
        commandList.get(command).run(state, args);
    }
}