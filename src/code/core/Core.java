package code.core;

import java.awt.image.BufferedImage;
import java.util.Random;

import code.math.IOHelp;
import code.math.MathHelp;
import code.models.Map3D;

import java.awt.Graphics;
import java.awt.Image;

public abstract class Core {

  public static final Window WINDOW;

  private static final double ROOT_TWO = Math.sqrt(2);
  private static final double INVERSE_ROOT_TWO = 1/ROOT_TWO;

  private static final int MAP_WIDTH    = 128;
  private static final int MAP_HEIGHT   = 128;
  private static final double MAP_RATIO = 1.0*MAP_WIDTH/MAP_HEIGHT;

  private static final int CYCLES = 25;

  // private static final int LAYERS = 5;

  private static volatile Image img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, 2);

  static {
    WINDOW = new Window();
    WINDOW.setFullscreen(false);
  }

  public static void main(String[] args) {
    int[] map = randomNoise();
    IOHelp.writeImage("../results/rnd.png", (BufferedImage)mapToImage(map));
    for (int i = 0; i < CYCLES; i++) map = smoothMap(map);
    img = mapToImage(map);
    IOHelp.writeImage("../results/avg.png", (BufferedImage)img);
    IOHelp.saveToFile("../results/map.obj", new Map3D(map, MAP_WIDTH, MAP_HEIGHT).toString());
    WINDOW.PANEL.repaint();
  }

  public static int[] randomNoise() {
    return seededNoise(new Random());
  }

  public static int[] seededNoise(Random random) {
    int[] map = new int[MAP_WIDTH*MAP_HEIGHT];
    for (int i = 0; i < MAP_WIDTH; i++) {
      for (int j = 0; j < MAP_HEIGHT; j++) {
        int height = random.nextInt(256);
        map[i + MAP_WIDTH * j] = 255 << 24 | height << 16 | height << 8 | height;
      }
    }

    return map;
  }

  public static int[] smoothMap(int[] map) {
    int[] res = new int[map.length];

    for (int i = 0; i < MAP_WIDTH; i++) {
      for (int j = 0; j < MAP_HEIGHT; j++) {

        int left  = ((MAP_WIDTH + i - 1)%MAP_WIDTH);
        int right = ((i + 1)%MAP_WIDTH);
        int up    = MAP_WIDTH * ((MAP_HEIGHT + j - 1)%MAP_HEIGHT);
        int down  = MAP_WIDTH * ((j + 1)%MAP_HEIGHT);

        int edges = (int)MathHelp.avg(map[left + MAP_WIDTH * j] & 255, map[right + MAP_WIDTH * j] & 255, map[i + up] & 255, map[i + down] & 255);
        int corners = (int)MathHelp.avg(map[left + up] & 255, map[right + up] & 255, map[left + down] & 255, map[right + down] & 255);
        int ec = (int)((edges + INVERSE_ROOT_TWO * corners) / (1+INVERSE_ROOT_TWO));
        int height     = (int)MathHelp.avg(map[i + MAP_WIDTH * j] & 255, ec);

        res[i + MAP_WIDTH * j] = 255 << 24 | height << 16 | height << 8 | height;
      }
    }

    return res;
  }

  public static Image mapToImage(int[] map) {
    BufferedImage img = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    img.setRGB(0, 0, MAP_WIDTH, MAP_HEIGHT, map, 0, MAP_WIDTH);
    return img;
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
