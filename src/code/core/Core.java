package code.core;

import java.awt.image.BufferedImage;

import mki.io.FileIO;
import code.generation.Chunk;
import code.generation.ImageProc;
import code.generation.MapGenerator;
import code.models.Map3D;
import code.models.MapCubes;

import java.awt.Graphics;

public abstract class Core {

  public static final Window WINDOW = new Window();

  public static double MAP_SCALE = 1;
  public static final int MAP_OCTAVES  = 10;
  // public static double MAP_SCALE = 1/Math.pow(2, 55);
  
  public static final int CHUNK_SIZE = 64;

  private static final int MAP_WIDTH    = 128;
  private static final int MAP_HEIGHT   = 128;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static volatile BufferedImage img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);

  private static volatile double lX = 0, lZ = 0;
  private static volatile int    gX = 0, gZ = 0;

  private static volatile float[] heightMap;

  private static final Chunk[][] CHUNKS = new Chunk[5][5];

  static {
    WINDOW.setFullscreen(false);

    FileIO.createDir("../results/");

    MapGenerator.initialise();

    for (int y = 0; y < CHUNKS.length; y++) {
      for (int x = 0; x < CHUNKS[y].length; x++) {
        // FileIO.saveToFile("../results/chunk_"+j+"_"+i+".obj", new Chunk(j, i).toString());
        CHUNKS[y][x] = new Chunk(gX-CHUNKS[y].length/2+x, gZ-CHUNKS.length/2+y);
      }
    }
  }

  public static void main(String[] args) {
    Core.heightMap = MapGenerator.generateHeights(lX, lZ, MAP_WIDTH, MAP_HEIGHT, MAP_OCTAVES, true);
    Core.img = ImageProc.mapToImage(Core.heightMap, MAP_WIDTH, MAP_HEIGHT);

    Core.printScreenToFiles();

    WINDOW.PANEL.repaint();
  }

  public static void printScreenToFiles() {
    FileIO.writeImage("../results/terrain_map.png", img);
    FileIO.saveToFile("../results/map.obj",         new Map3D(heightMap, MAP_WIDTH, MAP_HEIGHT).toString());
    FileIO.saveToFile("../results/mapBlock.obj",    new MapCubes(heightMap, MAP_WIDTH, MAP_HEIGHT).toString());
    FileIO.saveToFile("../results/mat.mtl",         MAT_FILE);
  }

  public static void updateMap(double xOff, double zOff) {
    lX += xOff;
    if (lX >= Core.CHUNK_SIZE) {
      lX -= Core.CHUNK_SIZE; gX++;
      for (int y = 0; y < CHUNKS.length; y++) {
        for (int x = 1; x < CHUNKS[y].length; x++) {
          CHUNKS[y][x-1] = CHUNKS[y][x];
        }
        CHUNKS[y][CHUNKS[y].length-1] = new Chunk(gX+CHUNKS[y].length/2, gZ+y-CHUNKS.length/2);
      }
    }
    else if (lX < 0) {
      lX += Core.CHUNK_SIZE; gX--;
      for (int y = 0; y < CHUNKS.length; y++) {
        for (int x = CHUNKS[y].length-2; x >= 0; x--) {
          CHUNKS[y][x+1] = CHUNKS[y][x];
        }
        CHUNKS[y][0] = new Chunk(gX-CHUNKS[y].length/2, gZ+y-CHUNKS.length/2);
      }
    }
    lZ += zOff;
    if (lZ >= Core.CHUNK_SIZE) {
      lZ -= Core.CHUNK_SIZE; gZ++;
      for (int y = 1; y < CHUNKS.length; y++) {
        CHUNKS[y-1] = CHUNKS[y];
      }
      CHUNKS[CHUNKS.length-1] = new Chunk[CHUNKS[0].length];
      for (int x = 0; x < CHUNKS[0].length; x++) {
        CHUNKS[CHUNKS.length-1][x] = new Chunk(gX+x-CHUNKS[0].length/2, gZ+CHUNKS.length-1-CHUNKS.length/2);
      }
    }
    else if (lZ < 0) {
      lZ += Core.CHUNK_SIZE; gZ--;
      for (int y = CHUNKS.length-2; y >= 0; y--) {
        CHUNKS[y+1] = CHUNKS[y];
      }
      CHUNKS[0] = new Chunk[CHUNKS[0].length];
      for (int x = 0; x < CHUNKS[0].length; x++) {
        CHUNKS[0][x] = new Chunk(gX+x-CHUNKS[0].length/2, gZ-CHUNKS.length/2);
      }
    }

    Core.heightMap = MapGenerator.generateHeights(lX+gX*Core.CHUNK_SIZE, lZ+gZ*Core.CHUNK_SIZE, MAP_WIDTH, MAP_HEIGHT, MAP_OCTAVES, false);
    Core.img = ImageProc.mapToImage(heightMap, MAP_WIDTH, MAP_HEIGHT);

    WINDOW.PANEL.repaint();
  }

  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    gra.fillRect(0, 0, WINDOW.screenWidth(), WINDOW.screenHeight());
    int size = Math.min((int)(WINDOW.screenWidth()/MAP_RATIO), WINDOW.screenHeight());
    gra.drawImage(img.getScaledInstance((int)(size*MAP_RATIO), size, BufferedImage.SCALE_DEFAULT), 0, 0, null);
    for (int y = 0; y < CHUNKS.length; y++) {
      for (int x = 0; x < CHUNKS[y].length; x++) {
        gra.drawImage(CHUNKS[y][x].getImg(), (int)((size*MAP_RATIO)+(WINDOW.screenWidth()-(size*MAP_RATIO))/2.0-lX+(x-CHUNKS[y].length/2)*CHUNK_SIZE), (int)(WINDOW.screenHeight()/2.0-lZ+(y-CHUNKS.length/2)*CHUNK_SIZE), null);
      }
    }
  }

  private static final String MAT_FILE = "newmtl mat\n"+
  "Ka 1.000 1.000 1.000\n"+
  "Kd 1.000 1.000 1.000\n"+
  // "Ks 0.000 0.000 0.000\n"+
  "d 1.0\n"+
  "illum 1\n"+
  "map_Ka terrain_map.png\n"+
  "map_Kd terrain_map.png\n";
  // "map_Ks final.png\n";
}
