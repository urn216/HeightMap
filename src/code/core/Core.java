package code.core;

import java.awt.image.BufferedImage;

import code.math.IOHelp;
import code.models.Map3D;

import java.awt.Graphics;
import java.awt.Image;

public abstract class Core {

  public static final Window WINDOW = new Window();

  private static final int MAP_WIDTH    = 100;
  private static final int MAP_HEIGHT   = 100;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static volatile Image img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);
  private static volatile Map   map;

  static {
    WINDOW.setFullscreen(false);
  }

  public static void main(String[] args) {
    map = Map.generateMap(MAP_WIDTH, MAP_HEIGHT);
    img = ImageProc.mapToImage(map.getMap(), MAP_WIDTH, MAP_HEIGHT);
    IOHelp.writeImage("../results/final.png", (BufferedImage)img);
    IOHelp.saveToFile("../results/map.obj", new Map3D(map.getMap(), MAP_WIDTH, MAP_HEIGHT).toString());
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
