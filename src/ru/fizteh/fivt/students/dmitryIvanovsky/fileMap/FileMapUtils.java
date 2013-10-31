package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

public class FileMapUtils {
    static int getCode(String s) {
        if (s.charAt(1) == '.') {
            return Integer.parseInt(s.substring(0, 1));
        } else {
            return Integer.parseInt(s.substring(0, 2));
        }
    }

    static int getHashDir(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % 16;
        if (ndirectory < 0) {
            ndirectory *= -1;
        }
        return ndirectory;
    }

    static int getHashFile(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / 16 % 16;
        if (nfile < 0) {
            nfile *= -1;
        }
        return nfile;
    }

    static String[] myParsing(String[] args) {
        String arg = args[0].trim();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int i = 0;
        while (i < arg.length() && arg.charAt(i) != ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) != ' ') {
            key.append(arg.charAt(i));
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length()) {
            value.append(arg.charAt(i));
            ++i;
        }
        return new String[]{key.toString(), value.toString()};
    }

    public static void getMessage(Exception e) {
        if (e.getMessage() != null) {
            errPrint(e.getMessage());
        }
        for (int i = 0; i < e.getSuppressed().length; ++i) {
            errPrint(e.getSuppressed()[i].getMessage());
        }
    }

    static void errPrint(String message) {
        //if (err) {
            System.err.println(message);
        //}
    }

    static void outPrint(String message) {
        //if (out) {
            System.out.println(message);
        //}
    }

}
