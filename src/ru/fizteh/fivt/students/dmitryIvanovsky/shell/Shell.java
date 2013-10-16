package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class Shell {

    public static void main(String[] args) throws IOException {
        //args = new String[]{"cd /home/deamoon/Music;", "cp dir3 dir4"};

        CommandShell fileCommand = new CommandShell();
        Map<String, String> commandList = new HashMap<String, String>(){ {
            put("dir", "dir");
            put("mv", "mv");
            put("cp", "cp");
            put("rm", "rm");
            put("pwd", "pwd");
            put("mkdir", "mkdir");
            put("cd", "cd");
        }};

        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileCommand, commandList);
        } catch (Exception e) {
            System.err.println("Не реализован метод из fileMapCommand");
            System.exit(1);
        }
        Code res = sys.runShell(args);
        if (res == Code.ERROR) {
            System.exit(1);
        }
    }

}
