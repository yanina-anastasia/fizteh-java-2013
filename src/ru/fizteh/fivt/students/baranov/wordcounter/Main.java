package ru.fizteh.fivt.students.baranov.wordcounter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int main(String[] args) {
        if (args.length < 1) {
            System.err.println("wordcounter needs arguments");
            return 0;
        }

        boolean output = false;
        boolean aggregate = false;
        boolean outputIsFound = false;
        List<File> files = new ArrayList<File>();
        File outputFile = null;
        String dir = System.getProperty("user.dir");


        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-o")) {
                output = true;
                continue;
            }
            if (args[i].equals("-a")) {
                aggregate = true;
                continue;
            }
            if (output && !outputIsFound) {
                outputFile = new File(args[i]);
                outputIsFound = true;
                continue;
            }

            File newFile = new File(args[i]);
            if (!newFile.isAbsolute()) {
                newFile = new File(dir, args[i]);
            }

            files.add(newFile);
        }

        MyWordCounterFactory factory = new MyWordCounterFactory();
        MyWordCounter counter = factory.create();
        OutputStream stream;

        if (output) {
            if (outputIsFound) {
                try {
                    stream = new FileOutputStream(outputFile);
                } catch (FileNotFoundException e) {
                    System.err.println(e);
                    return 0;
                }
            } else {
                System.err.println("output not found");
                return 0;
            }
        } else {
            stream = System.out;
        }

        try {
            counter.count(files, stream, aggregate);
        } catch (IOException e) {
            System.err.println(e);
            return 0;
        }
        return 0;
    }
}
