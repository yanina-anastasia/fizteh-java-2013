package ru.fizteh.fivt.students.vorotilov.file_map;

import java.io.File;
import java.io.IOException;

public class DataBaseCommands {

    public static void putCommand(File dbFile, String[] parsedCommand) throws IOException {
        System.out.println(dbFile.getCanonicalPath());
    }

    public static void getCommand(File dbFile, String[] parsedCommand) throws IOException {

    }

    public static void removeCommand(File dbFile, String[] parsedCommand) throws IOException {

    }
}

