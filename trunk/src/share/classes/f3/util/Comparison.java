package f3.util;

public enum Comparison 
{
    Less, Equal, Greater;

    public static Comparison fromInteger(int i) 
    {
	return (i < 0) ? Less : (i > 0) ? Greater : Equal;
    }

    public int toInteger()
    {
	return this == Less ? -1 : this == Equal ? 0 : 1;
    }

}