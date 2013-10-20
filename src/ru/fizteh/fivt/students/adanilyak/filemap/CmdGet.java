package ru.fizteh.fivt.students.adanilyak.filemap;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:48
 */

public class CmdGet {
    private final RequestCommandTypeFileMap name = RequestCommandTypeFileMap.getType("get");
    private final int amArgs = 1;

    public RequestCommandTypeFileMap getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String key, Shell shell) {
        String result = shell.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
    }
}
