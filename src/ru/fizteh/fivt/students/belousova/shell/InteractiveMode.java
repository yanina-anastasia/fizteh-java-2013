package ru.fizteh.fivt.students.belousova.shell;

import java.io.InputStream;
import java.util.Scanner;

public class InteractiveMode {

    public static void work(InputStream inputStream) {

        while (true) {

            System.out.print("$ ");
            Scanner scanner = new Scanner(inputStream);
            String s = scanner.nextLine();
            StringHandler.handle(s);
        }
    }
}
