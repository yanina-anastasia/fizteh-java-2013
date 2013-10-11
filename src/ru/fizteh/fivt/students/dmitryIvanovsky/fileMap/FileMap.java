package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

class MyFileMap {

    String dbPath;

    MyFileMap(String db) {
        dbPath = db;
    }


}

public class FileMap {
    public static void main(String[] args) {
        //args = new String[]{"cd /home/deamoon/Music;", "cp2 dir3 dir4"};

        MyFileMap fileMapCommand = new MyFileMap("123");
        Map<String, String> commandList = new HashMap<String, String>(){{
            put("put", "dir");
            put("get", "mv");
            put("remove", "cp");
        }};

        CommandLauncher sys = new CommandLauncher(fileMapCommand, commandList);
        Code res = sys.runShell(args);
        if (res == Code.ERROR) {
            System.exit(1);
        }
    }
}
