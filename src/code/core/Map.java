package code.core;

import java.util.Random;

import mki.io.FileIO;
// import code.math.MathHelp;

public class Map {
  
  private final int width;
  private final int height;
  private final Random rng;
  private final SimplexNoise noise;
  
  private float[] heightMap;
  
  private int layers = 0;
  
  private Map(int width, int height, long seed) {
    this.width = width;
    this.height = height;
    this.rng = new Random(seed);
    this.noise = new SimplexNoise(seed);
  }
  
  public static Map generateMap(int w, int h, int octaves) {
    Map map = new Map(w, h, System.currentTimeMillis());
    
    map.generateGrid(0, 0, octaves, true);
    
    return map;
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public float[] getHeightMap() {
    return heightMap;
  }
  
  public void generateGrid(double xOff, double yOff, int octaves, boolean bigPrint) {
    this.heightMap = new float[this.width*this.height];
    
    for (int o = 0; o < octaves; o++) {
      double xOctOff = o*1000;
      double yOctOff = o*1000;
      double scale = Math.pow(2, o);
      double damper = 50*Core.MAP_SCALE;

      for (int x = 0; x < this.width; x++) {
        for (int y = 0; y < this.height; y++) {
          this.heightMap[x+y*this.width] += noise.evaluate(
            xOctOff+((x+xOff)*scale/damper), 
            yOctOff+((y+yOff)*scale/damper)
          )/scale/(5/Core.MAP_SCALE);
        }
      }

      if (bigPrint) FileIO.writeImage("../results/layer" + o + "_octave.png", ImageProc.mapToImage(heightMap, width, height));
    }
  }
  
  public void addPrimitiveLayer(int smoothness) {
    layers++;
    
    float[] layer = Noise.seededNoise(rng, width * height);
    
    if (smoothness >= 2000) {
      int scale = smoothness/1000;
      layer = ImageProc.scaleMap(Noise.sampleNoise(rng, layer, width/scale*height/scale), width/scale, height/scale, width, height);
      smoothness/=10;
    }
    
    FileIO.writeImage("../results/layer" + layers + "_noise.png", ImageProc.mapToImage(layer, width, height));
    
    for (int i = 0; i < smoothness; i++) {
      layer = ImageProc.smootheMap(layer, width, height);
      if (i%10 == 0) layer = ImageProc.mapContrast(layer, 0.5f);
    }
    
    
    float x = Math.min(smoothness, 100)/200f;
    
    FileIO.writeImage("../results/layer" + layers + "_avged.png", ImageProc.mapToImage(layer, width, height));
    layer = ImageProc.mapContrast(layer, (600*x*x-400*x*x*x)-75);
    FileIO.writeImage("../results/layer" + layers + ".png", ImageProc.mapToImage(layer, width, height));
    
    heightMap = heightMap == null ? layer : ImageProc.combineMaps(heightMap, layer);
  }
}
