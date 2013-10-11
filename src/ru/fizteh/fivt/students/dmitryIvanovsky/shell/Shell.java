package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;
import java.util.*;

public class Shell {

    public static void main(String[] args) throws IOException {
        args = new String[]{"cd /home/deamoon/Music;", "cp2 dir3 dir4"};

        CommandShell fileCommand = new CommandShell();
        Map<String, String> commandList = new HashMap<String, String>(){{
            put("dir", "dir");
            put("mv", "mv");
            put("cp", "cp");
            put("rm", "rm");
            put("pwd", "pwd");
            put("mkdir", "mkdir");
            put("cd", "cd");
        }};

        CommandLauncher sys = new CommandLauncher(fileCommand, commandList);
        Code res = sys.runShell(args);
        if (res == Code.ERROR) {
            System.exit(1);
        }

    }

}
