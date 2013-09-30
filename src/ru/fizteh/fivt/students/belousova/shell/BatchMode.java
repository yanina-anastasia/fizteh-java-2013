package ru.fizteh.fivt.students.belousova.shell;

public class BatchMode {
    public static void work(String[] args) {
//        StringBuilder sb = new StringBuilder();
        for (String si : args) {
//            sb.append(si);
//            sb.append(" ");
            StringHandler.handle(si);
        }
//        String s = sb.toString();
//        StringHandler.handle(s);
    }
}
