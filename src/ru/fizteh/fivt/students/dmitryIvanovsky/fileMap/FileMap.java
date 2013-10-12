package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class FileMap {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music";
        String path = System.getProperty("fizteh.db.dir");

        try {
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

        } catch (Exception e) {
            System.err.println("Ошибка загрузки базы данных");
            System.exit(1);
        }
    }
}
