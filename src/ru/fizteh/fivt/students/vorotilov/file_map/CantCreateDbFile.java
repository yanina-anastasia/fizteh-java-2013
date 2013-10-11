package ru.fizteh.fivt.students.vorotilov.file_map;

public class CantCreateDbFile extends Exception {
    String problematicFile;

    public CantCreateDbFile(String file) {
        problematicFile = file;
    }

    public String getProblematicFile() {
        return problematicFile;
    }
}

