package code.core;

import java.util.Random;

public abstract class Noise {
  public static int[] randomNoise(int w, int h) {
    return seededNoise(new Random(), w, h);
  }

  public static int[] seededNoise(Random random, int w, int h) {
    int[] map = new int[w*h];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int height = random.nextInt(256);
        map[i + w * j] = 255 << 24 | height << 16 | height << 8 | height;
      }
    }

    return map;
  }

  public static int[] sampleNoise(Random random, int[] original, int oW, int oH, int sW, int sH) {
    if (sW > oW || sH > oH) return original;

    int[] res = new int[sW*sH];
    int xOff = random.nextInt(oW-sW);
    int yOff = random.nextInt(oH-sH);

    for (int i = 0; i < sW; i++) {
      for (int j = 0; j < sH; j++) {
        res[i + sW * j] = original[(i + xOff) + oW * (j + yOff)];
      }
    }

    return res;
  }
}
