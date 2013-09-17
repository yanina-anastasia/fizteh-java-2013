import static java.lang.Math.pow;

public class Node {
    private String s;
    private boolean isSymbol;
    private Node rightNode;
    private Node leftNode;

    private int ToNumber(String s, int dec){
        int p = 0;
        for (int i = s.length() -1; i >= 0; --i){
            if (s.charAt(i) >= 'A'){
                p += (s.charAt(i) - 'A' + 10) * pow((double)dec, s.length() - i - 1  );
            }
            else if (s.charAt(i) >= '0')
                p += (s.charAt(i) - '0') * pow((double)dec, s.length() - i - 1  );
        }
        if (!s.isEmpty() && s.charAt(0) == '-')
            p *= -1;
        return p;
    }

    private String ToString(int n, int dec){
        StringBuilder t = new StringBuilder();
        if (n < 0){
            n *= -1;
            while ( n > 0){
                int temp = n % dec;
                if ( temp < 10)
                    t.insert(0,(char) (temp + '0'));
                else
                    t.insert(0,(char)(temp - 10 + 'A'));
                n /= dec;
            }
            t.insert(0,"-");
            return t.toString();
        }
        while ( n > 0){
            int temp = n % dec;
            if ( temp < 10)
                t.insert(0,(char) (temp + '0'));
            else
                t.insert(0,(char)(temp - 10 + 'A'));
            n /= dec;
        }
        return t.toString();
    }

    private int Calculation(int dec){
        if (s.equals("*"))
            return ToNumber(leftNode.GetString(),dec)*ToNumber(rightNode.GetString(),dec);
        else if (s.equals("/")){
            if (ToNumber(rightNode.GetString(),dec) == 0 ){
                System.out.println("Programm tried to divide by zero");
                System.exit(-1);
            }
            return (ToNumber(leftNode.GetString(),dec) / ToNumber(rightNode.GetString(),dec));
        }
        else if (s.equals("-")){
            return (ToNumber(leftNode.GetString(),dec) - ToNumber(rightNode.GetString(),dec));
        }
        else
            return (ToNumber(leftNode.GetString(), dec) + ToNumber(rightNode.GetString(),dec));
    }

    public Node (String s1){
        this.s = s1;
        this.isSymbol = false;
        this.leftNode = new Node();
        this.rightNode = new Node();
    }

    public Node (){
        this.s = "";
        this.isSymbol = false;
    }

    public Node (String s1, boolean flag){
        this.s = s1;
        this.isSymbol = flag;
        this.leftNode = new Node();
        this.rightNode = new Node();
    }

    public Node ( String s1, Node right, Node left){
        this.s = s1;
        this.rightNode = right;
        this.leftNode = left;
        this.isSymbol = false;
    }

    public boolean IsEmpty (){
    if (s ==  "")
        return true;
    return false;
    }

    public void AddRightNode (Node n){
        this.rightNode = n;
    }

    public void AddLeftNode (Node n){
        this.leftNode = n;
    }
    public String GetString (){
        return s;
    }

    public void ChangeTypeOfSymbol(boolean flag){
        this.isSymbol = flag;
    }

    public boolean IsSymbol (){
        return isSymbol;
    }

    public boolean Equal (String t){
        return this.s.equals(t);
    }

    public void Calculate(){
        if (this.leftNode.IsEmpty() && this.rightNode.IsEmpty())
            return;
        if ( !this.leftNode.IsEmpty())
            leftNode.Calculate();
        if ( !this.rightNode.IsEmpty())
            rightNode.Calculate();
        this.s = ToString(Calculation(19), 19);
        leftNode.s = "";
        rightNode.s = "";
    }
}
