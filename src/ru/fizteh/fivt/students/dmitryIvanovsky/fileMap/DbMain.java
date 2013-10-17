package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music";
        String path = System.getProperty("fizteh.db.dir");

        MyFileMap fileMapCommand = null;
        try {
            fileMapCommand = new MyFileMap(path);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки базы данных");
            System.exit(1);
        }

        Map<String, String> commandList = new HashMap<String, String>(){ {
            put("put", "put");
            put("get", "get");
            put("remove", "remove");
        }};

        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileMapCommand, commandList);
        } catch (Exception e) {
            System.err.println("Не реализован метод из fileMapCommand");
            System.exit(1);
        }

        try {
            Code res = sys.runShell(args);
            if (res == Code.ERROR) {
                System.err.println("Ошибка выполнения");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Ошибка выполнения");
            System.exit(1);
        }

    }
}
