import java.util.ArrayList;
import java.util.List;


public class Tree {
    public Tree(){
    }

    private String CopyString(String s, int from, int to){
        StringBuilder t  = new StringBuilder(5);
        for ( int i = from; i <= to; ++i){
            t.append(s.charAt(i));
        }
        return t.toString();
    }

    private int FindBracket ( String s, int from){
        int bracketsSum = 0;
        for ( int i = from; i < s.length(); ++i){
            if ( s.charAt(i) == '(')
                ++bracketsSum;
            else if ( s.charAt(i) == ')')
                --bracketsSum;
            if ( bracketsSum == 0)
                return i;
        }
        return -1;
    }

    private String DeleteString(String s, int from, int to){
        StringBuilder temp = new StringBuilder();
        for ( int i = 0; i < from; ++i)
            temp.append(s.charAt(i));
        for ( int i = to +1; i < s.length(); ++i )
            temp.append(s.charAt(i));
        return temp.toString();
    }

    public List <Node> FirstTime (String s){
        List <Node> l =  new ArrayList<Node>();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < s.length(); ++i){
            if ( s.charAt(i) == '('){
                int tp = FindBracket(s,i);
                l.add( Parser( CopyString( s, i + 1,  tp - 1 ) ));
                s = DeleteString(s, i, tp );
                --i;
                continue;
            }
            if (s.charAt(i) =='\t' || s.charAt(i) =='\n' || s.charAt(i) ==' ')
                continue;
            while ( i < s.length() && s.charAt(i) != '+' && s.charAt(i) != '-' && s.charAt(i) != '*' && s.charAt(i) != '/') {
                if (s.charAt(i) =='\t' || s.charAt(i) =='\n' || s.charAt(i) ==' '){
                    ++i;
                    continue;
                }
                temp.append(s.charAt(i));
                ++i;
            }
            if (!temp.equals("")){
                l.add(new Node(temp.toString()));
                temp.delete(0, temp.length());
            }
            if (i < s.length() - 1){
                temp.append(s.charAt(i));
                l.add(new Node(temp.toString(), true));
                temp.delete(0, temp.length());
            }
        }
        return l;
    }

    public Node SecondTime (List < Node> l ){
        for ( int i =0; i < l.size(); ++i){
            if ( l.get(i).Equal("*") || l.get(i).Equal("/") ){
                l.get(i).AddLeftNode(l.remove(i - 1));
                l.get(i-1).AddRightNode(l.remove(i));
                --i;
            }
        }
        for ( int i =1; i < l.size(); ++i){
                l.get(i).AddLeftNode(l.remove(i-1));
                l.get(i-1).AddRightNode(l.remove(i));
                --i;
        }
        return l.get(0);
    }

    public Node Parser (String s){
        List <Node> l =  FirstTime(s);
        Node f = new Node();
        f = SecondTime(l);
        f.Calculate();
        return f;
    }

}
