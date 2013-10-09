
public class Exit implements Commands {
    
    public String getCommandName() {
        return "exit";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrong {
        throw new SomethingIsWrong("EXIT");
    }

}
