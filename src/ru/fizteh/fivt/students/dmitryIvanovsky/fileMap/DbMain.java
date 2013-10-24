package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music/deamoonSql";
        String path = System.getProperty("fizteh.db.dir");
        runDb(args, path);
    }

    public static void runDb(String[] args, String path) throws IOException {
        FileMap fileMapCommand = null;
        try {
            fileMapCommand = new FileMap(path);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка загрузки базы данных");
            System.exit(1);
        }

        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileMapCommand);
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
