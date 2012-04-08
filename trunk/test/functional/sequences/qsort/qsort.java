/**
 *  For comparison, compile with javac and run...takes second to sort 3000+ items
 *  With f3 it takes a minute or more to sort 1000.
 */
public class qsort<T extends Comparable>
{
    public static boolean bPrint = true;
    public int array_size = 0;
    T[] Obj_array;
    public static int compare_count = 0;
    public static int swap_count = 0;
    public static void PrintOff(boolean b) { bPrint = !b; }

    /**
     * Default constructor
     */
    protected qsort(){}

    /*
     * Create qsort object from array type T
     */
    public qsort(T[] newarray)
    {
     array_size = newarray.length;
     Obj_array = (T[])java.lang.reflect.Array.newInstance(
            newarray.getClass().getComponentType(), array_size);
   	 System.arraycopy(newarray, 0, Obj_array, 0, array_size);
    }

    /* Creat qsort object with an array of Integers from an int array.
     * Cannot instantiante generic class on primitive type.  Therefore
     * to accept arrays of primitive types, a constructor for each primitive type
     * would still have to be written as below
     */
    public qsort(int[] intarray)
    {
      Integer[] newarray = new Integer[intarray.length];
//      for(int i = 0; i<newarray.length; i++)
//         newarray[i] = new Integer(intarray[i]);
      int i = 0;
      for(int e : intarray)
         newarray[i++] = new Integer(e);
      array_size = newarray.length;
      Obj_array = (T[])java.lang.reflect.Array.newInstance(
            newarray.getClass().getComponentType(), array_size);

      System.arraycopy(newarray, 0, Obj_array, 0, array_size);
    }

    /**
     * Return length of internal array
     */
    public int length(){ return array_size; }

    /**
     * Print routine which is controlled by boolean bPrint
     */
    public static void println( String msg )
    {
        if (bPrint) System.out.println(msg);
    }
    public static void print( String msg )
    {
        if (bPrint) System.out.print(msg);
    }


    /**
     *  Print array preceded by message msg
     */
    public void printArray(String msg)
    {
        if (!bPrint) return;
        System.out.print(msg);
        try{
//        for (int i = 0; i<Obj_array.length; i++)
//          System.out.print(" " + Obj_array[i].toString());
        for (Object o : Obj_array ) //enhanced for loop
          System.out.print(" " + o );
        System.out.println();
        }
        catch(Exception e)
        {
            System.out.println("Error: Failed to print internal array, Obj_array.");
            e.printStackTrace();
        }
    }
    /**
     *  Print given array
     */
    public void printArray(T[] oarray)
    {
        if (!bPrint) return;
        try{
//        for (int i = 0; i<oarray.length; i++)
//          System.out.print(" " + oarray[i].toString());
// replace "old" for loop with enhanced for loop syntax
        for ( T e : oarray)
          System.out.print("-" + e.toString());
        System.out.println();
        }
        catch(Exception e)
        {
            System.out.println("Error: Failed to print array " + oarray.toString());
            e.printStackTrace();
        }
    }
    /**
     *  Return array of type T[]
     */
    public T[] getSortedArray()
    {
        return Obj_array;
    }

    /**
     *  Swap object in Obj_array with indexes i and j
     */
    void swap(int i, int j)
    {	  ++swap_count;
        T temp = Obj_array[i];
        Obj_array[i] = Obj_array[j];
        Obj_array[j] = temp;
    }
    /**
     *  Compares two Objects. Since qsort must be of type 'T extends Comparable',
     *  do not have to check the type.
     */
    int Compare(T o1, T o2)
    {
        String O1x = o1.toString();
        String O2x = o2.toString();
        ++compare_count;
	 return (o1.compareTo(o2));
    }

