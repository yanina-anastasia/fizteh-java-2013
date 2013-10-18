package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;


public class Shell {


    public static void main(String[] arg) {
        try {
            HashMap<String, String> commands = new HashMap<String, String>();
            commands.put("cd", "changeDir");
            commands.put("dir", "dir");
            commands.put("mkdir", "makeDir");
            commands.put("pwd", "printWorkingDir");
            commands.put("rm", "remove");
            commands.put("mv", "move");
            commands.put("cp", "copy");
            commands.put("exit", "exit");
            Parser.parser(arg, MyFileSystem.class, commands);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
