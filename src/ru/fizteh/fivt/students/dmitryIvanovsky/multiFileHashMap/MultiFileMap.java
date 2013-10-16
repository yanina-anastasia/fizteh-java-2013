package ru.fizteh.fivt.students.dmitryIvanovsky.multiFileHashMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class MultiFileMap {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        String path = "/home/deamoon/Music/deamoonSql/";
        //String path = System.getProperty("fizteh.db.dir");

        try {
            MyMultiFileMap fileMapCommand = new MyMultiFileMap(path);

            Map<String, String> commandList = new HashMap<String, String>(){ {
                put("put", "multiPut");
                put("get", "multiGet");
                put("remove", "multiRemove");
                put("create", "create");
                put("drop", "drop");
                put("use", "use");
            }};

            CommandLauncher sys = new CommandLauncher(fileMapCommand, commandList);
            Code res = sys.runShell(args);


            if (res == Code.ERROR) {
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();

            System.err.println("Ошибка загрузки базы данных");
            System.exit(1);
        }
    }
}
