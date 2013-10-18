package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class RemoveCommand implements Command {
    private Launcher fileMapLauncher;

    public RemoveCommand(Launcher myFileMapLauncher) {
        fileMapLauncher = myFileMapLauncher;
    }

    @Override
    public boolean exec(String[] command) {
        if (command.length != 2) {
            System.err.println("remove: Usage - remove key");
            return false;
        }
        try {
            String str = fileMapLauncher.getFileMap().remove(command[1]);
            if (str.equals("not found")) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (Exception e) {
            System.err.println("remove: Can't remove the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "remove";
    }
}
