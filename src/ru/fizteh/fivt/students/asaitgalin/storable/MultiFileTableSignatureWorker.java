package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.students.asaitgalin.utils.StringUtils;

import java.io.*;
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
        return MultiFileTableUtils.getColumnTypes(types);
    }

    public void writeColumnTypes(List<Class<?>> columnTypes) {
        try {
            if (!signatureFile.exists()) {
                signatureFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile));
            List<String> types = new ArrayList<>();
            for (Class<?> cl : columnTypes) {
                types.add(MultiFileTableUtils.getColumnTypeString(cl));
            }
            writer.write(StringUtils.join(types, " "));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
