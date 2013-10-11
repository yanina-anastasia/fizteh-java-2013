package ru.fizteh.fivt.students.karpichevRoman.shell;

class Main {
    public static void main(String[] args) throws IllegalArgumentException {
        Shell shell = new Shell();
        if (args.length == 0) {
            shell.execShell();
        } else {
            StringBuilder resultCommandSeq = new StringBuilder();
            for (String i : args) {
                resultCommandSeq.append(" ");
                resultCommandSeq.append(i);
            }

            shell.runCommandSeq(resultCommandSeq.toString());
        }
    }
}












