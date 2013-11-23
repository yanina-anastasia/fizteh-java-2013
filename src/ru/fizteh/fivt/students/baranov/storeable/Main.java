package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
        public static void main(String[] arguments) {
            String path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                System.err.println("property fizteh.db.dir not found");
                System.exit(1);
            }
            
            State currentState = new State();
            Shell shell = new Shell(currentState);
            
            shell.commandList.put("put", new Put());
            shell.commandList.put("get", new Get());
            shell.commandList.put("remove", new Remove());
            shell.commandList.put("create", new Create());
            shell.commandList.put("drop", new Drop());
            shell.commandList.put("use", new Use());
            shell.commandList.put("commit", new Commit());
            shell.commandList.put("rollback", new Rollback());
            shell.commandList.put("size", new Size());
            shell.commandList.put("exit", new Exit());
            
            MyTableProviderFactory factory = new MyTableProviderFactory();
            try {
                currentState.tableProvider = factory.create(path);
            } catch (IllegalArgumentException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            }
            
            if (arguments.length != 0) {
                shell.pocketMode(arguments);
            } else {
                shell.interactiveMode();
            }
        }
}