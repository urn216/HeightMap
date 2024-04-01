package code.core;

import java.awt.Graphics;

import code.generation.Chunk;
import code.generation.ChunkGenerator;
import code.generation.TerrainGenerator;
import mki.math.vector.Vector3;
import mki.world.RigidBody;

public abstract class World {

  private static volatile Chunk[][] chunks;
  
  private static TerrainGenerator terrainGenerator = new TerrainGenerator();
  
  private static volatile ChunkGenerator chunkGenerator = new ChunkGenerator();
  
  private static volatile int gX = 0, gZ = 0;

  public static final void generateNewWorld() {
    generateNewWorld(System.currentTimeMillis());
  }

  public static final void generateNewWorld(long seed) {
    chunkGenerator.end();

    terrainGenerator = new TerrainGenerator(seed);
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

  public static TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }

  public static void regenChunks() {
    chunks = new Chunk[Core.RENDER_RADIUS*2+1][Core.RENDER_RADIUS*2+1];
    RigidBody.clearBodies();

    chunkGenerator.resetGeneration();
    
    // for (int y = 0; y < chunks.length; y++) {
    //   for (int x = 0; x < chunks[y].length; x++) {
    //     chunks[y][x] = new Chunk(gX-chunks[y].length/2+x, gZ-chunks.length/2+y);
    //   }
    // }
  }

  public static void shiftXIncr() {
    gX++;
    for (int y = 0; y < chunks.length; y++) {
      int x = 0;

      if (chunks[y][x] != null) RigidBody.removeBody(chunks[y][x].getBody());

      for (++x; x < chunks[y].length; x++) {
        if (chunks[y][x] != null) chunks[y][x].getBody().setPosition(new Vector3(
          (x-Core.RENDER_RADIUS-0.5)*Core.CHUNK_SIZE, 
          0, 
          (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE
        ));
        chunks[y][x-1] = chunks[y][x];
      }

      chunks[y][x-1] = null;
    }
    chunkGenerator.resetGeneration();
  }

  public static void shiftXDecr() { 
    gX--;
    for (int y = 0; y < chunks.length; y++) {
      int x = chunks[y].length-1;

      if (chunks[y][x] != null) RigidBody.removeBody(chunks[y][x].getBody());

      for (--x; x >= 0; x--) {
        if (chunks[y][x] != null) chunks[y][x].getBody().setPosition(new Vector3(
          (x-Core.RENDER_RADIUS+1.5)*Core.CHUNK_SIZE, 
          0, 
          (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE
        ));
        chunks[y][x+1] = chunks[y][x];
      }
      
      chunks[y][x+1] = null;
    }
    chunkGenerator.resetGeneration();
  }

  public static void shiftZIncr() {
    gZ++;

    int y = 0;
    
    for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
      RigidBody.removeBody(chunks[y][x].getBody());
    }

    for (++y; y < chunks.length; y++) {
      for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
        chunks[y][x].getBody().setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS-0.5)*Core.CHUNK_SIZE));
      }
      chunks[y-1] = chunks[y];
    }
    
    chunks[y-1] = new Chunk[chunks[y-1].length];
    chunkGenerator.resetGeneration();
  }

  public static void shiftZDecr() {
    gZ--;

    int y = chunks.length-1;
    
    for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
      RigidBody.removeBody(chunks[y][x].getBody());
    }

    for (--y; y >= 0; y--) {
      for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
        chunks[y][x].getBody().setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+1.5)*Core.CHUNK_SIZE));
      }
      chunks[y+1] = chunks[y];
    }

    chunks[y+1] = new Chunk[chunks[y+1].length];
    chunkGenerator.resetGeneration();
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
