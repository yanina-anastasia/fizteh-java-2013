package ru.fizteh.fivt.students.adanilyak.filemap;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:48
 */

public class CmdPut {
    private final RequestCommandTypeFileMap name = RequestCommandTypeFileMap.getType("put");
    private final int amArgs = 2;

    public RequestCommandTypeFileMap getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String key, String value, Shell shell) {
        String result = shell.put(key, value);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
    }
}
