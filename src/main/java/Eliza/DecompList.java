package Eliza;

import java.util.Vector;

public class DecompList
  extends Vector
{
  public void add(String word, boolean mem, ReasembList reasmb)
  {
    addElement(new Decomp(word, mem, reasmb));
  }
  
  public void print(int indent)
  {
    for (int i = 0; i < size(); i++)
    {
      Decomp d = (Decomp)elementAt(i);
      d.print(indent);
    }
  }
}
