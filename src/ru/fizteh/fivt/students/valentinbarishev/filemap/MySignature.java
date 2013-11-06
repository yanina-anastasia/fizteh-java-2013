package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MySignature {
    static final String[] types = {"int", "long", "byte", "float", "double", "boolean", "String"};
    static final Class<?>[] classes = {Integer.class, Long.class, Byte.class, Float.class,
                                       Double.class, Boolean.class, String.class};


    public static List<Class<?>> getSignature(final String dir) throws IOException {
        File file = new File(dir, "signature.tsv");
        if (!file.exists()) {
            throw new IOException("Cannot find file:" + file.getCanonicalPath());
        }
        Scanner input = new Scanner(file);
        String[] data = input.next().split(" ");

        List<Class<?>> result = new ArrayList<>();

        for (int i = 0; i < data.length; ++i) {
            boolean flag = false;
            for (int j = 0; j < types.length; ++j) {
                if (data[i].equals(types[j])) {
                    result.add(classes[j]);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new IOException("Invalid type!");
            }
        }
        return result;
    }

    public static void setSignature(final String dir, List<Class<?>> classesList) throws IOException {
        PrintWriter output = new PrintWriter(new File(dir, "signature.tsv"));
        for (int i = 0; i < classesList.size(); ++i) {
            for (int j = 0; j < classes.length; ++j) {
                if (classes[j].equals(classesList.get(i))) {
                    output.write(types[j]);
                    break;
                }
            }
            if (i + 1 != classesList.size()) {
                output.write(" ");
            }
        }
        output.close();
    }
}
