import java.util.*;

/** A Java version of SeqRandomMods, using ArrayList.
 * The output from SeqRandomMods should match the output of this application.
 */

public class SeqRandomModsJ
{
  public static ArrayList<Integer> arr = new ArrayList<Integer>();
  static int modulo (int i, int j) {
    int m = i % j;
    return m < 0 ?  m + j : m;
  }
  static int modulo (int i) {
    return modulo(i, arr.size());
  }

  static int checksum() {
    int sum = 0;
    int sz = arr.size();
    for (int i = 0;  i < sz;  i++)
      sum += i*arr.get(i);
    return sum;
  }

  static final int size = 1000;
  static final int iterations = 10*size;
  static final boolean DEBUGGING = false;

  public static void main (String[] argv) {
    for (int i = 0;  i < size;  i++)
      arr.add(i);

    for (int i = 0;  i < iterations; i++) {
      int j = i * 99907;
      int k = j * 79943;
      int op = modulo(j, 7);
      int val = 987 * arr.get(modulo(k));
      if (DEBUGGING)
        System.out.println("i: "+i+" arr len="+arr.size()
                           +" checksum:"+checksum()+" val:"+val
                           +" j:"+j+"->"+modulo(j)+" op:"+op);
      j = modulo(j);
      if (op == 0) // replace single
        arr.set(j, val);
      else if (op == 1) // insert single before
        arr.add(j, val);
      else if (op == 2) // insert single after
        arr.add(j, val);
      else if (op == 3) {
        k = modulo(k);
        for (; k > j; k--)
          arr.remove(j);
      }
      else if (op == 4) { // delete single
        if (j < arr.size())
          arr.remove(j);
      }
      else if (op == 5 || op == 6) { // multiple insert or replace
        int n = modulo(k, 333);
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        for (int i2 = 0;  i2 < n;  i2++)
          tmp.add(i2*k);
        if (op == 6) {
          k = modulo(k);
          for (; k > j; k--)
            arr.remove(j);
        }
        arr.addAll(j, tmp);
      }
    }
    int sum = 0;
    int sz = arr.size();
    for (int i = 0; i < sz;  i++)
      sum += i*arr.get(i);
    System.out.println("Final array length "+sz+" check-sum: "+sum+'.');
  }
}
