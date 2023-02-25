package code.core;

import code.math.MathHelp;

import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class ImageProc {

  public static int[] scaleMap(int[] map, int oW, int oH, int nW, int nH) {
    int[] res = new int[nW*nH];
    double wScale = 1.0*oW/nW;
    double hScale = 1.0*oH/nH;

    for (int i = 0; i < nW; i++) {
      for (int j = 0; j < nH; j++) {
        res[i + nW * j] = map[(int)(i*wScale) + oW * (int)(j*hScale)];
      }
    }

    return res;
  }

  public static int[] smoothMap(int[] map, int w, int h) {
    int[] res = new int[map.length];

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {

        int left  = ((w + i - 1)%w);
        int right = ((i + 1)%w);
        int up    = w * ((h + j - 1)%h);
        int down  = w * ((j + 1)%h);

        int edges   = (int)MathHelp.avg(map[left + w * j] & 255, map[right + w * j] & 255, map[i + up] & 255, map[i + down] & 255);
        int corners = (int)MathHelp.avg(map[left + up] & 255, map[right + up] & 255, map[left + down] & 255, map[right + down] & 255);
        int ec      = (int)((edges + MathHelp.INVERSE_ROOT_TWO * corners) / (1+MathHelp.INVERSE_ROOT_TWO));
        int height  = (int)MathHelp.avg(map[i + w * j] & 255, ec);

        res[i + w * j] = 255 << 24 | height << 16 | height << 8 | height;
      }
    }

    return res;
  }

  public static Image mapToImage(int[] map, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    img.setRGB(0, 0, w, h, map, 0, w);
    return img;
  }
}
