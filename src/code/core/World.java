package code.core;

import java.awt.Graphics;

import code.generation.Chunk;
import code.generation.ChunkGenerator;

public abstract class World {

  private static volatile Chunk[][] chunks;

  private static volatile ChunkGenerator chunkGenerator = new ChunkGenerator();

  private static volatile int gX = 0, gZ = 0;

  public static final void generateNewWorld() {
    chunkGenerator.end();

    gX = 0; gZ = 0;
    regenChunks();
    chunkGenerator = new ChunkGenerator();

    chunkGenerator.start();
  }

  public static int getCentreChunkX() {
    return gX;
  }

  public static int getCentreChunkZ() {
    return gZ;
  }

  public static Chunk[][] getChunks() {
    return chunks;
  }

  public static void regenChunks() {
    chunks = new Chunk[Core.RENDER_RADIUS*2+1][Core.RENDER_RADIUS*2+1];
    // for (int y = 0; y < chunks.length; y++) {
    //   for (int x = 0; x < chunks[y].length; x++) {
    //     chunks[y][x] = new Chunk(gX-chunks[y].length/2+x, gZ-chunks.length/2+y);
    //   }
    // }
  }

  public static void shiftXIncr() {
    gX++;
    for (int y = 0; y < chunks.length; y++) {
      for (int x = 1; x < chunks[y].length; x++) {
        chunks[y][x-1] = chunks[y][x];
      }
      // chunks[y][chunks[y].length-1] = new Chunk(gX+chunks[y].length/2, gZ+y-chunks.length/2);
      chunks[y][chunks[y].length-1] = null;
    }
  }

  public static void shiftXDecr() { 
    gX--;
    for (int y = 0; y < chunks.length; y++) {
      for (int x = chunks[y].length-2; x >= 0; x--) {
        chunks[y][x+1] = chunks[y][x];
      }
      // chunks[y][0] = new Chunk(gX-chunks[y].length/2, gZ+y-chunks.length/2);
      chunks[y][0] = null;
    }
  }

  public static void shiftZIncr() {
    gZ++;
    for (int y = 1; y < chunks.length; y++) {
      chunks[y-1] = chunks[y];
    }
    chunks[chunks.length-1] = new Chunk[chunks[0].length];
    // for (int x = 0; x < chunks[0].length; x++) {
    //   chunks[chunks.length-1][x] = new Chunk(gX+x-chunks[0].length/2, gZ+chunks.length-1-chunks.length/2);
    // }
  }

  public static void shiftZDecr() {
    gZ--;
    for (int y = chunks.length-2; y >= 0; y--) {
      chunks[y+1] = chunks[y];
    }
    chunks[0] = new Chunk[chunks[0].length];
    // for (int x = 0; x < chunks[0].length; x++) {
    //   chunks[0][x] = new Chunk(gX+x-chunks[0].length/2, gZ-chunks.length/2);
    // }
  }

  public static void draw(Graphics g, int size, double mapRatio, double lX, double lZ) {
    for (int y = 0; y < chunks.length; y++) {
      for (int x = 0; x < chunks[y].length; x++) {
        if (chunks[y][x] == null) continue;
        g.drawImage(
          chunks[y][x].getImg(), 
          (int)((size*mapRatio)+(Core.WINDOW.screenWidth()-(size*mapRatio))/2.0-lX+(x-chunks[y].length/2)*Core.CHUNK_SIZE), 
          (int)(Core.WINDOW.screenHeight()/2.0-lZ+(y-chunks.length/2)*Core.CHUNK_SIZE), 
          null
        );
      }
    }
  }
}
