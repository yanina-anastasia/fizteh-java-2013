package ru.fizteh.fivt.students.asaitgalin.storable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultiFileTableSignatureWorker {
    private static final String TABLE_SIGNATURE_FILE = "signature.tsv";
    private File signatureFile;

    public MultiFileTableSignatureWorker(File tableDirectory) {
        signatureFile = new File(tableDirectory, TABLE_SIGNATURE_FILE);
    }

    public List<Class<?>> readColumnTypes() throws IOException {
        Scanner scanner = new Scanner(new FileInputStream(signatureFile));
        if (!scanner.hasNextLine()) {
            throw new IOException("signature file is empty");
        }
        String[] types = scanner.nextLine().split("\\s");
        List<Class<?>> columnsList = new ArrayList<>();
        for (String s : types) {
            switch (s) {
                case "int":
                    columnsList.add(Integer.class);
                    break;
                case "long":
                    columnsList.add(Long.class);
                    break;
                case "byte":
                    columnsList.add(Byte.class);
                    break;
                case "float":
                    columnsList.add(Float.class);
                    break;
                case "double":
                    columnsList.add(Double.class);
                    break;
                case "boolean":
                    columnsList.add(Boolean.class);
                    break;
                case "String":
                    columnsList.add(String.class);
                    break;
                default:
                    throw new RuntimeException("invalid column type in signature file");
            }
        }

        return columnsList;

    }

    public void writeColumnTypes(List<Class<?>> columnTypes) {
        //
    }

}
