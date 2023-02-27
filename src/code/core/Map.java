package code.core;

import java.util.Random;

import code.math.IOHelp;
import code.math.MathHelp;

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

    map.addLayer(0);
    map.addLayer(10);
    map.addLayer(100);
    map.addLayer(500);

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
    IOHelp.writeImage("../results/layer" + layers + "_noise.png", ImageProc.mapToImage(layer, width, height));

    for (int i = 0; i < smoothness; i++) layer = ImageProc.smootheMap(layer, width, height);

    IOHelp.writeImage("../results/layer" + layers + "_avged.png", ImageProc.mapToImage(layer, width, height));
    layer = ImageProc.mapContrast(layer, (float)MathHelp.clamp(smoothness*smoothness, 0, 200)-100);
    IOHelp.writeImage("../results/layer" + layers + ".png", ImageProc.mapToImage(layer, width, height));

    intMap = intMap == null ? layer : ImageProc.combineMaps(intMap, layer);
  }
}
