package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CmdLauncher {
    private Map<String, Command> commandList = new HashMap<String, Command>();

    private void fillCmdList() {
        CommandCd cd = new CommandCd();
        CommandCp cp = new CommandCp();
        CommandDir dir = new CommandDir();
        CommandMkdir mkdir = new CommandMkdir();
        CommandPwd pwd = new CommandPwd();
        CommandMv mv = new CommandMv();
        CommandRm rm = new CommandRm();

        commandList.put(cd.getName(), cd);
        commandList.put(cp.getName(), cp);
        commandList.put(dir.getName(), dir);
        commandList.put(mkdir.getName(), mkdir);
        commandList.put(pwd.getName(), pwd);
        commandList.put(mv.getName(), mv);
        commandList.put(rm.getName(), rm);
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
        for (String s : result) {
            s = s.trim();
        }
        return result;
    }

    public void launch(State state, String input) throws IOException {
        fillCmdList();
        String command = getCommand(input.trim());
        if (command.isEmpty()) {
            throw new IOException("Empty input.");
        }
        if (!commandList.containsKey(command)) {
            throw new IOException("Wrong command.");
        }
        commandList.get(command).run(state, getArgs(input));
    }
}