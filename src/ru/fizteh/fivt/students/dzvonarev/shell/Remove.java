package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class Remove {

    private static void delete(File aim) throws IOException {
        if (aim.getName().equals(".") || aim.getName().equals("..")) {
            throw new IOException("rm: can't remove " + aim + " : forbidden");
        }
        if (aim.isDirectory()) {
            if (aim.list().length == 0) {
                if (!aim.delete()) {
                    throw new IOException("rm: can't remove " + aim + " : no such file or directory");
                }
            } else {
                String[] file = aim.list();
                for (int i = 0; i < file.length; ++i) {
                    File currFile = new File(aim, file[i]);
                    delete(currFile);
                }
                if (aim.list().length == 0) {
                    if (!aim.delete()) {
                        throw new IOException("rm: can't remove " + aim + " : no such file or directory");
                    }
                }
            }
        } else {
            if (!aim.delete()) {
                throw new IOException("rm: can't remove " + aim + " : no such file or directory");
            }
        }
    }

    public static void deleteObject(String expr, int spaceIndex) throws IOException {
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex != -1) {
            throw new IOException("Wrong parametres of remove");
        }
        String path = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        if (path.equals(Main.getCurrentDirectory())) {
            throw new IOException("rm: can't remove " + path);
        }
        if ((new File(path)).isFile() || (new File(path)).isDirectory()) {
            delete(new File(path));
        } else {
            throw new IOException("rm: can't remove " + path);
        }
    }

}
