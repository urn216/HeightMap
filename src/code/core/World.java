package code.core;

import java.awt.Graphics;
import java.util.ArrayDeque;

import code.generation.Chunk;
import code.generation.ChunkGenerator;
import code.generation.TerrainGenerator;
import code.models.Ring;
import mki.math.vector.Vector3;
import mki.world.RigidBody;

public abstract class World {

  private static volatile Chunk[][] chunks = {};

  private static final ArrayDeque<Chunk> insertionQueue = new ArrayDeque<>();
  
  private static TerrainGenerator terrainGenerator = new TerrainGenerator();

  private static Ring ring = null;
  
  private static volatile ChunkGenerator chunkGenerator = new ChunkGenerator();
  
  private static volatile int gX = 0, gZ = 0;

  private static int spawnX = 0, spawnZ = 0;

  private static double spawnY = 0;

  public static final void generateNewWorld() {
    generateNewWorld(System.currentTimeMillis());
  }

  public static final void generateNewWorld(long seed) {
    chunkGenerator.end();

    terrainGenerator = new TerrainGenerator(seed);

    findValidSpawnLocation();
    gX = spawnX; gZ = spawnZ;

    chunkGenerator = new ChunkGenerator();
    regenChunks();

    if (Core.GLOBAL_SETTINGS.getBoolSetting("g_ringworld")) addRing();

    chunkGenerator.start();
  }

  public static final void endWorld() {
    chunkGenerator.end();

    chunks = new Chunk[Core.RENDER_RADIUS*2+1][Core.RENDER_RADIUS*2+1];
    RigidBody.clearBodies();

    insertionQueue.clear();
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

  private static void findValidSpawnLocation() {
    double pos = (-(Core.CHUNK_SIZE+1)/2.0)/Core.MAP_RANGE_SCALE;
    
    spawnX = 0;
    spawnY = World.getTerrainGenerator().generateHeights(pos, pos, 1, 1, Core.MAP_OCTAVES, Core.MAP_RANGE_SCALE, false)[0]*Core.MAP_HEIGHT_SCALE;
    spawnZ = 0;
  }

  public static double returnToSpawn() {
    moveToChunk(spawnX, spawnZ);
    return spawnY;
  }

  public static void regenChunks() {
    chunks = new Chunk[Core.RENDER_RADIUS*2+1][Core.RENDER_RADIUS*2+1];
    RigidBody.clearBodies();

    chunkGenerator.resetGeneration();
    chunkGenerator.ackReset();

    insertionQueue.clear();

    if (ring != null) {
      RigidBody.addBody(ring);
      ring.setSlide(-gX*Core.CHUNK_SIZE);
      ring.setSpin ( gZ*Core.CHUNK_SIZE);
    }
    else if (Core.GLOBAL_SETTINGS.getBoolSetting("g_ringworld")) addRing();
    
    // for (int y = 0; y < chunks.length; y++) {
    //   for (int x = 0; x < chunks[y].length; x++) {
    //     chunks[y][x] = new Chunk(gX-chunks[y].length/2+x, gZ-chunks.length/2+y);
    //   }
    // }
  }

  public static boolean hasRing() {
    return ring != null;
  }

  public static void addRing() {
    removeRing();
    chunkGenerator.generateRing();
  }

  public static void removeRing() {
    if (ring == null) return;
    RigidBody.removeBody(ring);
    World.ring = null;
  }

  public static void setRing(Ring ring) {
    removeRing();
    World.ring = ring;
  }

  public static void setRing(boolean b) {
    if (b) addRing();
    else removeRing();
  }

  public static void toggleRing() {
    if (ring != null) removeRing();
    else addRing();
  }

  private static void removeChunk(Chunk c) {
    RigidBody.removeBody(c.getBody());
  }

  public static void moveToChunk(int gX, int gZ) {
    World.gX = gX;
    World.gZ = gZ;
    regenChunks();
  }

  public static void shiftXIncr() {
    gX++;
    for (int y = 0; y < chunks.length; y++) {
      int x = 0;

      if (chunks[y][x] != null) removeChunk(chunks[y][x]);

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

    if (ring != null) ring.setSlide(-gX*Core.CHUNK_SIZE);
    // Chunk c = chunks[chunks.length/2][chunks[0].length/2];
    // System.out.println("("+gX+","+gZ+") == ("+c.getX()+","+c.getY()+") => "+c.getBody().getPosition());
  }

  public static void shiftXDecr() { 
    gX--;
    for (int y = 0; y < chunks.length; y++) {
      int x = chunks[y].length-1;

      if (chunks[y][x] != null) removeChunk(chunks[y][x]);

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

    if (ring != null) ring.setSlide(-gX*Core.CHUNK_SIZE);
    // Chunk c = chunks[chunks.length/2][chunks[0].length/2];
    // System.out.println("("+gX+","+gZ+") == ("+c.getX()+","+c.getY()+") => "+c.getBody().getPosition());
  }

  public static void shiftZIncr() {
    gZ++;

    int y = 0;
    
    for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
      removeChunk(chunks[y][x]);
    }

    for (++y; y < chunks.length; y++) {
      for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
        chunks[y][x].getBody().setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS-0.5)*Core.CHUNK_SIZE));
      }
      chunks[y-1] = chunks[y];
    }
    
    chunks[y-1] = new Chunk[chunks[y-1].length];
    chunkGenerator.resetGeneration();

    if (ring != null) ring.setSpin(gZ*Core.CHUNK_SIZE);
    // Chunk c = chunks[chunks.length/2][chunks[0].length/2];
    // System.out.println("("+gX+","+gZ+") == ("+c.getX()+","+c.getY()+") => "+c.getBody().getPosition());
  }

  public static void shiftZDecr() {
    gZ--;

    int y = chunks.length-1;
    
    for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
      removeChunk(chunks[y][x]);
    }

    for (--y; y >= 0; y--) {
      for (int x = 0; x < chunks[y].length; x++) if (chunks[y][x] != null) {
        chunks[y][x].getBody().setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+1.5)*Core.CHUNK_SIZE));
      }
      chunks[y+1] = chunks[y];
    }

    chunks[y+1] = new Chunk[chunks[y+1].length];
    chunkGenerator.resetGeneration();

    if (ring != null) ring.setSpin(gZ*Core.CHUNK_SIZE);
    // Chunk c = chunks[chunks.length/2][chunks[0].length/2];
    // System.out.println("("+gX+","+gZ+") == ("+c.getX()+","+c.getY()+") => "+c.getBody().getPosition());
  }

  public static void insertChunk(Chunk c) {
    insertionQueue.add(c);
  }

  public static void handleChunkInsertion() {
    while (!insertionQueue.isEmpty()) {
      Chunk c = insertionQueue.poll();
      int y = c.getY()-gZ+chunks   .length/2;
      if (y < 0 || y >= chunks   .length) {removeChunk(c); continue;}
      int x = c.getX()-gX+chunks[y].length/2;
      if (x < 0 || x >= chunks[y].length) {removeChunk(c); continue;}

      if (chunks[y][x] != null) removeChunk(chunks[y][x]);
      chunks[y][x] = c;
      RigidBody b = c.getBody();
      b.setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE));
    }
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
