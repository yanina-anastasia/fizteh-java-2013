package ru.fizteh.fivt.students.drozdowsky.shell;

import ru.fizteh.fivt.students.drozdowsky.shell.commands.ChangeDirectory;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.MakeDirectory;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.Copy;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.Directory;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.PrintWorkingDirectory;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.Remove;
import ru.fizteh.fivt.students.drozdowsky.shell.commands.Move;

public class Utils {
    public static boolean executeCommand(String[] args, PathController workingDirectory) {
        if (args.length == 0) {
            return true;
        }
        String command = args[0];
        if (command.equals("cd")) {
            ChangeDirectory cd = new ChangeDirectory(workingDirectory, args);
            return cd.execute();
        } else if (command.equals("mkdir")) {
            MakeDirectory mkdir = new MakeDirectory(workingDirectory, args);
            return mkdir.execute();
        } else if (command.equals("pwd")) {
            PrintWorkingDirectory pwd = new PrintWorkingDirectory(workingDirectory, args);
            return pwd.execute();
        } else if (command.equals("rm")) {
            Remove rm = new Remove(workingDirectory, args);
            return rm.execute();
        } else if (command.equals("dir")) {
            Directory dir = new Directory(workingDirectory, args);
            return dir.execute();
        } else if (command.equals("cp")) {
            Copy cp = new Copy(workingDirectory, args);
            return cp.execute();
        } else if (command.equals("mv")) {
            Move mv = new Move(workingDirectory, args);
            return mv.execute();
        } else if (command.equals("exit")) {
            System.exit(0);
        } else {
            System.err.println(args[0] + ": command not found");
            return false;
        }
        return true;
    }
}
