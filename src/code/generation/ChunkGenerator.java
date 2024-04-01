package code.generation;

import code.core.Core;
import code.core.World;
import mki.math.vector.Vector3;
import mki.world.RigidBody;

public class ChunkGenerator extends Thread {

  private volatile boolean active = true;

  private volatile boolean resetGeneration = false;

  public ChunkGenerator() {
    super("Chunk-Generator");
  }
  
  /**
   * Active method for this {@code ChunkGenerator}.
   * Generates chunks for the current {@code World} in 
   * an expanding ring around the centre of the viewport 
   * up to the desired render distance.
   */
  @Override
  public void run() {
    int r = 0; // 'radius' of square around middle of view to draw
    int s = 1; // side-length of r-'radius' square around middle of view
    int i = 0; // horizontal step through current drawing square
    int j = 0; // vertical step through current drawing square

    while (active) {
      Chunk[][] chunks = World.getChunks();

      // reset from middle if we need to
      if (resetGeneration) {r=0;s=1;i=0;j=0;this.resetGeneration=false;}

      // x and y from centre, rather than top-left
      int x = Core.RENDER_RADIUS - r + i;
      int y = Core.RENDER_RADIUS - r + j;
      int detail = (int)Math.sqrt(((x-Core.RENDER_RADIUS)*(x-Core.RENDER_RADIUS) + (y-Core.RENDER_RADIUS)*(y-Core.RENDER_RADIUS))/3);

      // only bother to generate if we need to
      if (chunks[y][x] == null) {
        int gX = World.getCentreChunkX();
        int gZ = World.getCentreChunkZ();

        Chunk c = new Chunk(World.getTerrainGenerator(), gX-chunks[y].length/2+x, gZ-chunks.length/2+y, detail);
        chunks[y][x] = c;
        RigidBody b = c.getBody();
        b.setPosition(new Vector3((x-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE, 0, (y-Core.RENDER_RADIUS+0.5)*Core.CHUNK_SIZE));
      }
      else chunks[y][x].setVertexDensity(detail);

      i++;
      if (i<s) continue; // pseudo for-loop
      i = 0;

      j++;
      if (j<s) continue; // pseudo for-loop
      j = 0;
      r = (r+1)%(Core.RENDER_RADIUS+1);
      s = 2*r+1;
    }
  }

  public void end() {
    this.active = false;
  }

  public void resetGeneration() {
    this.resetGeneration = true;
  }
}
