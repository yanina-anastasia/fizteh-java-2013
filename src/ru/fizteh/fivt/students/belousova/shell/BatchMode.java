package ru.fizteh.fivt.students.belousova.shell;

public class BatchMode {
    public static void work(String[] args) {
        for (String s : args) {
            StringHandler.handle(s);
        }
    }
}
