package code.core;

import java.awt.image.BufferedImage;

import code.generation.Chunk;
import code.models.Ring;
import mki.io.FileIO;
import mki.math.vector.Vector2I;
import mki.math.vector.Vector3;
import mki.rendering.Constants;
import mki.rendering.renderers.Renderer;
import mki.ui.control.UIController;
import mki.world.Camera3D;
// import mki.world.Material;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

enum State {
  MAINMENU,
  RUN,
  SPLASH
}

public abstract class Core {

  public static final Window WINDOW = new Window("Heightmap", (x, y) -> {});
  
  public static final Settings GLOBAL_SETTINGS = new Settings();
  
  private static final long TICKS_PER_SECOND = 60;
  private static final long MILLISECONDS_PER_TICK = 900/TICKS_PER_SECOND;

  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  private static final BufferedImage SPLASH = FileIO.readImage("splash.png");

  private static State state = State.SPLASH;
  
  private static boolean quit = false;

  public static double MAP_RANGE_SCALE  = 1000;
  public static double MAP_HEIGHT_SCALE = 3000;
  public static final int MAP_OCTAVES   = 20;
  // public static double MAP_SCALE = 1/Math.pow(2, 55);

  public static final int FULL_BRIGHT = ~0;
  public static final int SOME_DIM = (255<<24) | (150<<16) | (150<<8) | (150);
  
  public static final int CHUNK_POW = 7;
  public static final int CHUNK_SIZE = (int)Math.pow(2, CHUNK_POW);
  public static final int RENDER_RADIUS = 16;
  
  private static Camera3D cam;
  private static double   camFOVChange = -1;
  private static Vector2I camResChange = null;

  private static long pTTime = System.currentTimeMillis();
  private static long pFTime = System.currentTimeMillis();
  
  private static double fps = 0;
  private static int fCount = 0;

  private static Ring menuRing;

  /**
   * Main method. Called on execution. Performs basic startup
   *
   * @param args Ignored for now
   * @throws InterruptedException if thread sleeping fails for whatever reason. Catastrophic error should kill process.
   */
  public static void main(String[] args) throws InterruptedException {
    WINDOW.FRAME.setBackground(new Color(173, 173, 173));
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.putPane("HUD"      , UICreator.createHUD ());

    // Constants.setNormalMapUse(true);
    // Constants.setFilteringMode(Material::getBilinearFilteringTexel);

    camResChange = GLOBAL_SETTINGS.getVector2ISetting("v_resolution");
    cam = new Camera3D(
      new Vector3(),
      camResChange.x,
      camResChange.y,
      GLOBAL_SETTINGS.getDoubleSetting("v_fieldOfView"),
      Renderer.rasterizer()
    );
    camResChange = null;

    playGame();
  }
  
  /**
  * @return the currently active camera
  */
  public static Camera3D getActiveCam() {
    return cam;
  }

  public static double getFps() {
    return fps;
  }

  public static State getState() {
    return state;
  }

  public static void setFieldOfView(double f) {
    camFOVChange = f;
  }

  public static void setResolution(Vector2I v) {
    camResChange = v;
  }

  public static void printChunkToFiles() {
    Chunk c = World.getChunks()[RENDER_RADIUS][RENDER_RADIUS];
    FileIO.writeImage("../results/terrain_map.png", c.getImg());
    FileIO.saveToFile("../results/chunk.obj",       c.getBody().getModel().toString());
    FileIO.saveToFile("../results/mat.mtl",         MAT_FILE);
  }

  public static void quitToMenu() {
    World.endWorld();

    Constants.setFogUse(false);

    menuRing = new Ring.Surface(World.getTerrainGenerator(), 1.0/5000);
    menuRing.setRotation(0, 26, -45);
    menuRing.setPosition(new Vector3(1000000, -740000, 10000000));

    cam.setPosition(new Vector3());
    cam.resetRotation();
    Core.state = State.MAINMENU;
    UIController.setCurrentPane("Main Menu");
  }

  public static void loadScene(long seed) {
    World.generateNewWorld(seed);

    Constants.setFogUse(true);

    cam.setPosition(new Vector3(0, World.returnToSpawn()+2, 0));
    cam.resetRotation();
    Core.state = State.RUN;
    UIController.setCurrentPane("HUD");

    menuRing = null;
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    if (quit) System.exit(1);
    
    quit = true;
  }

  public static void playGame() throws InterruptedException {
    while (true) {
      long tickTime = System.currentTimeMillis();
      long deltaTimeMillis = tickTime - pTTime;
      pTTime  = tickTime;

      switch (state) {
        case SPLASH:
          if (tickTime-START_TIME >= SPLASH_TIME) {
            Controls.initialiseControls(WINDOW.FRAME);
            quitToMenu();
          }
        break;
        case MAINMENU:
          menuRing.offsetPitch(0.01);
          cam.draw();
        break;
        case RUN:
          Controls.doInput(deltaTimeMillis, cam);
          World.handleChunkInsertion();
        default:
          cam.draw();
        break;
      }

      if (camFOVChange > 0) {
        cam.setFieldOfView(camFOVChange);
        camFOVChange = -1;
      }

      if (camResChange != null) {
        cam.setImageDimensions(camResChange.x, camResChange.y);
        camResChange = null;
      }

      if (quit) {
        System.exit(0);
      }
      WINDOW.PANEL.repaint();
      
      tickTime = System.currentTimeMillis() - tickTime;
      Thread.sleep(Math.max(MILLISECONDS_PER_TICK - tickTime, 0));
    }
  }

  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {

    switch (state) {
      case SPLASH:
        gra.drawImage(SPLASH, (WINDOW.screenWidth()-SPLASH.getWidth())/2, (WINDOW.screenHeight()-SPLASH.getHeight())/2, null);
      break;
      default:
        int size = Math.min(WINDOW.screenWidth(), (int)(WINDOW.screenHeight()/cam.getImageAspectRatio()));
        gra.drawImage(cam.getImage().getScaledInstance(size, (int)(size*cam.getImageAspectRatio()), BufferedImage.SCALE_DEFAULT), 0, 0, null);
    
        UIController.draw((Graphics2D)gra, WINDOW.screenWidth(), WINDOW.screenHeight());
      break;
    }

    long cFTime = System.currentTimeMillis();

    if (cFTime-pFTime >= 1000) {
      fps = fCount*1000.0/(cFTime-pFTime);
      // System.out.println(fps);
      pFTime = cFTime;
      fCount=0;
    }
    
    fCount++;
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
