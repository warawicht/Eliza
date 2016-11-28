package Eliza;

import java.util.Vector;

public class PrePostList
  extends Vector
{
  public void add(String src, String dest)
  {
    addElement(new PrePost(src, dest));
  }
  
  public void print(int indent)
  {
    for (int i = 0; i < size(); i++)
    {
      PrePost p = (PrePost)elementAt(i);
      p.print(indent);
    }
  }
  
  String xlate(String str)
  {
    for (int i = 0; i < size(); i++)
    {
      PrePost p = (PrePost)elementAt(i);
      if (str.equals(p.src())) {
        return p.dest();
      }
    }
    return str;
  }
  
  public String translate(String s)
  {
    String[] lines = new String[2];
    String work = EString.trim(s);
    s = "";
    while (EString.match(work, "* *", lines))
    {
      s = s + xlate(lines[0]) + " ";
      work = EString.trim(lines[1]);
    }
    s = s + xlate(work);
    return s;
  }
}
