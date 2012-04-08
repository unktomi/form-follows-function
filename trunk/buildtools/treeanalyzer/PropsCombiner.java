import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * This class combines properties from multiple files into single 
 * Properties object. User specifies a directory and a file name pattern.
 * All files (recursively found) in the specified directory with the 
 * matched name are loaded as Properties object and are consolidated 
 * into a single Properties object. The summary Property values are
 * printed on System.out.
 *
 * To run this class, use the following command:
 *
 *    java PropsCombiner <directory> <file-name-pattern>
 *
 * See also README.txt in this directory.
 *
 * @author A. Sundararajan
 */
public class PropsCombiner {

   public static void main(String[] args) throws Exception {
       if (args.length != 2) {
           System.err.println("java " + PropsCombiner.class + " <dir> <pattern>");
           System.exit(1);
       }
       File curDir = new File(args[0]);
       Pattern pat = Pattern.compile(args[1]);
       Properties prop = summarize(curDir, pat);
       List names = Collections.list(prop.propertyNames());
       Collections.sort(names);
       for (Object o : names) {
           String name = o.toString();
           System.out.print(name);
           System.out.print(" = ");
           System.out.println(prop.getProperty(name));
       }
   }

   private static Properties summarize(File dir, final Pattern pat) throws IOException {
       assert dir.isDirectory() : dir + " is not a directory";
       Properties result = new Properties();
       File[] files = dir.listFiles();
       for (File f : files) {
           Properties props = null;
           if (f.isFile() && pat.matcher(f.getName()).matches()) {
               props = load(f);
           }
           if (f.isDirectory()) {
               props = summarize(f, pat);
           }

           if (props != null) {
               for (Object prop : props.keySet()) {
                   String name = prop.toString();
                   int prevValue = 0;
                   String tmp = result.getProperty(name);
                   try {
                       if (tmp != null) {
                           prevValue = Integer.parseInt(tmp);
                       }
                       int curValue = Integer.parseInt(props.getProperty(name));
                       result.setProperty(name, Integer.valueOf(prevValue + curValue).toString());
                   } catch (NumberFormatException nfe) {
                       // ignore, may be corrupted properties entry in some file
                   }
               }
           }
       }
       return result;
   }

   private static Properties load(File f) throws IOException {
       Properties props = new Properties();
       InputStream is = new BufferedInputStream(new FileInputStream(f));
       try {
           props.load(is);
       } finally {
           is.close();
       }
       return props;
   }
}
