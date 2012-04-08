/*
 * @subtest
 */
public class MethodOverloadJavaClass {
    public static int isInt = 1;
    public static int isLong = 2;
    public static int isByte = 3;
    public static int isShort = 4;
    public static int isChar = 5;
    public static int isFloat = 6;
    public static int isDouble = 7;
    public static int isByteLong = 8;
    public static int isLongByte = 9;

    public static int returnType = 0;

    public int testOverloadRetInt(int x) { return isInt;}
    public int testOverloadRetInt(long x) { return isLong;}
    public int testOverloadRetInt(byte x) { return isByte;}
    public int testOverloadRetInt(short x) { return isShort;}
    public int testOverloadRetInt(char x) { return isChar;}
    public int testOverloadRetInt(float x) { return isFloat;}
    public int testOverloadRetInt(double x) { return isDouble;}
    public int testOverloadRetInt(byte x, long y) { return isByteLong;}
    public int testOverloadRetInt(long x, byte y) { return isLongByte;}

    public void testOverloadRetVoid(int x) { returnType = isInt;}
    public void testOverloadRetVoid(long x) { returnType = isLong;}
    public void testOverloadRetVoid(byte x) { returnType = isByte;}
    public void testOverloadRetVoid(short x) { returnType = isShort;}
    public void testOverloadRetVoid(char x) { returnType = isChar;}
    public void testOverloadRetVoid(float x) { returnType = isFloat;}
    public void testOverloadRetVoid(double x) { returnType = isDouble;}
    public void testOverloadRetVoid(byte x, long y) { returnType = isByteLong;}
    public void testOverloadRetVoid(long x, byte y) { returnType = isLongByte;}

    public int testOverloadWrapperRetInt(Integer x) { return isInt;}
    public int testOverloadWrapperRetInt(Long x) { return isLong;}
    public int testOverloadWrapperRetInt(Byte x) { return isByte;}
    public int testOverloadWrapperRetInt(Short x) { return isShort;}
    public int testOverloadWrapperRetInt(Character x) { return isChar;}
    public int testOverloadWrapperRetInt(Float x) { return isFloat;}
    public int testOverloadWrapperRetInt(Double x) { return isDouble;}
    public int testOverloadWrapperRetInt(Byte x, Long y) { return isByteLong;}
    public int testOverloadWrapperRetInt(Long x, Byte y) { return isLongByte;}

    public void testOverloadWrapperRetVoid(Integer x) { returnType = isInt;}
    public void testOverloadWrapperRetVoid(Long x) { returnType = isLong;}
    public void testOverloadWrapperRetVoid(Byte x) { returnType = isByte;}
    public void testOverloadWrapperRetVoid(Short x) { returnType = isShort;}
    public void testOverloadWrapperRetVoid(Character x) { returnType = isChar;}
    public void testOverloadWrapperRetVoid(Float x) { returnType = isFloat;}
    public void testOverloadWrapperRetVoid(Double x) { returnType = isDouble;}
    public void testOverloadWrapperRetVoid(Byte x, Long y) { returnType = isByteLong;}
    public void testOverloadWrapperRetVoid(Long x, Byte y) { returnType = isLongByte;}

}
