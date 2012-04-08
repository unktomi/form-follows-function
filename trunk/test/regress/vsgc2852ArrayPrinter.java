/**
 * VSGC-2852 - String(byte[]...) doesn't compile.
 *
 * @subtest
 */

// contains static methods that accept Java primitive
// array type arguments. These are called from F3
// to check that we can pass primitive sequences to these.

public class vsgc2852ArrayPrinter {

    public static void printByteArray(byte[] arr) {
        System.out.print("byte array : ");
        for (byte b : arr) {
            System.out.print(b + ", ");
        }
        System.out.println();
    }

    public static void printShortArray(short[] arr) {
        System.out.print("short array : ");
        for (short s : arr) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }

    public static void printIntArray(int[] arr) {
        System.out.print("int array : ");
        for (int i : arr) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }

    public static void printLongArray(long[] arr) {
        System.out.print("long array : ");
        for (long l : arr) {
            System.out.print(l + ", ");
        }
        System.out.println();
    }

    public static void printFloatArray(float[] arr) {
        System.out.print("float array : ");
        for (float f : arr) {
            System.out.print(f + ", ");
        }
        System.out.println();
    }

    public static void printDoubleArray(double[] arr) {
        System.out.print("double array : ");
        for (double d : arr) {
            System.out.print(d + ", ");
        }
        System.out.println();
    }
}
