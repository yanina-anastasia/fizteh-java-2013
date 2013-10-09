import java.io.File;


public class Rm implements Commands {
    
    public String getCommandName() {
        return "rm";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrong {
        String whoToDelete = args[0];
        File deleteIt = UtilMethods.getAbsoluteName(whoToDelete, state);
        try {
            if (deleteIt.exists()) {
                if(!deleteIt.delete()) {
                    throw new SomethingIsWrong("Error acquired while deleting a file");
                }
            } else {
                throw new SomethingIsWrong ("The file, you want to delete, doesn't exist.");
            }
        } catch (SecurityException e) {
            throw new SomethingIsWrong("You don't have enogh rights to delete this file. " + e.getMessage());
        }
    }

}
