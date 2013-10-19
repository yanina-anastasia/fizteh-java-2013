package shell;

public class Main {
    public static void main(String[] args) {
        Shell sl = new Shell();
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(' ');
            }

            try {
                sl.executeCommands(builder.toString());
            } catch (SException e) {
                System.err.println(e);
                System.exit(1);
            }

        } else {
            sl.iMode();
        }
    }
}
