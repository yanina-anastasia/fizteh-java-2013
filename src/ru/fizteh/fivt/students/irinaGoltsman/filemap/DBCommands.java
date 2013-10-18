package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.students.irinaGoltsman.shell.*;


public class DBCommands {
    public static class Put implements Command {
        protected final String name = "put";
        protected final int countOfArguments = 2;

        public String getName() {
            return name;
        }

        public int getCountOfArguments() {
            return countOfArguments;
        }

        public boolean check(String[] parts) {
            return ((parts.length - 1) >= countOfArguments);
        }

        public Code perform(String[] args) {
            return DataBase.put(args);
        }
    }

    public static class Get implements Command {
        protected final String name = "get";
        protected final int countOfArguments = 1;

        public String getName() {
            return name;
        }

        public int getCountOfArguments() {
            return countOfArguments;
        }

        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        public Code perform(String[] args) {
            return DataBase.get(args);
        }
    }

    public static class Remove implements Command {
        protected final String name = "remove";
        protected final int countOfArguments = 1;

        public String getName() {
            return name;
        }

        public int getCountOfArguments() {
            return countOfArguments;
        }

        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        public Code perform(String[] args) {
            return DataBase.remove(args);
        }
    }

}
