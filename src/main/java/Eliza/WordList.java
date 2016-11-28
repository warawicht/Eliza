package Eliza;

import java.io.PrintStream;
import java.util.Vector;

public class WordList
  extends Vector
{
  public void add(String word)
  {
    addElement(word);
  }
  
  public void print(int indent)
  {
    for (int i = 0; i < size(); i++)
    {
      String s = (String)elementAt(i);
      System.out.print(s + "  ");
    }
    System.out.println();
  }
  
  boolean find(String s)
  {
    for (int i = 0; i < size(); i++) {
      if (s.equals((String)elementAt(i))) {
        return true;
      }
    }
    return false;
  }
}
