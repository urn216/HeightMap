package code.math;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.*;

import java.awt.Desktop;

import java.util.List;
import java.util.ArrayList;

/**
* Non initializable class with helper methods and fields for use in Input/Output operations
*/
public abstract class IOHelp {
  
  //-------------------------------------------------------------------------------------
  //                              FILE TRANSFER HELPERS
  //-------------------------------------------------------------------------------------
  
  public static final void saveToFile(String filename, String... content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      for (String s : content) out.print(s);
      out.close();
    } catch(IOException e){System.err.println("Saving failed " + e);}
  }
  
  public static final void copyContents(File source, Path dest) {
    try {
      Files.copy(source.toPath(), dest, StandardCopyOption.valueOf("REPLACE_EXISTING"));
    } catch(IOException e){System.err.println("Copying failed " + e);}
    if (source.isDirectory()) {
      for (File fi : source.listFiles()) {
        IOHelp.copyContents(fi, dest.resolve(fi.toPath().getFileName()));
      }
    }
  }
  
  public static final void copyContents(InputStream source, Path dest) {
    try {
      Files.copy(source, dest, StandardCopyOption.valueOf("REPLACE_EXISTING"));
    } catch(IOException e){System.err.println("Copying failed " + e);}
  }
  
  /**
  * Creates an empty file directory at the given location.
  *
  * Deletes everything within the directory if something exists in its place. Be careful with this power.
  *
  * @param filename the directory to create.
  *
  * @return true if creation was a success; false if something went wrong
  */
  public static final boolean createDir(String filename) {
    File fi = new File(filename);
    if (fi.exists()) {
      for (File f : fi.listFiles()) {
        if (!delete(f)) return false;
      }
      return true;
    }
    else {return fi.mkdirs();}
  }
  
  public static final boolean exists(String filename) {return new File(filename).exists();}
  
  public static boolean delete(File f) {
    if (f.isDirectory()) {
      for (File fi : f.listFiles()) {
        IOHelp.delete(fi);
      }
    }
    return f.delete();
  }
  
  public static final List<String> readAllLines(String filename, boolean inJar) {
    try {
      if (!inJar) return Files.readAllLines(Paths.get(filename));

      BufferedReader file = new BufferedReader(new InputStreamReader(IOHelp.class.getResourceAsStream(filename)));
      List<String> allLines = new ArrayList<String>();
      String line;
      while ((line = file.readLine()) != null) {
        allLines.add(line);
      }
      return allLines;
    } catch(IOException e){System.err.println("Reading failed: " + e);}

    return new ArrayList<String>();
  }
  
  /**
  * @param filename The path of the texture file desired
  *
  * @return a buffered image of the desired texture
  */
  public static final BufferedImage readImage(String filename) {
    try {
      return ImageIO.read(IOHelp.class.getResourceAsStream("/data/textures/" + filename));
    }catch(IOException e){System.err.println("Failed to find Texture at " + filename);}
    return null;
  }
  
  /**
  * @param filename The path of the texture file desired
  *
  * @return a square texture in RGBA array format
  */
  public static final int[] readImageInt(String filename) {
    BufferedImage img = IOHelp.readImage(filename);
    
    return img.getRGB(0, 0, img.getHeight(), img.getHeight(), null, 0, img.getWidth());
  }

  public static final void writeImage(String filename, BufferedImage img) {
    try {
      ImageIO.write(img, "PNG", new File(filename));
    } catch (IOException e) {System.err.println("Failed to write image to " + filename);}
  }

  /**
   * If supported by the user's {@code Operating System}, 
   * this method will attempt to open the given file path using the {@code OS}'s default application for the given file
   * 
   * @param filename the path to the desired file
   * 
   * @return {@code true} if the file was opened successfully
   */
  public static final boolean openProgram(String filename) {
    if (!Desktop.isDesktopSupported()) return false;
    try {
      Desktop.getDesktop().open(new File(filename));
      return true;
    } catch (IOException e) {System.err.println("Failed to open file at " + filename);}
    return false;
  }
}