package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.util.Scanner;
import java.io.File;
import java.lang.reflect.Method;    
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//import ru.fizteh.fivt.students.ryabovaMaria.fileMap.Commands;

public class Shell {
    static AbstractCommands command;
    
    public Shell(AbstractCommands myCommands, String s) {
        command = myCommands;
        command.currentDir = new File(System.getProperty(s));
    }
    
    public static void processing(String currentString)throws Exception {
        String[] commands = currentString.split("[ \t\n\r]*;[ \t\n\r]*");
        for (int i = 0; i < commands.length; ++i) {
            commands[i] = commands[i].trim();
            command.lexems = commands[i].split("[ \t\n\r]+", 2);
            if (command.lexems.length == 0) {
                continue;
            }
            String curCommand = command.lexems[0];
            curCommand.trim();
            if (!curCommand.isEmpty()) {
                Class c = command.getClass();
                try {
                    Method makeCommand = c.getMethod(curCommand, null);
                    makeCommand.invoke(command);
                } catch (NoSuchMethodException e) {
                    throw new Exception("Bad command"); 
                } catch (Exception e) {
                    throw new Exception(e.getCause().getMessage());
                }
            }
        }
    }
    
    public static void interactive() {
        String currentString;
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print(command.currentDir);
            System.out.print("$ ");
            if (scan.hasNextLine()) {
                currentString = scan.nextLine();
                try {
                    processing(currentString);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } else {
                System.exit(0);
            }
        }
    }
    
    public static void packet(String[] args) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            temp.append(args[i]);
            temp.append(" ");
        }
        try {
            processing(temp.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
