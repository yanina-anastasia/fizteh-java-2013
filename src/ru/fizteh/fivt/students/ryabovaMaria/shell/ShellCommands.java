package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ShellCommands extends AbstractCommands {
    private static void parse() throws Exception {
        String[] tempLexems = new String[0];
        if (lexems.length > 1) {
            tempLexems = lexems[1].split("[ \t\n\r]+");
        }
        lexems = tempLexems;
    }
    
    public static void cd() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("cd: there is no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("cd: there is so many arguments.");
        }
        File newDir = new File(lexems[0]);
        Path temp = currentDir.toPath().resolve(newDir.toPath()).normalize();
        if (temp.toFile().isDirectory()) {
            currentDir = temp.toFile();
        } else {
            throw new Exception("cd: the argument isn't a directory.");
        }
    }
        
    public static void mkdir() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("mkdir: there id no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("mkdir: there is so many arguments.");
        }
        File dirName = new File(lexems[0]);
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
        parse();
        if (lexems.length > 0) {
            throw new Exception("pwd: there is no arguments.");
        }
        System.out.println(currentDir);
    }
    
    private static void delete(Path name) throws Exception {
        if (name.toFile().isDirectory()) {
            String[] list = name.toFile().list();
            for (int i = 0; i < list.length; ++i) {
                File curFile = new File(list[i]);
                Path temp = name.resolve(curFile.toPath()).normalize();
                delete(temp);
            }
            if (!name.toFile().delete()) {
                throw new Exception("rm: I can't delete this file.");
            }
        } else {
            if (!name.toFile().delete()) {
                throw new Exception("rm: I can't delete this file.");
            }
        }
    }
        
    public static void rm() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("rm: there is no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("rm: there is so many arguments.");
        }
        File dirName = new File(lexems[0]);
        Path temp = currentDir.toPath().resolve(dirName.toPath()).normalize();
        if (currentDir.toPath().startsWith(temp)) {
            throw new Exception("rm: I can't delete this.");
        } else {
            try {
                delete(temp);
            } catch (Exception e) {
                throw new Exception("rm: I can't delete this.");
            }
        }
    }
    
    private static void myCopy(boolean needDelete, 
                              Path sourcePath, 
                              Path destPath) throws Exception {
        if (sourcePath.toFile().isDirectory() 
            && destPath.toFile().isFile()) {
            if (needDelete) {
                throw new Exception("rm: I can't remove dir into file.");
            } else {
                throw new Exception("cp: I can't copy dir into file.");
            }
        }
        if (destPath.toFile().isDirectory()) {
            destPath = destPath.resolve(sourcePath.getFileName());
        }
        if (needDelete) {
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (sourcePath.toFile().isDirectory()) {
            String[] dirList = sourcePath.toFile().list();
            for (int i = 0; i < dirList.length; ++i) {
                Path newSourcePath = sourcePath.resolve(dirList[i]).normalize();
                myCopy(needDelete, newSourcePath, destPath);
            }
        }
    }
        
    public static void cp() throws Exception {
        parse();
        if (lexems.length < 2) {
            throw new Exception("cp: there is no enough arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("cp: there is so many arguments.");
        }
        Path currentPath = currentDir.toPath();
        File sourceName = new File(lexems[0]);
        Path sourcePath = sourceName.toPath();
        sourcePath = currentPath.resolve(sourcePath).normalize();
        File destName = new File(lexems[1]);
        Path destPath = currentPath.resolve(destName.toPath()).normalize();
        if (destPath.startsWith(sourcePath)) {
            throw new Exception("cp: there is a cyclic copying.");
        } else {
            try {
                myCopy(false, sourcePath, destPath);
            } catch (Exception e) {
                throw new Exception("cp: I can't copy this.");
            }
        }
    }
        
    public static void mv() throws Exception {
        parse();
        if (lexems.length < 2) {
            throw new Exception("mv: there is no enough arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("mv: there is so many arguments.");
        }
        Path currentPath = currentDir.toPath();
        File sourceName = new File(lexems[0]);
        Path sourcePath = sourceName.toPath();
        sourcePath = currentPath.resolve(sourcePath).normalize();
        File destName = new File(lexems[1]);
        Path destPath = currentPath.resolve(destName.toPath()).normalize();
        if (currentDir.toPath().startsWith(sourcePath)) {
            throw new Exception("mv: I can't remove this.");
        } else {
            if (destPath.startsWith(sourcePath)) {
                throw new Exception("mv: there is a cyclic removeing.");
            } else {
                myCopy(true, sourcePath, destPath);
            }
        }
    }
        
    public static void dir() throws Exception {
        parse();
        if (lexems.length > 0) {
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
}