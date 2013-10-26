package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import java.io.File;
import java.io.IOException;

public class Main {
    private static MyHashMap dtb;
    public static void main(String[] args) throws IOException {
        try {
            String way = System.getProperty("fizteh.db.dir");
            File dbsDir = new File(way);
            if (!dbsDir.isDirectory()) {
                throw new Exception(dbsDir + " doesn't exist or is not a directory");
            }
           /* File[] files = dbsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if(file.isFile()) {
                    throw new Exception("It's a file in a root directory.");
                    }
                }
            } */
        } catch (Exception e) {
            System.out.println("Error while opening database: " + (e.getMessage()));
            System.exit(1);
        }

        dtb = new MyHashMap();
        Shell sl = new Shell(dtb);
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(' ');
            }

            try {
                sl.executeCommands(builder.toString());
            } catch (SException e) {
                System.err.println(e);
                System.exit(1);
            }
            catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
        } else {
            sl.iMode();
        }
    }
}
