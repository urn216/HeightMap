package code.core;

import java.awt.image.BufferedImage;

import mki.io.FileIO;
import code.generation.ImageProc;
import code.models.Map3D;
import code.models.MapCubes;

import java.awt.Graphics;

public abstract class Core_2D {

  public static final Window_2D WINDOW = new Window_2D();

  private static final int MAP_WIDTH    = 128;
  private static final int MAP_HEIGHT   = 128;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static volatile BufferedImage img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);

  private static volatile double lX = 0, lZ = 0;
  private static volatile double  x = 0,  z = 0;

  private static volatile float[] heightMap;

  public static void main(String[] args) {

    WINDOW.setFullscreen(false);

    FileIO.createDir("../results/");

    World.generateNewWorld();

    Core_2D.heightMap = World.getTerrainGenerator().generateHeights(lX, lZ, MAP_WIDTH, MAP_HEIGHT, Core.MAP_OCTAVES, true);
    Core_2D.img = ImageProc.mapToImage(Core_2D.heightMap, MAP_WIDTH, MAP_HEIGHT);

    Core_2D.printScreenToFiles();

    WINDOW.PANEL.repaint();

    play();
  }

  public static void printScreenToFiles() {
    FileIO.writeImage("../results/terrain_map.png", img);
    FileIO.saveToFile("../results/map.obj",         new Map3D(heightMap, MAP_WIDTH, MAP_HEIGHT, 0, new int[]{~0}).getModel().toString());
    FileIO.saveToFile("../results/mapBlock.obj",    new MapCubes(heightMap, MAP_WIDTH, MAP_HEIGHT, new int[]{~0}).toString());
    FileIO.saveToFile("../results/mat.mtl",         MAT_FILE);
  }

  public static void play() {
    while(true) {
      WINDOW.PANEL.repaint();
    }
  }

  public static void updateMap(double xOff, double zOff) {
    lX += xOff;
    x  += xOff;
    while(lX >= Core.CHUNK_SIZE) {
      lX -= Core.CHUNK_SIZE;
      World.shiftXIncr();
    }
    while(lX < 0) {
      lX += Core.CHUNK_SIZE;
      World.shiftXDecr();
    }
    lZ += zOff;
    z  += zOff;
    while(lZ >= Core.CHUNK_SIZE) {
      lZ -= Core.CHUNK_SIZE;
      World.shiftZIncr();
    }
    while(lZ < 0) {
      lZ += Core.CHUNK_SIZE;
      World.shiftZDecr();
    }

    Core_2D.heightMap = World.getTerrainGenerator().generateHeights(x, z, MAP_WIDTH, MAP_HEIGHT, Core.MAP_OCTAVES, false);
    Core_2D.img = ImageProc.mapToImage(heightMap, MAP_WIDTH, MAP_HEIGHT);
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
    World.draw(gra, size, MAP_RATIO, lX, lZ);
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
