package Eliza;

import java.io.PrintStream;
import java.util.Vector;

public class SynList
  extends Vector
{
  public void add(WordList words)
  {
    addElement(words);
  }
  
  public void print(int indent)
  {
    for (int i = 0; i < size(); i++)
    {
      for (int j = 0; j < indent; j++) {
        System.out.print(" ");
      }
      System.out.print("synon: ");
      WordList w = (WordList)elementAt(i);
      w.print(indent);
    }
  }
  
  public WordList find(String s)
  {
    for (int i = 0; i < size(); i++)
    {
      WordList w = (WordList)elementAt(i);
      if (w.find(s)) {
        return w;
      }
    }
    return null;
  }
  
  boolean matchDecomp(String str, String pat, String[] lines)
  {
    if (!EString.match(pat, "*@* *", lines)) {
      return EString.match(str, pat, lines);
    }
    String first = lines[0];
    String synWord = lines[1];
    String theRest = " " + lines[2];
    
    WordList syn = find(synWord);
    if (syn == null)
    {
      System.out.println("Could not fnd syn list for " + synWord);
      return false;
    }
    for (int i = 0; i < syn.size(); i++)
    {
      pat = first + (String)syn.elementAt(i) + theRest;
      if (EString.match(str, pat, lines))
      {
        int n = EString.count(first, '*');
        for (int j = lines.length - 2; j >= n; j--) {
          lines[(j + 1)] = lines[j];
        }
        lines[n] = ((String)syn.elementAt(i));
        return true;
      }
    }
    return false;
  }
}
