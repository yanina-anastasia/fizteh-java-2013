package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.io.File;

public class CommandDrop implements Command {

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        File victimTable = new File(MultiFileHashMap.rootDir.getAbsoluteFile() + File.separator + args[0]);
        if (!victimTable.exists()) {
            System.out.println(args[0] + " not exists");
            return;
        }

        if (victimTable.getAbsoluteFile().equals(DatabaseWorker.currentTable.getAbsoluteFile())) {
            DatabaseWorker.storage.clear();
        }

        for (Integer i = 0; i < 16; ++i) {
            String dirName = i.toString() + ".dir";

            for (Integer j = 0; j < 16; ++j) {
                String fileName = dirName + File.separator + j.toString() + ".dat";

                File victim = new File(victimTable.getAbsoluteFile() + File.separator + fileName);
                if (victim.exists() && !victim.delete()) {
                    throw new Exception("Couldn't delete the file " + fileName);
                }

            }

            File victimDir = new File(victimTable.getAbsoluteFile() + File.separator + dirName);
            if (victimDir.exists() && !victimDir.delete()) {
                throw new Exception("Couldn't delete the directory " + dirName
                        + ". Maybe there are some unexpected files inside it.");
            }

        }

        if (!victimTable.delete()) {
            throw new Exception("Couldn't delete the directory " + args[0]);
        }

        System.out.println("dropped");

    }
}
