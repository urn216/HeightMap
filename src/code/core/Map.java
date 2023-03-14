package code.core;

import java.util.Random;

import code.math.IOHelp;
// import code.math.MathHelp;

public class Map {

  private final int width;
  private final int height;
  private final Random rng;

  private float[] intMap;

  private int layers = 0;

  private Map(int width, int height, Random rng) {
    this.width = width;
    this.height = height;
    this.rng = rng;
  }

  public static Map generateMap(int w, int h) {
    Map map = new Map(w, h, new Random());

    // map.addLayer(0);
    map.addLayer(10);
    map.addLayer(100);
    map.addLayer(1000);
    map.addLayer(10000);

    return map;
  }
  
  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public float[] getIntMap() {
    return intMap;
  }

  public void addLayer(int smoothness) {
    layers++;

    float[] layer = Noise.seededNoise(rng, width * height);

    if (smoothness >= 2000) {
      int scale = smoothness/1000;
      layer = ImageProc.scaleMap(Noise.sampleNoise(rng, layer, width/scale*height/scale), width/scale, height/scale, width, height);
      smoothness/=10;
    }

    IOHelp.writeImage("../results/layer" + layers + "_noise.png", ImageProc.mapToImage(layer, width, height));

    for (int i = 0; i < smoothness; i++) {
      layer = ImageProc.smootheMap(layer, width, height);
      if (i%10 == 0) layer = ImageProc.mapContrast(layer, 0.5f);
    }


    float x = Math.min(smoothness, 100)/200f;

    IOHelp.writeImage("../results/layer" + layers + "_avged.png", ImageProc.mapToImage(layer, width, height));
    layer = ImageProc.mapContrast(layer, (600*x*x-400*x*x*x)-75);
    IOHelp.writeImage("../results/layer" + layers + ".png", ImageProc.mapToImage(layer, width, height));

    intMap = intMap == null ? layer : ImageProc.combineMaps(intMap, layer);
  }
}
