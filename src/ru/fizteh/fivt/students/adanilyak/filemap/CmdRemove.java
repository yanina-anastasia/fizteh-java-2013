package ru.fizteh.fivt.students.adanilyak.filemap;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:49
 */

public class CmdRemove {
    private final RequestCommandTypeFileMap name = RequestCommandTypeFileMap.getType("remove");
    private final int amArgs = 1;

    public RequestCommandTypeFileMap getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String key, Shell shell) {
        String result = shell.remove(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
