package ru.fizteh.fivt.students.eltyshev.shell;

import java.io.IOException;
import java.io.File;

import java.nio.file.*;

public class Program {
    public static void main(String[] Args) throws IOException {
        Shell shell = new Shell(Args);
        shell.start();
    }
}
