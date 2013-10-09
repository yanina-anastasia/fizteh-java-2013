import java.io.File;




public class Mv implements Commands {
    
    public String getCommandName() {
        return "mv";
    }

    public int getArgumentQuantity() {
        return 2;
    }
    
    private void shift(File from, File to) throws SomethingIsWrong {
        if (from.isFile()) {
            Cp.copy(from, to);
        } else {
            File newPlace = new File(to, from.getName());
            if (!newPlace.exists() || !newPlace.mkdir()) {
                throw new SomethingIsWrong("Unable to create new directory " + from.getName());
            }
            for (String tmp : from.list()) {
                shift(new File(from, tmp), newPlace);
            }
            from.delete();
        }
    }
    
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrong {
        String from = args[0];
        String to = args[1];
        File source = UtilMethods.getAbsoluteName(from, state);
        File destination = UtilMethods.getAbsoluteName(to, state);
        if (!source.exists()) {
            throw new SomethingIsWrong ("The file " + from + " doesn't exist.");
        }
        if (source.getParent().equals(destination.getParent()) && destination.isDirectory()) {
            if (!source.renameTo(destination)) {
                throw new SomethingIsWrong("Error acquired while renaming the " + from + " to " + to);
            }
            return;
        }
        if (!destination.isDirectory()) {
            if(!source.renameTo(destination)) {
                throw new SomethingIsWrong(to + " is not a directory name");
            }
            return;
        }
        shift(source, destination);
        
    }
}
