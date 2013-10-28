package ru.fizteh.fivt.students.drozdowsky;

import ru.fizteh.fivt.students.drozdowsky.database.FileMap;
import ru.fizteh.fivt.students.drozdowsky.modes.ModeController;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FileMapMain {

    public static void main(String[] args) {
        String dbDirectory = System.getProperty("fizteh.db.dir");
        if (dbDirectory == null) {
            System.err.println("No database location");
            System.exit(1);
        }
        if (!new File(dbDirectory).isDirectory()) {
            System.err.println(dbDirectory + ": not a directory");
            System.exit(1);
        }
        File dbPath = new File(dbDirectory + "/db.dat");

        String[] commandNames = {"put", "get", "remove", "exit"};
        HashMap<String, Method> map = Utils.getMethods(commandNames, FileMap.class);
        try {
            FileMap db = new FileMap(dbPath);
            ModeController<FileMap> start = new ModeController<FileMap>(db);
            start.execute(map, args);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