    /**
     * Find pivot point in Obj_array
     */
    int findPivot(int i, int j)
    {
       try{
        T firstkey = Obj_array[i];
        for(int k = i+1; k<=j; k++)
          if( Compare(Obj_array[k],firstkey) > 0)
             return k;
          else if( Compare( Obj_array[k],firstkey) < 0 )
           return i;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    /**
     *  Partition array around pivot point 'pivot'
     */
    int partition(int i, int j, T pivot)
    {
		  int l = i;
        int r = j;
        try{
          while(l<=r)
          {
              swap(l,r);
              while( Compare(Obj_array[l],pivot)<0 )
                  l = l+1;
              while( Compare(Obj_array[r],pivot)>-1 )
                r = r-1;
          }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * Recursive quick sort algorithm
     *   While pivot index is >=0(ie., j>i)
     *   1. Find pivot index.
     *   2. Partition array around index
     *   3. Sort each partition
     */
    public void sort(int i, int j)
    {
        T pivot;
        int pivotindex;
        int k;
        pivotindex = findPivot(i,j);
        try{
          if(pivotindex >=0)
          {
              pivot = Obj_array[pivotindex];
              k = partition(i,j,pivot);
              sort(i,k-1);
              sort(k,j);
          }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Call quick sort algorithm from i to j.
     * This sorts the internal array, leaving it in a sorted state,
     * and returns the sorted array.
     */
    public T[] sort()
    {
        int i = 0;
        int j = Obj_array.length-1;
        sort(i,j);
        return getSortedArray();
    }

//-----------------------------------------------------------------
    public static void main(String[] args)
    {
      qsort.PrintOff(true);
      if (args.length>0)
        if(args[0].compareTo("print")==0)
          qsort.PrintOff(false);
      //create an array
      int arraysize = 20;
      int multiplier = 100;
      int modnum = 151;
      int totalSortedItems = 0;
      int totalCompares = 0;
      int totalSwaps = 0;
      System.out.print("START...");

qsort.println("----Integer array-----------------");
      Integer Iarray[] = new Integer[arraysize];
      for(int j = 0; j<arraysize; j++)
         Iarray[j] = new Integer((j*multiplier + 1) %modnum);
      qsort<Integer> qI = new qsort<Integer>(Iarray);
      qI.printArray("Unsorted");
      qI.sort(0,qI.length()-1);
      totalSortedItems += qI.length();
      totalCompares += qI.compare_count;
      totalSwaps += qI.swap_count;
      qI.printArray("Sorted");
//*
qsort.println("----Double array-----------------");
      Double Darray[] = new Double[arraysize];
      for(int j = 0; j<arraysize; j++)
         Darray[j] = new Double( ((j*multiplier + 1) %modnum)*1.235 );
      qsort<Double> qD = new qsort<Double>(Darray);
      String type = qD.Obj_array[0].getClass().getName();
qsort.println("type of Object array[1]: " + type);
      qD.printArray("Unsorted:");
      qD.sort(0,qD.length()-1);
      totalSortedItems += qD.length();
      totalCompares += qD.compare_count;
      totalSwaps += qD.swap_count;
      qD.printArray("Sorted:");

      //see note for constructor for primitive type arrays
qsort.println("----int array-----------------");
      int iarray[]  = new int[arraysize];
      for(int j = 0; j<arraysize; j++)
         iarray[j] = (j*multiplier + 1) %modnum;
      qsort<Integer> q = new qsort<Integer>(iarray);
      q.printArray("Unsorted");
      q.sort(0,q.length()-1);
      totalSortedItems += q.length();
      totalCompares    += q.compare_count;
      totalSwaps       += q.swap_count;
      //force use of enhanced for loop in printArray(T[])
      q.printArray ( q.getSortedArray() );

qsort.println("----String array-----------------");
      String Sarray[] ={"cat", "dog","aligator","Zebra","Monkey","elephant","snake","lizard"};
      qsort<String> qS = new qsort<String>(Sarray);
      qS.printArray("Unsorted:");
      qS.sort(0,qS.length()-1);
      System.out.println("DONE!");
      totalSortedItems += qS.length();
      totalCompares    += qS.compare_count;
      totalSwaps       += qS.swap_count;
		qS.printArray("Sorted:");
//*/

		System.out.println("Items sorted: " + totalSortedItems  + "  Swaps: " + totalSwaps + "  compares: " + totalCompares) ;
      System.out.println("PASS QSORT");
    }
//*/
}
