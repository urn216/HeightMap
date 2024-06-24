package code.core;

import java.awt.Graphics2D;

import mki.math.TriFunction;
import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.rendering.Constants;
import mki.ui.components.*;
import mki.ui.components.interactables.*;
import mki.ui.control.*;
import mki.ui.control.UIColours.ColourSet;
import mki.ui.elements.*;
import mki.world.Material;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.045;
  private static final double BUFFER_HEIGHT = 0.007;

  private static final UIComponent[] optionList = {
    new UIToggle(
      "Ring", 
      ( ) -> Core.GLOBAL_SETTINGS.getBoolSetting("g_ringworld"), 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("g_ringworld", b)
    ),
    new UIText("", 1, 0),
    new UIToggle(
      "Fullscreen", 
      Core.WINDOW::isFullScreen, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_fullScreen", b)
    ),
    new UIDropDown<Vector2I>(
      "Resolution: %s",
      ( ) -> {
        Vector2I v = Core.getActiveCam().getImageDimensions();
        return v.x + " x " + v.y;
      },
      (v) -> Core.GLOBAL_SETTINGS.setVector2ISetting("v_resolution", v),
      new Vector2I(256 , 144 ),
      new Vector2I(512 , 288 ),
      new Vector2I(560 , 315 ),
      new Vector2I(1280, 720 ),
      new Vector2I(1920, 1080)
    ),
    new UIText("", 1, 0),
    new UIToggle(
      "Normal Maps", 
      Constants::usesNormalMap, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_normalmapping", b)
    ),
    new UIToggle(
      "Dynamic Lights",
      Constants::usesDynamicRasterLighting, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_fancylighting", b)
    ),
    new UIDropDown<TriFunction<int[][], Double, Double, Integer>>(
      "Filtering Mode: %s",
      ( ) -> {
        return "NN";//Constants.getFilteringMode().toString();
      },
      (f) -> {
        Constants.setFilteringMode(f);
      },
      Material::getNearestNeighbourFilteringTexel,
      Material::getBilinearFilteringTexel
    ),
    new UISlider.Double(
      "FOV: %.0f",
      ( ) -> Core.getActiveCam().getFieldOfView(),
      (f) -> Core.GLOBAL_SETTINGS.setDoubleSetting("v_fieldOfView", f),
      30,
      100
    ),
    new UIText("", 1, 0),
    new UIButton("Reset To Defaults", Core.GLOBAL_SETTINGS::resetToDefault),
    new UIButton("Back", UIController::back)
  };

  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement outPanel = leftList(0.2,
      new UIButton("Play"   , () -> UIController.setState(UIState.NEW_GAME)),
      new UIButton("Options", () -> UIController.setState(UIState.OPTIONS) ),
      new UIButton("Quit"   , Core::quitToDesk)
    );

    UITextfield seed = new UITextfield("Seed (Leave Blank For Random)", 100, 1);

    UIElement newGame = leftList(0.2,
      seed,
      new UIText("", 0, 0),
      new UIButton("Start New Game", () -> {
        try {Core.loadScene(Long.parseLong(seed.getText()));} 
        catch(NumberFormatException e) {Core.loadScene(System.currentTimeMillis());}
      }),
      new UIButton("Back", UIController::back)
    );

    UIElement options = leftList(0.2, optionList);

    mainMenu.addState(UIState.DEFAULT , outPanel);
    mainMenu.addState(UIState.NEW_GAME, newGame  , UIState.DEFAULT);
    mainMenu.addState(UIState.OPTIONS , options  , UIState.DEFAULT, checkSettings);

    mainMenu.clear();
    
    return mainMenu;
  }

  public static UIPane createHUD() {
    UIPane HUD = new UIPane();

    UIText fps = new UIText(() -> String.format("FPS: %.0f", Core.getFps()), 1, 1);
    UIElement fpsCounter = new ElemListVert(
      new Vector2(0   , 0),
      new Vector2(0.07, UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, fps))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      new UIComponent[]{fps},
      UIElement.TRANSITION_SLIDE_LEFT
    );
    
    UIElement outPanel = leftList(
      new UIButton("Return"   , UIController::back),
      new UIButton("Main Menu", Core::quitToMenu  ),
      new UIButton("Quit"     , Core::quitToDesk  )
    );

    UIText     gC = new UIText(() -> "("+World.getCentreChunkX()+", "+World.getCentreChunkZ()+")", 1, 0);
    UINumfield gX = new UINumfield();
    UINumfield gZ = new UINumfield();
    UIButton   go = new UIButton("Teleport", () -> World.moveToChunk((int)gX.getValue(), (int)gZ.getValue()));

    UIElement teleport = new UIElement(
      new Vector2(0, 1-COMPON_HEIGHT*2.5-BUFFER_HEIGHT*4), 
      new Vector2(0.12, 1), 
      UIElement.TRANSITION_SLIDE_DOWN_LEFT
    ) {
      {this.components = new UIComponent[]{gC, gX, gZ, go};}
      @Override
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, ColourSet c) {
        float buffer = (float)BUFFER_HEIGHT*screenSizeY;
        float height = (float)COMPON_HEIGHT*screenSizeY;
        float halfw  = (float)(bR.x-tL.x-buffer*3)/2;
        float x = (float)(tL.x+buffer);
        float y = (float)(tL.y+buffer);
        gX.draw(g, x             , y                     , halfw         , height  , c);
        gZ.draw(g, x+buffer+halfw, y                     , halfw         , height  , c);
        gC.draw(g, x             , y+buffer  +height     , halfw*2+buffer, height/2, c);
        go.draw(g, x             , y+buffer*2+height*1.5f, halfw*2+buffer, height  , c);
      }
    };

    UIElement options = rightList(0.2, optionList);

    HUD.setModeParent(UIState.DEFAULT, UIState.OPTIONS);

    HUD.addState(UIState.DEFAULT, fpsCounter);
    HUD.addState(UIState.OPTIONS, outPanel   , UIState.DEFAULT , checkSettings);
    HUD.addState(UIState.OPTIONS, options   );
    HUD.addState(UIState.OPTIONS, fpsCounter);
    HUD.addState(UIState.OPTIONS, teleport);

    HUD.clear();
    
    return HUD;
  }

  protected static UIElement leftList(UIComponent... components) {
    return leftList(0.078, components);
  }

  protected static UIElement leftList(double width, UIComponent... components) {
    double height = UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components));
    return new ElemListVert(
      new Vector2(0    , 0.4-height/2),
      new Vector2(width, 0.4+height/2),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_LEFT
    );
  }

  protected static UIElement rightList(UIComponent... components) {
    return rightList(0.07, components);
  }

  protected static UIElement rightList(double width, UIComponent... components) {
    double height = UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components));
    return new ElemListVert(
      new Vector2(1-width, 0.4-height/2),
      new Vector2(1      , 0.4+height/2),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_RIGHT
    );
  }

  private static final ElemConfirmation settingsChanged = new ElemConfirmation(
    new Vector2(0.35, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2),
    new Vector2(0.65, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    BUFFER_HEIGHT, 
    UIElement.TRANSITION_SLIDE_DOWN,
    () -> {Core.GLOBAL_SETTINGS.saveChanges();   UIController.retState();},
    () -> {
      Core.GLOBAL_SETTINGS.revertChanges();
      Core.getActiveCam().setImageDimensions(
        Core.GLOBAL_SETTINGS.getIntSetting("v_resolution_X"), 
        Core.GLOBAL_SETTINGS.getIntSetting("v_resolution_Y")
      );
      UIController.retState();
    },
    "Save Changes?"
  );
  
  /**
  * A lambda function which, in place of transitioning back a step,
  * checks if the global settings have been changed and if so, 
  * brings up a confirmation dialogue to handle the changes before transitioning back.
  */
  public static final UIAction checkSettings = () -> {
    if (Core.GLOBAL_SETTINGS.hasChanged()) UIController.displayTempElement(settingsChanged);
    else UIController.retState();
  };
}
