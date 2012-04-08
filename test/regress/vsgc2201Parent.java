/*
 * @subtest
 */
public class vsgc2201Parent {
  public void firePropertyChange(String a, byte b, byte c) {
    System.out.println("called(firePropertyChange,String,byte,byte)"); }
  public void firePropertyChange(String a, char b, char c) {
    System.out.println("called(firePropertyChange,String,char,char)"); }
  public void firePropertyChange(String a, int b, int c) {
    System.out.println("called(firePropertyChange,String,int,int)"); }
}
