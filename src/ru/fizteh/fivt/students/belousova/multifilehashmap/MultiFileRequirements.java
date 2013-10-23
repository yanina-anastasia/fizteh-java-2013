package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.filemap.Requirements;

import java.nio.charset.StandardCharsets;

public class MultiFileRequirements implements Requirements {
    private int nfile;
    private int ndirectory;

    public MultiFileRequirements(int nf, int nd) {
        nfile = nf;
        ndirectory = nd;
    }

    @Override
    public boolean isKeyValid(String key) {
        int keyByte = key.getBytes(StandardCharsets.UTF_8)[0] + 128;
        int nd = keyByte % 16;
        int nf = keyByte / 16 % 16;
        return (nd == ndirectory)&&(nf == nfile);
    }

    @Override
    public boolean isValueValid(String value) {
        return true;
    }
}
