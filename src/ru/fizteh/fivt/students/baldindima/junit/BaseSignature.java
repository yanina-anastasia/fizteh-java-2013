package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BaseSignature {

    static final String[] TYPES = {"int", "long", "byte", "float", "double", "boolean", "String"};
    static final Class<?>[] CLASSES = {Integer.class, Long.class, Byte.class, Float.class,
            Double.class, Boolean.class, String.class};


    public static void setBaseSignature(String nameDirectory, List<Class<?>> types) throws IOException {


        File fileForSignature = new File(nameDirectory, "signature.tsv");

        try (PrintWriter output = new PrintWriter(fileForSignature)) {
            for (int i = 0; i < types.size(); ++i) {
                boolean isTypeCorrect = false;
                for (int j = 0; j < CLASSES.length; ++j) {
                    if (CLASSES[j].equals(types.get(i))) {
                        output.write(TYPES[j]);
                        isTypeCorrect = true;
                        break;
                    }
                }
                if (!isTypeCorrect) {
                    throw new IllegalArgumentException("There is no such types!");
                }
                if (i + 1 != types.size()) {
                    output.write(" ");
                }
            }
        }


    }

    public static List<Class<?>> getBaseSignature(String directoryName) throws IOException {
        File file = new File(directoryName, "signature.tsv");
        if (!file.exists()) {
            throw new IOException("Cannot find file");
        }
        Scanner input = new Scanner(file);
        if (!input.hasNext()) {
            throw new IOException("Empty signature");
        }

        StringBuilder builder = new StringBuilder();
        while (input.hasNext()) {
            builder.append(input.next()).append(" ");
        }

        String[] signatureFromFile = builder.toString().split(" ");

        List<Class<?>> signature = new ArrayList<>();

        if (signatureFromFile.length <= 0) {
            throw new IOException("Empty signature");
        }

        for (String type : signatureFromFile) {
            boolean isTypeCorrect = false;
            for (int j = 0; j < TYPES.length; ++j) {
                if (type.equals(TYPES[j])) {
                    signature.add(CLASSES[j]);
                    isTypeCorrect = true;
                    break;
                }
            }
            if (!isTypeCorrect) {
                throw new IOException("There is no such type");
            }
        }
        return signature;
    }


    public static List<Class<?>> getTypes(String str) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        byte[] s = str.trim().getBytes();
        if (!(s[0] == '(' && s[str.length() - 1] == ')')) {
            throw new IOException("wrong type (no brackets)");
        }
        for (int i = 1; i < str.length() - 1; ++i) {
            if (s[i] == ' ') {
                continue;
            }

            boolean flag = false;
            for (int j = 0; j < TYPES.length; ++j) {
                if (new String(s, i, Math.min(TYPES[j].length(), str.length() - i)).equals(TYPES[j])) {
                    result.add(CLASSES[j]);
                    i += TYPES[j].length();
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                throw new IOException("Cannot read type!");
            }
        }
        return result;
    }
}