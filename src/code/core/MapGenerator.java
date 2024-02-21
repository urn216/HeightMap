package code.core;

// import java.util.Random;

import mki.io.FileIO;
// import code.math.MathHelp;

public class MapGenerator {

  private static final double BEACH_FLATTEN_EXP = 1.5;

  private static final double BEACH_CUTOFF = Math.pow(1/BEACH_FLATTEN_EXP, 1/(BEACH_FLATTEN_EXP-1));
  private static final float  LAND_OFFSET = (float)(BEACH_CUTOFF-Math.pow(BEACH_CUTOFF, BEACH_FLATTEN_EXP));
  
  // private static Random rng;
  private static SimplexNoise noise;
  
  public static void initialise(long seed) {
    // MapGenerator.rng = new Random(seed);
    MapGenerator.noise = new SimplexNoise(seed);
  }

  public static void initialise() {
    initialise(System.currentTimeMillis());
  }
  
  public static float[] generateHeights(double xOff, double yOff, int width, int height, int octaves, boolean bigPrint) {
    float[] res = new float[width*height];

    xOff = xOff*Core.MAP_SCALE-width /2.0;
    yOff = yOff*Core.MAP_SCALE-height/2.0;
    
    for (int o = 0; o < octaves; o++) {
      double xOctOff = o*1000;
      double yOctOff = o*1000;
      double scale = Math.pow(2, o);
      double damper = 500*Core.MAP_SCALE;

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          res[x+y*width] += noise.evaluate(
            xOctOff+((x+xOff)*scale/damper), 
            yOctOff+((y+yOff)*scale/damper)
          )/scale;
        }
      }

      if (bigPrint) FileIO.writeImage("../results/layer" + o + "_octave.png", ImageProc.mapToImage(res, width, height));
    }

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        float h = res[x+y*width];
        res[x+y*width] = h > 0?
          h >  BEACH_CUTOFF ? h-LAND_OFFSET :  (float)Math.pow( h, BEACH_FLATTEN_EXP):
          h < -BEACH_CUTOFF ? h+LAND_OFFSET : -(float)Math.pow(-h, BEACH_FLATTEN_EXP);
      }
    }

    return res;
  }
  
  /*
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
  */
}
