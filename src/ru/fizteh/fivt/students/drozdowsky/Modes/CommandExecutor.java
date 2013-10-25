package ru.fizteh.fivt.students.drozdowsky.Modes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class CommandExecutor<T> {
    T controller;
    public CommandExecutor(T controller) {
        this.controller = controller;
    }

    public boolean executeCommand(String[] args, Method command) {
        try {
            if (Modifier.isStatic(command.getModifiers())) {
                return (boolean) command.invoke(null, controller, args);
            } else {
                return (boolean) command.invoke(controller, new Object[]{args});
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean execute(String[] args, HashMap<String, Method> commands) {
        Method command = commands.get(args[0]);
        if (command != null) {
            return executeCommand(args, command);
        } else {
            System.err.println(args[0] + ": command not found");
        }
        return false;
    }
}
