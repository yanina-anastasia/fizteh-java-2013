    package ru.fizteh.fivt.students.mikhaylova_daria.db;

    import java.io.IOException;
    import java.util.HashMap;

    class FileMap {
        static HashMap<String, String> fileMap = new HashMap<String, String>();
        public static void main(String[] arg) {

        }

        public static void put(String[] arg) throws IOException {
            if (arg.length != 3) {
                throw new IOException("put: Wrong number of arguments");
            }
            if (!fileMap.containsValue(arg[2])) {
                 System.out.println("new");
            }
            if (fileMap.containsKey(arg[1])) {
                if (fileMap.containsKey(arg[1])) {
                    System.out.println("overwrite\n" + fileMap.get(arg[1]));
                }
            }
            fileMap.put(arg[1], arg[2]);
        }

        public static void get(String[] arg) throws IOException {
            if (arg.length != 2) {
                throw new IOException("get: Wrong number of arguments");
            }
            if (fileMap.containsKey(arg[1])) {
                System.out.println("found\n" + fileMap.get(arg[1]));
            } else {
                System.out.println("not found");
            }
        }

        public static void remove(String[] arg) throws  IOException {
            if (arg.length != 2) {
                throw new IOException("remove: Wrong number of arguments");
            }
            if (fileMap.containsKey(arg[1])) {
                fileMap.remove(arg[1]);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }

        public static void exit(String[] arg) {
            try {
                DbMain.writerDateBase();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            System.exit(0);
        }
    }
