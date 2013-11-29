package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {

    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music/deamoonSql";

//        FileMapLoggingFactory f = new FileMapLoggingFactory();
//
//        Writer a = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
//        //Writer a = new OutputStreamWriter(new FileOutputStream("/home/deamoon/Videos/4.txt"));
//        //Writer w = new PrintWriter();
//        //a.write("!23");
//        //a.flush();
//
//        FileMapProviderFactory factory = new FileMapProviderFactory();
//        TableProviderFactory b = (TableProviderFactory) f.wrap(a, factory, TableProviderFactory.class);
//        b.create("table");
//        a.flush();
//        System.exit(1);

        try {
            String path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                throw new IllegalArgumentException("path can't be null");
            }
            Path pathTables = Paths.get(".").resolve(path);
            runDb(args, pathTables.toFile().getCanonicalPath());
        } catch (Exception e) {
            System.out.println("Error loading");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }
    }

    public static void runDb(String[] args, String path) throws IOException {
        FileMapProvider fileMapCommand = null;
        try {
            FileMapProviderFactory factory = new FileMapProviderFactory();
            fileMapCommand = (FileMapProvider) factory.create(path);
        } catch (Exception e) {
            System.err.println("Error loading database");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }

        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileMapCommand);
        } catch (Exception e) {
            System.err.println("Not implemented method of fileMapCommand");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }

        try {
            Code res = sys.runShell(args);
            if (res == Code.ERROR) {
                System.err.println("Runtime Error");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Runtime Error");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }
    }
}
