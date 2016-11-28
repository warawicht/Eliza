package Eliza;

public class Mem
{
  final int memMax = 20;
  String[] memory = new String[20];
  int memTop;
  
  public void save(String str)
  {
    if (this.memTop < 20) {
      this.memory[(this.memTop++)] = new String(str);
    }
  }
  
  public String get()
  {
    if (this.memTop == 0) {
      return null;
    }
    String m = this.memory[0];
    for (int i = 0; i < this.memTop - 1; i++) {
      this.memory[i] = this.memory[(i + 1)];
    }
    this.memTop -= 1;
    return m;
  }
}
