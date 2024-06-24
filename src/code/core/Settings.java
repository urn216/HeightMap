package code.core;

import mki.io.FileIO;
import mki.math.vector.Vector2I;
import mki.ui.control.UIActionSetter;
import mki.rendering.Constants;

import java.util.List;

public class Settings {

  private static final int I_NAME    = 0;
  private static final int I_CURRENT = 1;
  private static final int I_SAVED   = 2;
  private static final int I_DEFAULT = 3;
  private static final int I_ACTION  = 4;


  private static final String GLOBAL_SETTINGS_LOCATION = "../settings.txt";

  private static final Vector2I res = new Vector2I(512, 288);

  //      NAME       |CURRENT|SAVED|DEFAULT|                       ACTION
  private static final Object[][] GLOBAL_SETTINGS = {
    {"g_ringworld"    , false, null, false, new UIActionSetter<Boolean >() {public void set(Boolean  b) {World      .setRing                 (b);}}},
    {"v_fieldOfView"  ,  80.0, null,  80.0, new UIActionSetter<Double  >() {public void set(Double   v) {Core       .setFieldOfView          (v);}}},
    {"v_fullScreen"   , false, null, false, new UIActionSetter<Boolean >() {public void set(Boolean  b) {Core.WINDOW.setFullscreen           (b);}}},
    {"v_fancylighting", false, null, false, new UIActionSetter<Boolean >() {public void set(Boolean  b) {Constants  .setDynamicRasterLighting(b);}}},
    {"v_normalmapping", false, null, false, new UIActionSetter<Boolean >() {public void set(Boolean  b) {Constants  .setNormalMapUse         (b);}}},
    {"v_resolution"   ,   res, null,   res, new UIActionSetter<Vector2I>() {public void set(Vector2I v) {Core       .setResolution           (v);}}},
  };

  private final String fileName;

  private final Object[][] settings;

  /**
   * Initialises the user-defined settings for the program
   */
  public Settings() {
    this.fileName = GLOBAL_SETTINGS_LOCATION;
    this.settings = GLOBAL_SETTINGS;
    revertChanges();
  }

  /**
   * Initialises the user-defined settings for the program
   */
  public Settings(String fileName, Object[]... settings) {
    this.fileName = "../" + fileName + ".txt";
    this.settings = settings;
    revertChanges();
  }

  /**
   * Helper method for retrieving the index associated with a given setting name
   * 
   * @param name the name of the setting to look for the index of
   * 
   * @return the index of the found setting - or {@code -1} if not found
   */
  private int getIndex(String name) {
    for (int i = 0; i < settings.length; i++) {
      if (((String)settings[i][I_NAME]).equals(name)) return i;
    }
    return -1;
  }

  /**
   * Gets the current {@code String} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public String getStringSetting(String name) {
    int i = getIndex(name);
    return i < 0 ? null : (String)settings[i][I_CURRENT];
  }

  /**
   * Sets a given {@code String} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  @SuppressWarnings("unchecked")
  public void setStringSetting(String name, String value) {
    int i = getIndex(name);
    if (!(settings[i][I_DEFAULT] instanceof String)) return;

    settings[i][I_CURRENT] = value;
    ((UIActionSetter<String>)settings[i][I_ACTION]).set(value);
  }

  /**
   * Gets the current {@code int} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public int getIntSetting(String name) {
    int i = getIndex(name);
    return i < 0 ? 0 : (int)settings[i][I_CURRENT];
  }

  /**
   * Sets a given {@code int} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  @SuppressWarnings("unchecked")
  public void setIntSetting(String name, int value) {
    int i = getIndex(name);
    if (!(settings[i][I_DEFAULT] instanceof Integer)) return;

    settings[i][I_CURRENT] = value;
    ((UIActionSetter<Integer>)settings[i][I_ACTION]).set(value);
  }

  /**
   * Gets the current {@code double} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public double getDoubleSetting(String name) {
    int i = getIndex(name);
    return i < 0 ? Double.NaN : (double)settings[i][I_CURRENT];
  }

  /**
   * Sets a given {@code double} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  @SuppressWarnings("unchecked")
  public void setDoubleSetting(String name, double value) {
    int i = getIndex(name);
    if (!(settings[i][I_DEFAULT] instanceof Double)) return;

    settings[i][I_CURRENT] = value;
    ((UIActionSetter<Double>)settings[i][I_ACTION]).set(value);
  }

  /**
   * Gets the current {@code boolean} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public boolean getBoolSetting(String name) {
    int i = getIndex(name);
    return i < 0 ? false : (boolean)settings[i][I_CURRENT];
  }

  /**
   * Sets a given {@code boolean} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  @SuppressWarnings("unchecked")
  public void setBoolSetting(String name, boolean value) {
    int i = getIndex(name);
    if (!(settings[i][I_DEFAULT] instanceof Boolean)) return;

    settings[i][I_CURRENT] = value;
    ((UIActionSetter<Boolean>)settings[i][I_ACTION]).set(value);
  }

  /**
   * Gets the current {@code boolean} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public Vector2I getVector2ISetting(String name) {
    int i = getIndex(name);
    return i < 0 ? null : (Vector2I)settings[i][I_CURRENT];
  }

  /**
   * Sets a given {@code boolean} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  @SuppressWarnings("unchecked")
  public void setVector2ISetting(String name, Vector2I value) {
    int i = getIndex(name);
    if (!(settings[i][I_DEFAULT] instanceof Vector2I)) return;

    settings[i][I_CURRENT] = value;
    ((UIActionSetter<Vector2I>)settings[i][I_ACTION]).set(value);
  }

  /**
   * Checks whether or not the settings have been changed from their last saved state
   * 
   * @return {@code true} if any settings have been changed
   */
  public boolean hasChanged() {
    for (int i = 0; i < settings.length; i++) {
      if (!settings[i][I_CURRENT].equals(settings[i][I_SAVED])) return true;
    }
    return false;
  }

