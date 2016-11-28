package Eliza;

public class ElizaApp
{
  static String scriptPathname = "script";
  static String testPathname = "test";
  static final boolean useWindow = false;
  static final boolean local = true;
  
  public static void main(String[] args)
  {
    ElizaMain eliza = new ElizaMain();
    String script = scriptPathname;
    String test = testPathname;
    if (args.length > 0) {
      script = args[0];
    }
    if (args.length > 1) {
      test = args[1];
    }
    int res = eliza.readScript(true, script);
    if (res != 0) {
      System.exit(res);
    }
    res = eliza.runProgram(test, null);
    System.exit(res);
  }
}
