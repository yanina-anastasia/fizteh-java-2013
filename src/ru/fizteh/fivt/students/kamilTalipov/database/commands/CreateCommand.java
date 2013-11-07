package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiTableDatabase;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

import java.util.ArrayList;

public class CreateCommand extends SimpleCommand {
    public CreateCommand(MultiTableDatabase database) {
        super("create", -2);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException(name + ": expected at least 2 arguments "
                                                + args.length + " got");
        }

        ArrayList<Class<?>> types = new ArrayList<>();
        for (int i = 1; i < args.length; ++i) {
            switch (args[i]) {
                case "Integer": case "int":
                    types.add(Integer.class);
                    break;

                case "Long": case "long":
                    types.add(Long.class);
                    break;

                case "Byte": case "byte":
                    types.add(Byte.class);
                    break;

                case "Float": case "float":
                    types.add(Float.class);
                    break;

                case "Double": case "double":
                    types.add(Double.class);
                    break;

                case "Boolean": case "boolean":
                    types.add(Boolean.class);
                    break;

                case "String":
                    types.add(String.class);
                    break;

                default:
                    System.err.println("Unsupported type " + args[i]);
                    return;
            }
        }

        if (database.createTable(args[0], types)) {
            System.out.println("created");
        }  else {
            System.out.println(args[0] + " exists");
        }
    }

    private final MultiTableDatabase database;
}
