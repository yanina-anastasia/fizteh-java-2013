package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ShellState {
    private Path currentPath;
    public int copyMade = 0;

    ShellState(Path path) {
        this.currentPath = path;
    }

    public Path getCurrentPath() {
        return currentPath.toAbsolutePath();
    }

    public void changeCurrentPath(Path newPath) {
        currentPath = newPath;
    }

    /*public void MakeCurrentPathNormal() {
        String path = currentPath.toString();
        String[] arr = path.split("/");
        int l = arr.length;
        int[] used = new int[l];
        Arrays.fill(used, 1);
        for (int i = l - 1; i >= 0; --i) {
            if (arr[i].equals("..")) {
                used[i] = 0;
                for (int j = i - 1; j >= 0; --j)
                    if (used[j] == 1 && !arr[j].equals("..")) {
                        used[j] = 0;
                        break;
                    }
            }
        }

        String newStr = "";
        for (int i = 0; i < l; ++i) {
            if (used[i] == 1) {
                newStr = newStr + "/" + arr[i];
            }
        }

        currentPath = Paths.get(newStr);
    } */
}
