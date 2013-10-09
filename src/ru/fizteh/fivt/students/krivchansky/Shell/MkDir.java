import java.io.File;


public class MkDir implements Commands {
    
    public String getCommandName() {
        return "mkdir";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement (String[] args, Shell.ShellState state) throws SomethingIsWrong{
        String nameOfDirectory = args [0];
        File creating = UtilMethods.getAbsoluteName(nameOfDirectory, state);
        if (!creating.mkdir()) {
            throw new SomethingIsWrong("Can't make a directory " + nameOfDirectory);
        }
    }
}