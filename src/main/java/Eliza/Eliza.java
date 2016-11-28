package Eliza;

import java.applet.Applet;
import java.awt.Event;

public class Eliza
  extends Applet
{
  static String scriptPathname = "c:\\cch\\eliza\\script";
  static String testPathname = "c:\\cch\\eliza\\test";
  static String scriptURL = "http://www.monmouth.com/~chayden/eliza/script";
  static String testURL = "http://www.monmouth.com/~chayden/eliza/test";
  boolean useWindow = true;
  boolean local = false;
  ElizaMain eliza;
  
  public void init()
  {
    showStatus("Loading Eliza");
    this.eliza = new ElizaMain();
  }
  
  public void start()
  {
    String script = getScriptParam();
    String test = getTestParam();
    if (this.local)
    {
      script = scriptPathname;
      test = testPathname;
    }
    showStatus("Loading script from " + script);
    this.eliza.readScript(this.local, script);
    showStatus("Ready");
    if (this.useWindow) {
      this.eliza.runProgram(test, this);
    } else {
      this.eliza.runProgram(test, null);
    }
  }
  
  public boolean handleEvent(Event e)
  {
    return this.eliza.handleEvent(e);
  }
  
  String getScriptParam()
  {
    String script = getParameter("script");
    if (script == null) {
      script = scriptURL;
    }
    return script;
  }
  
  String getTestParam()
  {
    String test = getParameter("test");
    if (test == null) {
      test = testURL;
    }
    return test;
  }
  
  public String[][] getParameterInfo()
  {
    String[][] info = {
      { "script", "URL", "URL of script file" }, 
      { "test", "URL", "URL of test file" } };
    
    return info;
  }
  
  public String getAppletInfo()
  {
    return "Eliza v0.1 written by Charles Hayden chayden@monmouth.com";
  }
}
