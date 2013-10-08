package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Shell {
    public static int argc;
    
    public static File currentDir;
    
    public static String[] lexems;
    
    public static void cd() throws Exception { 
        if (lexems.length == 1) {
            throw new Exception("cd: there is no arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("cd: there is so many arguments.");
        }
        File newDir = new File(lexems[1]);
        Path temp = currentDir.toPath().resolve(newDir.toPath()).normalize();
        if (temp.toFile().isDirectory()) {
            currentDir = temp.toFile();
        } else {
            throw new Exception("cd: the argument isn't a directory.");
        }
    }
        
    public static void mkdir() throws Exception {
        if (lexems.length == 1) {
            throw new Exception("mkdir: there id no arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("mkdir: there is so many arguments.");
        }
        File dirName = new File(lexems[1]);
        Path temp = currentDir.toPath().resolve(dirName.toPath()).normalize();
        if (temp.toFile().exists()) {
            throw new Exception("mkdir: this directory is already exists.");
        } else {
            if (!temp.toFile().mkdir()) {
                throw new Exception("mkdir: I can't make this directory.");
            }
        }
    }
        
    public static void pwd() throws Exception {
        if (lexems.length > 1) {
            throw new Exception ("pwd: there is no arguments.");
        }
        System.out.println(currentDir);
    }
    
    public static void delete(Path name) throws Exception {
        if (name.toFile().isDirectory()) {
            String[] list = name.toFile().list();
            for (int i = 0; i < list.length; ++i) {
                File curFile = new File(list[i]);
                Path temp = name.resolve(curFile.toPath()).normalize();
                delete(temp);
            }
            name.toFile().delete();
        } else {
            if (!name.toFile().delete()) {
                throw new Exception("rm: I can't delete this file.");
            }
        }
    }
        
    public static void rm() throws Exception {
        if (lexems.length == 1) {
            throw new Exception("rm: there is no arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("rm: there is so many arguments.");
        }
        File dirName = new File(lexems[1]);
        Path temp = currentDir.toPath().resolve(dirName.toPath()).normalize();
        delete(temp);
    }
        
    public static void cp() throws Exception {
        if (lexems.length < 3) {
            throw new Exception("cp: there is no enough arguments.");
        }
        if (lexems.length > 3) {
            throw new Exception("cp: there is so many arguments.");
        }
        try {
            File sourceName = new File(lexems[1]);
            Path sourcePath = currentDir.toPath().resolve(sourceName.toPath()).normalize();
            File destinationName = new File(lexems[2]);
            Path destinationPath = currentDir.toPath().resolve(destinationName.toPath()).normalize();
            destinationPath = destinationPath.resolve(sourceName.toPath()).normalize();
            //System.out.println(sourcePath);
            //System.out.println(destinationPath);
            Files.copy(sourcePath, destinationPath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new Exception("cp: I can't copy this file.");
        }
    }
        
    public static void mv() throws Exception {
        if (lexems.length < 3) {
            throw new Exception("mv: there is no enough arguments.");
        }
        if (lexems.length > 3) {
            throw new Exception("mv: there is so many arguments.");
        }
        try {
            File sourceName = new File(lexems[1]);
            Path sourcePath = currentDir.toPath().resolve(sourceName.toPath()).normalize();
            File destinationName = new File(lexems[2]);
            Path destinationPath = currentDir.toPath().resolve(destinationName.toPath()).normalize();
            if (sourcePath.toFile().isFile()
                && destinationPath.toFile().isFile()) {
                sourcePath.toFile().renameTo(destinationPath.toFile());
            } else {
                destinationPath = destinationPath.resolve(sourceName.toPath()).normalize();
                Files.copy(sourcePath, destinationPath);
                delete(sourcePath);
            }
        } catch (Exception e) {
            throw new Exception("mv: I can't remove this file.");
        }
    }
        
    public static void dir() throws Exception {
        if (lexems.length > 1) {
            throw new Exception("dir: there is some arguments.");
        }
        String[] dirList = currentDir.list();
        for (int i = 0; i < dirList.length; ++i) {
            System.out.println(dirList[i]);
        }
    }
        
    public static void exit() {
        System.exit(0);
    }
    
    public static void processing(String currentString)throws Exception {
        String[] commands = currentString.split("[ ]*;[ ]*");
        for (int i = 0; i < commands.length; ++i) {
            lexems = commands[i].split("[ ]+");
            String command = lexems[0];
            switch (command) {
                case "cd":
                    cd();
                    break;
                case "mkdir":
                    mkdir();
                    break;
                case "pwd":
                    pwd();
                    break;
                case "rm":
                    rm();
                    break;
                case "cp":
                    cp();
                    break;
                case "mv":
                    mv();
                    break;
                case "dir":
                    dir();
                    break;
                case "exit":
                    exit();
                    break;
                default:
                    throw new Exception("Bad command");
            }
        }
    }
    
    public static void interactive() {
        String currentString;
        Scanner scan = new Scanner(System.in); 
        while (true) {
            System.out.print(currentDir);
            System.out.print("$ ");
            currentString = scan.nextLine();
            try {
                processing(currentString);
            } catch (Exception e) {
                System.err.println(e.getMessage());
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
    
    public static void main(String[] args) {
        argc = args.length;
        currentDir = new File(System.getProperty("user.dir"));
        if (argc == 0) {
            interactive();
        } else {
            packet(args);
        }
    }
}