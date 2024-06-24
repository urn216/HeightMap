package code.generation;

// import java.util.Random;

import mki.io.FileIO;
// import code.math.MathHelp;

public class TerrainGenerator {

  // private static final double BEACH_FLATTEN_EXP = 1.5;

  // private static final double BEACH_CUTOFF = Math.pow(1/BEACH_FLATTEN_EXP, 1/(BEACH_FLATTEN_EXP-1));
  // private static final float  LAND_OFFSET = (float)(BEACH_CUTOFF-Math.pow(BEACH_CUTOFF, BEACH_FLATTEN_EXP));
  
  private final SimplexNoise noise;
  
  public TerrainGenerator(long seed) {
    this.noise = new SimplexNoise(seed);
  }

  public TerrainGenerator() {
    this(System.currentTimeMillis());
  }
  
  public float[] generateHeights(double xOff, double yOff, int width, int height, int octaves, double worldScale, boolean bigPrint) {
    float[] res = new float[width*height];

    xOff = xOff*worldScale-width /2.0;
    yOff = yOff*worldScale-height/2.0;
    
    for (int o = 0; o < octaves; o++) {
      double xOctOff = o*1000;
      double yOctOff = o*1000;
      double scaleH = Math.pow(2, o);
      double scaleV = Math.pow(2, -o*0.8);
      double damper = 500*worldScale;//(o<octaves/2?worldScale:100);

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          res[x+y*width] += noise.evaluate(
            xOctOff+((x+xOff)*scaleH/damper), 
            yOctOff+((y+yOff)*scaleH/damper)
          )*scaleV
          *(o>9?Math.min(1, Math.abs(res[x+y*width]/0.0078125)):1);
        }
      }

      if (bigPrint) FileIO.writeImage("../results/layer" + o + "_octave.png", ImageProc.mapToImage(res, width, height));
    }

    // for (int x = 0; x < width; x++) {
    //   for (int y = 0; y < height; y++) {
    //     //Continent test
    //     // res[x+y*width] += 3/(1+Math.pow(Math.E, 50*(0.5-noise.evaluate((x+xOff)/(10000*Core.MAP_SCALE), (y+yOff)/(10000*Core.MAP_SCALE)))))-1.5;
    //     float h = res[x+y*width];
    //     res[x+y*width] = h > 0?
    //       h >  BEACH_CUTOFF ? h-LAND_OFFSET :  (float)Math.pow( h, BEACH_FLATTEN_EXP):
    //       h < -BEACH_CUTOFF ? h+LAND_OFFSET : -(float)Math.pow(-h, BEACH_FLATTEN_EXP);
    //   }
    // }

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
