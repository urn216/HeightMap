package code.core;

import mki.math.MathHelp;

import java.awt.image.BufferedImage;

public abstract class ImageProc {

  /**
   * Changes the contrast of an array of {@code float}s between {@code -1f} and {@code 1f}.
   * <p>
   * Changing the contrast in this case represents a scaling of the values within the array away from or towards {@code 0}.
   * Values will always be clamped between {@code -1f} and {@code 1f}.
   * 
   * @param map the array of {@code float}s to scale
   * @param magnitude the contrast increase/decrease desired. 
   * <p> 
   * This value should be between {@code -100f} and {@code 100f}, where {@code -100f} homogenises all values to {@code 0}, 
   * and {@code 100f} sets everything greater than {@code 0} to {@code 1f} and everything less than {@code 0} to {@code -1f}.
   * 
   * @return a new {@code float} array with the desired changes.
   */
  public static float[] mapContrast(float[] map, float magnitude) {
    float[] res = new float[map.length];

    float factor = (104f*(magnitude + 100f)) / (100f*(104f-magnitude));

    for (int i = 0; i < map.length; i++) res[i] = (float)MathHelp.clamp(factor*map[i], -1, 1);

    return res;
  }

  /**
   * Combines two {@code float} arrays into one by averaging each value between the arrays.
   * 
   * @param layer1 the first array to combine.
   * @param layer2 the second array to combine.
   * 
   * @return a new {@code float} array with the averaged values between {@code layer1} and {@code layer2}.
   */
  public static float[] combineMaps(float[] layer1, float[] layer2) {
    if (layer1.length != layer2.length) return layer1;

    float[] res = new float[layer1.length];

    for (int i = 0; i < res.length; i++) {
      res[i] = (float)MathHelp.avg(layer1[i], layer2[i]);
    }

    return res;
  }

  /**
   * Scales a {@code float} array to a new size using nearest-neighbour scaling.
   * 
   * @param map the {@code float} array to scale.
   * @param oW the original width of the given array.
   * @param oH the original height of the given array.
   * @param nW the desired width to give the resulting array.
   * @param nH the desired height to give the resulting array.
   * 
   * @return a new {@code float} array of the desired size containing scaled information from the input array.
   */
  public static float[] scaleMap(float[] map, int oW, int oH, int nW, int nH) {
    float[] res = new float[nW*nH];
    double wScale = 1.0*oW/nW;
    double hScale = 1.0*oH/nH;

    for (int i = 0; i < nW; i++) {
      for (int j = 0; j < nH; j++) {
        res[i + nW * j] = map[(int)(i*wScale) + oW * (int)(j*hScale)];
      }
    }

    return res;
  }

  /**
   * Takes in an array of {@code float}s between {@code -1f} and {@code 1f} and averages each value with its neighbours, 
   * creating a new array with softer transitions between values.
   * 
   * @param map the {@code float} array to smoothe.
   * @param w the width of the array.
   * @param h the height of the array.
   * 
   * @return a new {@code float} array with each value averaged between itself and its eight neighbours.
   */
  public static float[] smootheMap(float[] map, int w, int h) {
    float[] res = new float[map.length];

    for (int i = 0; i < w; i++) {
      final int left  = ((w + i - 1)%w);
      final int right = ((i + 1)%w);

      for (int j = 0; j < h; j++) {
        final int up    = w * ((h + j - 1)%h);
        final int down  = w * ((j + 1)%h);

        final float edges   = (float)MathHelp.avg(map[left + w * j], map[right + w * j], map[i + up],      map[i + down]    );
        final float corners = (float)MathHelp.avg(map[left + up],    map[right + up],    map[left + down], map[right + down]);
        final float ec      = (float)((edges + MathHelp.INVERSE_ROOT_TWO * corners) / (1+MathHelp.INVERSE_ROOT_TWO));

        res[i + w * j] = (float)MathHelp.avg(map[i + w * j], ec); 
      }
    }

    return res;
  }

  /**
   * Converts a {@code float} array with values between {@code -1f} and {@code 1f} into 
   * a {@code BufferedImage} with the brightness of each pixel representing the value of 
   * each {@code float} in the array; {@code -1f} being black; and {@code 1f} being white.
   * 
   * @param map the {@code float} array to convert.
   * @param w the width of the image.
   * @param h the height of the image.
   * 
   * @return a new {@code BufferedImage}.
   */
  public static BufferedImage mapToImage(float[] map, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    int[] rgbArray = new int[map.length];

    for (int i = 0; i < map.length; i++) {
      int height = (int)MathHelp.clamp((map[i] + 1) * 128, 0, 255);
      rgbArray[i] = 
      255 << 24 | 
      (height > 127 ? height > 130 ? height > 240 ? 2*height-255 :  36+height/4 : 52+height : Math.max(0,   height   - 81)) << 16 | //R
      (height > 127 ? height > 130 ? height > 240 ? 2*height-255 : 200-height/3 : 47+height : Math.max(0, 2*height   -140)) << 8  | //G
      (height > 127 ? height > 130 ? height > 240 ? 2*height-255 :  20+height/4 :  4+height :              (height/2)+ 83 );        //B
      //                                               PEAKS          LAND         SAND              OCEAN
    }

    img.setRGB(0, 0, w, h, rgbArray, 0, w);
    return img;
  }
}