  /**
   * Saves the current settings to the user's device storage
   */
  public void saveChanges() {
    for (int i = 0; i < settings.length; i++) {
      settings[i][I_SAVED] = settings[i][I_CURRENT];
    }
    FileIO.saveToFile(fileName, toString());
  }

  /**
   * Reverts the settings to the last saved state, or to the default state if no saved state is found
   */
  @SuppressWarnings("unchecked")
  public void revertChanges() {
    for (int i = 0; i < settings.length; i++) {
      settings[i][I_CURRENT] = settings[i][I_SAVED];
      if      (settings[i][I_CURRENT] instanceof Boolean  b) ((UIActionSetter<Boolean >)settings[i][I_ACTION]).set(b);
      else if (settings[i][I_CURRENT] instanceof Integer  l) ((UIActionSetter<Integer >)settings[i][I_ACTION]).set(l);
      else if (settings[i][I_CURRENT] instanceof Double   d) ((UIActionSetter<Double  >)settings[i][I_ACTION]).set(d);
      else if (settings[i][I_CURRENT] instanceof Vector2I v) ((UIActionSetter<Vector2I>)settings[i][I_ACTION]).set(v);
      else if (settings[i][I_CURRENT] instanceof String   s) ((UIActionSetter<String  >)settings[i][I_ACTION]).set(s);
      else {load(); return;}
    }
  }

  /**
   * Resets the settings to their default state, and saves this default to file
   */
  @SuppressWarnings("unchecked")
  public void resetToDefault() {
    for (int i = 0; i < settings.length; i++) {
      settings[i][I_CURRENT] = settings[i][I_DEFAULT];
      if      (settings[i][I_CURRENT] instanceof Boolean  b) ((UIActionSetter<Boolean >)settings[i][I_ACTION]).set(b);
      else if (settings[i][I_CURRENT] instanceof Integer  l) ((UIActionSetter<Integer >)settings[i][I_ACTION]).set(l);
      else if (settings[i][I_CURRENT] instanceof Double   d) ((UIActionSetter<Double  >)settings[i][I_ACTION]).set(d);
      else if (settings[i][I_CURRENT] instanceof Vector2I v) ((UIActionSetter<Vector2I>)settings[i][I_ACTION]).set(v);
      else if (settings[i][I_CURRENT] instanceof String   s) ((UIActionSetter<String  >)settings[i][I_ACTION]).set(s);
    }
  }

  /**
   * Loads the currently saved settings file into the program.
   * <p>
   * if a setting is missing within the file as it is read in, the file will be deleted and replaced with the default settings
   */
  @SuppressWarnings("unchecked")
  private void load() {
    List<String> lines = FileIO.readAllLines(fileName, false);

    if (lines.size() != settings.length) {
      System.out.println("Error loading settings: number of settings mismatch ["+lines.size()+" != "+settings.length+"]");
      resetToDefault();
      saveChanges();
      return;
    }

    for (int i = 0; i < settings.length; i++) {
      String[] entry = lines.get(i).split(" ", 2);

      if (!entry[0].equals(settings[i][I_NAME])) {
        System.out.println("Error loading settings: incorrect setting found ["+entry[0]+". But expected "+settings[i][I_NAME]+"]");
        resetToDefault();
        saveChanges();
        return;
      }

      settings[i][I_CURRENT] = 
      settings[i][I_DEFAULT] instanceof Boolean  ? Boolean .parseBoolean(entry[1]) :
      settings[i][I_DEFAULT] instanceof Integer  ? Integer .parseInt    (entry[1]) :
      settings[i][I_DEFAULT] instanceof Double   ? Double  .parseDouble (entry[1]) :
      settings[i][I_DEFAULT] instanceof Vector2I ? Vector2I.parseVector (entry[1]) :
      entry[1];

      settings[i][I_SAVED] = settings[i][I_CURRENT];
      
      if      (settings[i][I_CURRENT] instanceof Boolean  b) ((UIActionSetter<Boolean >)settings[i][I_ACTION]).set(b);
      else if (settings[i][I_CURRENT] instanceof Integer  l) ((UIActionSetter<Integer >)settings[i][I_ACTION]).set(l);
      else if (settings[i][I_CURRENT] instanceof Double   d) ((UIActionSetter<Double  >)settings[i][I_ACTION]).set(d);
      else if (settings[i][I_CURRENT] instanceof Vector2I v) ((UIActionSetter<Vector2I>)settings[i][I_ACTION]).set(v);
      else if (settings[i][I_CURRENT] instanceof String   s) ((UIActionSetter<String  >)settings[i][I_ACTION]).set(s);
    }
  }

  /**
   * Converts the current settings into a readable {@code String} to be saved to a file
   * 
   * @return a {@code String} representation of the current {@code Settings} object
   */
  public String toString() {
    String res = "";
    for (int i = 0; i < settings.length; i++) {
      res += settings[i][I_NAME] + " " + settings[i][I_SAVED] + "\n";
    }
    return res;
  }
}
