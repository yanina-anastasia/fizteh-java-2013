package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class FileMap {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"cd /home/deamoon/Music;", "cp2 dir3 dir4"};
        String path = System.getProperty("fizteh.db.dir");
        MyFileMap fileMapCommand = new MyFileMap(path);

        Map<String, String> commandList = new HashMap<String, String>(){ {
            put("put", "put");
            put("get", "get");
            put("remove", "remove");
        }};

        CommandLauncher sys = new CommandLauncher(fileMapCommand, commandList);
        Code res = sys.runShell(args);
        fileMapCommand.closeDbFile();

        if (res == Code.ERROR) {
            System.exit(1);
        }
    }
}
