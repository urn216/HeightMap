package code.core;

import java.awt.image.BufferedImage;

import mki.io.FileIO;
// import code.models.Chunk;
import code.models.Map3D;
import code.models.MapCubes;

import java.awt.Graphics;

public abstract class Core {

  public static final Window WINDOW = new Window();

  public static double MAP_SCALE = 1;
  public static final int MAP_OCTAVES  = 10;
  // public static double MAP_SCALE = 1/Math.pow(2, 55);

  private static final int MAP_WIDTH    = 128;
  private static final int MAP_HEIGHT   = 128;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static volatile BufferedImage img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);

  private static volatile double x = 0, y = 0;

  private static volatile float[] heightMap;

  static {
    WINDOW.setFullscreen(false);

    FileIO.createDir("../results/");

    MapGenerator.initialise();
  }

  public static void main(String[] args) {
    Core.heightMap = MapGenerator.generateHeights(x, y, MAP_WIDTH, MAP_HEIGHT, MAP_OCTAVES, true);
    Core.img = ImageProc.mapToImage(Core.heightMap, MAP_WIDTH, MAP_HEIGHT);

    Core.printScreenToFiles();

    // for (int i = 0; i < 3; i++) {
    //   for (int j = 0; j < 3; j++) {
    //     FileIO.saveToFile("../results/chunk_"+j+"_"+i+".obj", new Chunk(j, i).toString());
    //   }
    // }

    WINDOW.PANEL.repaint();
  }

  public static void printScreenToFiles() {
    FileIO.writeImage("../results/final.png",    img);
    FileIO.saveToFile("../results/map.obj",      new Map3D(heightMap, MAP_WIDTH, MAP_HEIGHT).toString());
    FileIO.saveToFile("../results/mapBlock.obj", new MapCubes(heightMap, MAP_WIDTH, MAP_HEIGHT).toString());
    FileIO.saveToFile("../results/mat.mtl",      MAT_FILE);
  }

  public static void updateMap(double xOff, double yOff) {
    Core.heightMap = MapGenerator.generateHeights(x+=xOff, y+=yOff, MAP_WIDTH, MAP_HEIGHT, MAP_OCTAVES, false);
    Core.img = ImageProc.mapToImage(heightMap, MAP_WIDTH, MAP_HEIGHT);

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

  private static final String MAT_FILE = "newmtl mat\n"+
  "Ka 1.000 1.000 1.000\n"+
  "Kd 1.000 1.000 1.000\n"+
  // "Ks 0.000 0.000 0.000\n"+
  "d 1.0\n"+
  "illum 1\n"+
  "map_Ka final.png\n"+
  "map_Kd final.png\n";
  // "map_Ks final.png\n";
}
