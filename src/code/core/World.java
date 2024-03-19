package code.core;

import java.awt.Graphics;
import java.util.Arrays;

import code.generation.Chunk;
import code.generation.ChunkGenerator;
import code.generation.TerrainGenerator;
import mki.math.tri.Tri3D;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.world.Model;
import mki.world.RigidBody;

public abstract class World {

  private static final RigidBody BLANK_BODY = new RigidBody(new Vector3(), new Model(new Vector3[0], new Tri3D[0], new Vector2[0])) {};

  private static volatile Chunk[][] chunks;
  
  private static volatile RigidBody[] bodies;
  
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

  public static RigidBody[] getChunkBodies() {
    return bodies;
  }

  public static TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }

  public static void regenChunks() {
    chunks = new Chunk[Core.RENDER_RADIUS*2+1][Core.RENDER_RADIUS*2+1];
    bodies = new RigidBody[chunks.length*chunks.length];

    Arrays.fill(bodies, BLANK_BODY);

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
      for (int x = 1; x < chunks[y].length; x++) {
        chunks[y][x-1] = chunks[y][x];
        RigidBody b = bodies[x+y*chunks.length];
        b.setPosition(new Vector3((x-Core.RENDER_RADIUS-0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE));
        bodies[x-1+y*chunks.length] = b;
      }
      // chunks[y][chunks[y].length-1] = new Chunk(gX+chunks[y].length/2, gZ+y-chunks.length/2);
      chunks[y][chunks[y].length-1] = null;
      bodies[chunks[y].length-1+y*chunks.length] = BLANK_BODY;
    }
    chunkGenerator.resetGeneration();
  }

  public static void shiftXDecr() { 
    gX--;
    for (int y = 0; y < chunks.length; y++) {
      for (int x = chunks[y].length-2; x >= 0; x--) {
        chunks[y][x+1] = chunks[y][x];
        RigidBody b = bodies[x+y*chunks.length];
        b.setPosition(new Vector3((x-Core.RENDER_RADIUS+1.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE));
        bodies[x+1+y*chunks.length] = b;
      }
      // chunks[y][0] = new Chunk(gX-chunks[y].length/2, gZ+y-chunks.length/2);
      chunks[y][0] = null;
      bodies[y*chunks.length] = BLANK_BODY;
    }
    chunkGenerator.resetGeneration();
  }

  public static void shiftZIncr() {
    gZ++;
    for (int y = 1; y < chunks.length; y++) {
      for (int x = 0; x < chunks[y].length; x++) {
        RigidBody b = bodies[x+y*chunks.length];
        b.setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS-0.5)*Core.CHUNK_SIZE));
        bodies[x+(y-1)*chunks.length] = b;
      }
      chunks[y-1] = chunks[y];
    }
    chunks[chunks.length-1] = new Chunk[chunks[0].length];
    for (int x = 0; x < chunks[0].length; x++) bodies[x+(chunks.length-1)*chunks.length] = BLANK_BODY;
    // for (int x = 0; x < chunks[0].length; x++) {
    //   chunks[chunks.length-1][x] = new Chunk(gX+x-chunks[0].length/2, gZ+chunks.length-1-chunks.length/2);
    // }
    chunkGenerator.resetGeneration();
  }

  public static void shiftZDecr() {
    gZ--;
    for (int y = chunks.length-2; y >= 0; y--) {
      for (int x = 0; x < chunks[y].length; x++) {
        RigidBody b = bodies[x+y*chunks.length];
        b.setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+1.5)*Core.CHUNK_SIZE));
        bodies[x+(y+1)*chunks.length] = b;
      }
      chunks[y+1] = chunks[y];
    }
    chunks[0] = new Chunk[chunks[0].length];
    for (int x = 0; x < chunks[0].length; x++) bodies[x] = BLANK_BODY;
    // for (int x = 0; x < chunks[0].length; x++) {
    //   chunks[0][x] = new Chunk(gX+x-chunks[0].length/2, gZ-chunks.length/2);
    // }
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
