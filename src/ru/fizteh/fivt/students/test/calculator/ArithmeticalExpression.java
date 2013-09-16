
public class ArithmeticalExpression 
{
	private final String value;
	
	public ArithmeticalExpression (String source) 
	{
		String sourceWithoutSpaces = deleteSpaces (source);
		value = coverWithBrackets (sourceWithoutSpaces);
		System.out.println (value);
	}
	public String toString ()
	{
		return value;
	}
	
	private String deleteSpaces (String source)
	{
		String result = source.replace(" ", "");
		return result;
	}
	
	private String coverWithBrackets (String source)
	{
		String result = "(" + source + ")";
		return result;
	}

}
