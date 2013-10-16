package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import java.util.*;

public class MapOfCommands {
    private static Map<String, Command> commands = new HashMap<String, Command>();

    public void addCommand(Command command) {
        if (!commands.containsKey(command.getName())) {
            commands.put(command.getName(), command);
        }
    }

    public static Code commandProcessing(String command) {
        StringTokenizer st = new StringTokenizer(command, " \t", false);
        ArrayList<String> parts = new ArrayList<String>();
        while (st.hasMoreElements()) {
            String tmp = (String) st.nextElement();
            if (!tmp.equals("")) {
                parts.add(tmp);
            }
        }
        String[] partsOfCommand = new String[parts.size()];
        for (int i = 0; i < parts.size(); i++) {
            partsOfCommand[i] = parts.get(i);
        }
        String nameOfCommand = partsOfCommand[0];
        if (!commands.containsKey(nameOfCommand)) {
            System.err.println("Command '" + nameOfCommand + "' is not available or does not exist");
            return Code.ERROR;
        } else {
            if (commands.get(nameOfCommand).check(partsOfCommand)) {
                return commands.get(nameOfCommand).perform(partsOfCommand);
            } else {
                System.err.println("Command '" + nameOfCommand + "' has wrong arguments");
                return Code.ERROR;
            }
        }
    }
}