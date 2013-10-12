package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.File;
import java.io.IOException;
import java.lang.System;

import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        Path sourcePath = Paths.get(args[0]);
        if (curState.workingDirectory != null && !sourcePath.isAbsolute()) {
            sourcePath = Paths.get(curState.workingDirectory).resolve(sourcePath);
        }
        File source = sourcePath.normalize().toFile();
        Path destinationPath = Paths.get(args[1]);
        if (curState.workingDirectory != null && !destinationPath.isAbsolute()) {
            destinationPath = Paths.get(curState.workingDirectory).resolve(destinationPath);
        }
        File destination = destinationPath.normalize().toFile();
        if (!destination.isDirectory()) {
            System.err.println("Destination is not a directory");
            return false;
        }
        if ((!source.exists()) || (!destination.exists())) {
            System.err.println("File does not exist");
            return false;
        }
        if (source.toString().equals(destination.toString())) {
            System.err.println("Error: source = destination");
            return false;
        }
        try {
            Files.move(sourcePath, destinationPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("IOException");
        }
        return true;
    }

    public String getCmd() {
        return "mv";
    }
}