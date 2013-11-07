package ru.fizteh.fivt.students.drozdowsky;

import ru.fizteh.fivt.students.drozdowsky.database.FileHashMap;
import ru.fizteh.fivt.students.drozdowsky.database.MFHMProviderFactory;
import ru.fizteh.fivt.students.drozdowsky.database.MultiFileHashMap;

public class Test {
    public static void main(String[] args) {
        String workingDir = "/Users/00/Documents/Programming/Java/multibd";
        MFHMProviderFactory temp = new MFHMProviderFactory();
        MultiFileHashMap temp2= temp.create(workingDir);
        FileHashMap temp3 = temp2.getTable("asd");
        temp3.put("asd", null);
    }
}
