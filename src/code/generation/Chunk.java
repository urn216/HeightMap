package code.generation;

import java.awt.image.BufferedImage;

import code.core.Core;
import code.models.Map3D;

public class Chunk {

  private final float[] heightMap;

  private final BufferedImage img;

  private final Map3D model;

  public Chunk(int x, int z) {
    this.heightMap = MapGenerator.generateHeights(x/Core.MAP_SCALE*Core.CHUNK_SIZE, z/Core.MAP_SCALE*Core.CHUNK_SIZE, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1, Core.MAP_OCTAVES, false);
    this.img = ImageProc.mapToImage(this.heightMap, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1);
    this.model = new Map3D(this.heightMap, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1);
  }

  public float[] getHeightMap() {
    return heightMap;
  }

  public BufferedImage getImg() {
    return img;
  }

  public Map3D getModel() {
    return model;
  }
}
