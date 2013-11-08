package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;

public class CommandCreate implements Command<StoreableState> {
    public String getName() {
        return "create";
    }

    public int getArgCount() {
        return -1;
    }

    public void run(StoreableState state, String[] args) throws Exception {
        File tableDir = state.getPath().resolve(args[0]).toFile();
        if (args[0].contains(".")) {
            System.err.println("Dots in table name.");
            throw new RuntimeException("Dots in table name.");
        }
        if (tableDir.exists()) {
            System.out.println(args[0] + " exists");
        } else {
            tableDir.mkdir();
            String[] types = new String[args.length - 1];
            System.arraycopy(args, 1, types, 0, args.length - 1);
            if (types.length == 0) {
                tableDir.delete();
                System.out.println("wrong type (signature expected)");
                throw new IllegalArgumentException("Signature exprcted.");
            }
            types[0] = types[0].substring(1);
            types[types.length - 1] = types[types.length - 1].substring(0, types[types.length - 1].length() - 1);
            try {
                Utils.writeColumnTypes(state.getPath().resolve(args[0]).toString(), types);
                state.createTable(args[0]);
            } catch (Exception e) {
                File signature = new File(tableDir, "signature.tsv");
                signature.delete();
                tableDir.delete();
                throw e;
            }
            System.out.println("created");
        }
    }
}