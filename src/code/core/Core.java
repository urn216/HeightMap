package code.core;

import java.awt.image.BufferedImage;

import mki.io.FileIO;
import code.models.Map3D;
import code.models.MapCubes;

import java.awt.Graphics;

public abstract class Core {

  public static final Window WINDOW = new Window();

  public static double MAP_SCALE = 1;
  // public static double MAP_SCALE = 1/Math.pow(2, 55);

  private static final int MAP_WIDTH    = 128;
  private static final int MAP_HEIGHT   = 128;
  private static final int MAP_OCTAVES  = 10;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static volatile BufferedImage img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);
  private static volatile Map map;

  private static volatile double x = 0, y = 0;

  static {
    WINDOW.setFullscreen(false);

    FileIO.createDir("../results/");
  }

  public static void main(String[] args) {
    Core.map = Map.generateMap(MAP_WIDTH, MAP_HEIGHT, MAP_OCTAVES);

    Core.img = ImageProc.mapToImage(map.getHeightMap(), MAP_WIDTH, MAP_HEIGHT);

    Core.printScreenToFiles();

    WINDOW.PANEL.repaint();
  }

  public static void printScreenToFiles() {
    FileIO.writeImage("../results/final.png",    img);
    FileIO.saveToFile("../results/map.obj",      new Map3D(map.getHeightMap(), map.getWidth(), map.getHeight()).toString());
    FileIO.saveToFile("../results/mapBlock.obj", new MapCubes(map.getHeightMap(), map.getWidth(), map.getHeight()).toString());
  }

  public static void updateMap(double xOff, double yOff) {
    map.generateGrid(x+=xOff, y+=yOff, MAP_OCTAVES, false);
    img = ImageProc.mapToImage(map.getHeightMap(), MAP_WIDTH, MAP_HEIGHT);
    WINDOW.PANEL.repaint();
  }

  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    int size = Math.min((int)(WINDOW.screenWidth()/MAP_RATIO), WINDOW.screenHeight());
    gra.drawImage(img.getScaledInstance((int)(size*MAP_RATIO), size, BufferedImage.SCALE_DEFAULT), 0, 0, null);
  }
}
