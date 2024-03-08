package code.generation;

import java.awt.image.BufferedImage;

import code.core.Core;
import code.models.Map3D;
import mki.world.RigidBody;

public class Chunk {

  private final float[] heightMap;

  private final BufferedImage img;

  private final RigidBody body;

  public Chunk(TerrainGenerator tG, int x, int z) {
    this.heightMap = tG.generateHeights(x/Core.MAP_SCALE*Core.CHUNK_SIZE, z/Core.MAP_SCALE*Core.CHUNK_SIZE, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1, Core.MAP_OCTAVES, false);
    this.img = ImageProc.mapToImage(this.heightMap, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1);
    this.body = new Map3D(this.heightMap, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1, this.img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
  }

  public float[] getHeightMap() {
    return heightMap;
  }

  public BufferedImage getImg() {
    return img;
  }

  public RigidBody getBody() {
    return body;
  }
}
