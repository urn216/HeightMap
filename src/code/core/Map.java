package code.core;

import java.awt.image.BufferedImage;

import java.util.Random;

import code.math.IOHelp;

public class Map {

  private final int w;
  private final int h;
  private final int[] noise;
  private final int[] map;

  private Map(int w, int h, int[] noise, int[] map) {
    this.w = w;
    this.h = h;
    this.noise = noise;
    this.map = map;
  }

  public static Map generateMap(int w, int h) {
    Random random = new Random();
    int[] noise = Noise.seededNoise(random, w, h);
    IOHelp.writeImage("../results/noise.png", (BufferedImage)ImageProc.mapToImage(noise, w, h));
    
    int[] map = ImageProc.scaleMap(noise, w, h, w, h);
    for (int i = 0; i < 25; i++) map = ImageProc.smoothMap(map, w, h);

    return new Map(w, h, noise, map);
  }
  
  public int getW() {
    return w;
  }

  public int getH() {
    return h;
  }

  public int[] getNoise() {
    return noise;
  }

  public int[] getMap() {
    return map;
  }
}
