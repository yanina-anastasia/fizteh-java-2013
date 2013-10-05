package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class CommandsMap {
    private Map<String, Commands> commands = new HashMap<String, Commands>();

    public void addCommand(Commands command) {
        if (!commands.containsKey(command.getName())){
             commands.put(command.getName(), command);
        }
    }

    public void execute(String commandName, String arguments) throws MyException, IOException {
        if (commands.containsKey(commandName)){
            commands.get(commandName).perform(arguments);
        } else {
            throw new MyException("No command with such name: " + commandName);
        }
    }

}
