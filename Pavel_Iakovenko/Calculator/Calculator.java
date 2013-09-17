

public class Calculator {

    private static boolean BracketsSum (String s){
        int count = 0;
        for ( int i = 0; i < s.length(); ++i){
            if (s.charAt(i) == '(')
                ++count;
            else if (s.charAt(i) == ')')
                --count;
        }
        return (count == 0);
    }

    public static void main ( String[] args){
        StringBuilder s = new StringBuilder();
        for ( int i = 0; i < args.length; ++i)
            s.append(args[i]);
        if ( s.toString() == ""){
            System.out.println("No program arguments!");
            System.exit(-1);
        }
        if ( ! BracketsSum (s.toString())) {
            System.out.println("Wrong brackets!");
            System.exit(-1);
        }
        Tree t = new Tree();
        Node p = t.Parser(s.toString());
        if ( p.GetString().equals(""))
           System.out.println('0');
        else
           System.out.println(p.GetString());

    }
}
