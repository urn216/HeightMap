package code.core;

public class Chunk extends Map {

  private static final int CHUNK_SIZE = 16;

  protected Chunk(long seed) {
    super(CHUNK_SIZE, CHUNK_SIZE, seed);
  }
  
}
