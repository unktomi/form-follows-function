package f3.util;

public enum Comparison 
{
    Less, Equal, Greater;

    public static Comparison fromInteger(int i) 
    {
	return (i < 0) ? Less : i > 0 ? Greater : Equal;
    }

}