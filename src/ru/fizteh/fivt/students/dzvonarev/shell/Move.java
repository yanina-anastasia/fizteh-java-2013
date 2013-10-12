package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class Move {

  private static boolean isMoved(String source, String destination) {
    File sourceFile = new File(source);
    if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
      return false;
    }
    if (sourceFile.isFile() || sourceFile.isDirectory()) {
      File destFile = new File(destination);
      if (destFile.isDirectory()) {
        if (!sourceFile.renameTo(new File(destination + File.separator + sourceFile.getName()))) {
          return false;
        }
        return true;
      } else {
        if (destFile.isFile()) {
          return false;
        }
        if (!sourceFile.renameTo(new File(destination))) {
          return false;
        }
        return true;
      }
    }
    return true;
  }

  public static void moveObject(String expr, int spaceIndex) throws IOException {
    int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
    if (newSpaceIndex == -1) {
      throw new IOException("mv: wrong parametres");
    }
    if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
      throw new IOException("mv: wrong parametres");
    }
    String source = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, newSpaceIndex));
    String destination = DoCommand.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
    if (destination.contains(source)) {    // if parent into child
      throw new IOException("mv: can't move " + source);
    }
    if (!isMoved(source, destination)) {
      throw new IOException("mv: can't move " + source);
    }
  }

}
