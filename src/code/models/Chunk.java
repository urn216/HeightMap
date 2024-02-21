package code.models;

import code.core.Core;
import code.core.MapGenerator;

public class Chunk extends Map3D {

  private static final int CHUNK_SIZE = 16;

  public Chunk(int x, int z) {
    super(MapGenerator.generateHeights(x*CHUNK_SIZE, z*CHUNK_SIZE, CHUNK_SIZE+1, CHUNK_SIZE+1, Core.MAP_OCTAVES, false), CHUNK_SIZE+1, CHUNK_SIZE+1);
  }
  
}
