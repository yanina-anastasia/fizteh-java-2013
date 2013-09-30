package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainShell {
    public static Map<String, Command> commandList = new HashMap<String, Command>();
    public static File currentDirectory = new File("C:/");

    public static void main(String[] args) {
        makeCommandList();

        if (args.length == 0) {
            InteractiveMode.work(System.in);
        } else {
            BatchMode.work(args);
        }

    }

    public static void makeCommandList() {
        CommandCd cd = new CommandCd();
        commandList.put(cd.getName(), cd);

        CommandPwd pwd = new CommandPwd();
        commandList.put(pwd.getName(), pwd);

        CommandDir dir = new CommandDir();
        commandList.put(dir.getName(), dir);

        CommandExit commandExit = new CommandExit();
        commandList.put(commandExit.getName(), commandExit);

        CommandMkdir mkdir = new CommandMkdir();
        commandList.put(mkdir.getName(), mkdir);

        CommandRm rm = new CommandRm();
        commandList.put(rm.getName(), rm);

        CommandCp cp = new CommandCp();
        commandList.put(cp.getName(), cp);

        CommandMv mv = new CommandMv();
        commandList.put(mv.getName(), mv);
    }
}
