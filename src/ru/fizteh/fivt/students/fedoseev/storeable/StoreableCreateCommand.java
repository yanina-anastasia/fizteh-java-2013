package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class StoreableCreateCommand extends AbstractCommand<StoreableState> {
    public StoreableCreateCommand() {
        super("create", -1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException, ClassNotFoundException {
        String tableName = input[0];
        File curTableDir = new File(state.getCurDir(), tableName);

        if (!tableName.matches("[^><\"?|*.]*")) {
            throw new RuntimeException("CREATE ERROR: illegal symbol in table name");
        }

        if (input.length < 2) {
            ifWrongType(curTableDir);
        }

        if (!input[1].contains("(") || !input[input.length - 1].contains(")")) {
            ifWrongType(curTableDir);
        }

        input[1] = input[1].replace("(", "");
        input[input.length - 1] = input[input.length - 1].replace(")", "");

        if (input[1].trim().isEmpty()) {
            ifWrongType(curTableDir);
        }

        for (int i = 1; i < input.length; i++) {
            if (!input[i].trim().matches("(boolean|byte|double|float|int|long|String)")) {
                ifWrongType(curTableDir);
            }
        }

        if (!curTableDir.exists()) {
            curTableDir.mkdirs();

            String[] columnTypes = Arrays.copyOfRange(input, 1, input.length);

            try {
                AbstractStoreable.writeTypes(curTableDir.toString(), columnTypes);
                state.createTable(tableName);
            } catch (Exception e) {
                File signatureFile = new File(curTableDir, "signature.tsv");

                signatureFile.delete();
                curTableDir.delete();

                throw e;
            }

            System.out.println("created");
        } else {
            System.out.println(tableName + " exists");
        }
    }

    private void ifWrongType(File curTableDir) {
        curTableDir.delete();

        System.out.println("wrong type (CREATE ERROR: no signature)");
        throw new IllegalArgumentException("CREATE ERROR: no signature");
    }
}
