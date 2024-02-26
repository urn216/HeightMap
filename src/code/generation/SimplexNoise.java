package code.generation;

public class SimplexNoise {
  private static final double STRETCH = (1/Math.sqrt(2+1)-1)/2;
  private static final double SQUISH = (Math.sqrt(2+1)-1)/2;
  private static final double NORM = 1.0 / 47.0;
  
  private byte[] perm;
  private byte[] perm2D;
  
  private static final double[] gradients = new double[] {
    5,  2,  2,  5,
    -5,  2, -2,  5,
    5, -2,  2, -5,
    -5, -2, -2, -5,
  };
  
  private static final Contribution[] lookup;
  
  static {
    int[][] base = new int[][] {
      { 1, 1, 0, 1, 0, 1, 0, 0, 0 },
      { 1, 1, 0, 1, 0, 1, 2, 1, 1 }
    };
    int[] p = new int[] { 0, 0, 1, -1, 0, 0, -1, 1, 0, 2, 1, 1, 1, 2, 2, 0, 1, 2, 0, 2, 1, 0, 0, 0 };
    int[] lookupPairs = new int[] { 0, 1, 1, 0, 4, 1, 17, 0, 20, 2, 21, 2, 22, 5, 23, 5, 26, 4, 39, 3, 42, 4, 43, 3 };
    
    Contribution[] contributions = new Contribution[p.length / 4];
    for (int i = 0; i < p.length; i += 4)
    {
      int[] baseSet = base[p[i]];
      Contribution previous = null, current = null;
      for (int k = 0; k < baseSet.length; k += 3)
      {
        current = new Contribution(baseSet[k], baseSet[k + 1], baseSet[k + 2]);
        if (previous == null) {
          contributions[i / 4] = current;
        }
        else {
          previous.Next = current;
        }
        previous = current;
      }
      current.Next = new Contribution(p[i + 1], p[i + 2], p[i + 3]);
    }
    
    lookup = new Contribution[64];
    for (int i = 0; i < lookupPairs.length; i += 2) {
      lookup[lookupPairs[i]] = contributions[lookupPairs[i + 1]];
    }
  }
  
  private static long FastFloor(double x) {
    long xi = (long)x;
    return x < xi ? xi - 1 : xi;
  }
  
  public SimplexNoise() {
    this(System.currentTimeMillis());
  }
  
  public SimplexNoise(long seed) {
    perm = new byte[256];
    perm2D = new byte[256];
    byte[] source = new byte[256];
    for (int i = 0; i < 256; i++)
    {
      source[i] = (byte)i;
    }
    seed = seed * 6364136223846793005L + 1442695040888963407L;
    seed = seed * 6364136223846793005L + 1442695040888963407L;
    seed = seed * 6364136223846793005L + 1442695040888963407L;
    for (int i = 255; i >= 0; i--)
    {
      seed = seed * 6364136223846793005L + 1442695040888963407L;
      int r = (int)((seed + 31) % (i + 1));
      if (r < 0)
      {
        r += (i + 1);
      }
      perm[i] = source[r];
      perm2D[i] = (byte)(perm[i] & 0x0E);
      source[r] = source[i];
    }
  }
  
  public double evaluate(double x, double y) {
    double stretchOffset = (x + y) * STRETCH;
    double xs = x + stretchOffset;
    double ys = y + stretchOffset;
    
    long xsb = FastFloor(xs);
    long ysb = FastFloor(ys);
    
    double squishOffset = (xsb + ysb) * SQUISH;
    double dx0 = x - (xsb + squishOffset);
    double dy0 = y - (ysb + squishOffset);
    
    double xins = xs - xsb;
    double yins = ys - ysb;
    
    double inSum = xins + yins;
    
    int hash = (int)(
      (long)(xins - yins + 1) |
      (long)(inSum) << 1 |
      (long)(inSum + yins) << 2 |
      (long)(inSum + xins) << 4
    );
    
    Contribution c = lookup[hash];
    
    double value = 0.0;
    while (c != null)
    {
      double dx = dx0 + c.dx;
      double dy = dy0 + c.dy;
      double attn = 2 - dx * dx - dy * dy;
      if (attn > 0)
      {
        int px = (int)xsb + c.xsb;
        int py = (int)ysb + c.ysb;
        
        byte i = perm2D[(int)(perm[(int)(px & 0xFF)] + py) & 0xFF];
        double valuePart = gradients[i] * dx + gradients[i + 1] * dy;
        
        attn *= attn;
        value += attn * attn * valuePart;
      }
      c = c.Next;
    }
    return value * NORM;
  }
  
  private static class Contribution {
    public double dx, dy;
    public int xsb, ysb;
    public Contribution Next;
    
    public Contribution(double multiplier, int xsb, int ysb)
    {
      dx = -xsb - multiplier * SQUISH;
      dy = -ysb - multiplier * SQUISH;
      this.xsb = xsb;
      this.ysb = ysb;
    }
  }
}