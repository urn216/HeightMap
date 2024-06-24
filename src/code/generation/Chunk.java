package code.generation;

import java.awt.image.BufferedImage;

import code.core.Core;
import code.models.Map3D;
import mki.world.RigidBody;

public class Chunk {

  // private final float[] heightMap;

  private final BufferedImage img;

  private final RigidBody body;

  private final int x, y;

  public Chunk(TerrainGenerator tG, int x, int z, int downScaleFactor) {
    float[] heightMap = tG.generateHeights(x/Core.MAP_RANGE_SCALE*Core.CHUNK_SIZE, z/Core.MAP_RANGE_SCALE*Core.CHUNK_SIZE, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1, Core.MAP_OCTAVES, Core.MAP_RANGE_SCALE, false);
    this.img = ImageProc.mapToImage(heightMap, Core.CHUNK_SIZE+1, Core.CHUNK_SIZE+1);
    this.body = new Map3D(heightMap, (short)(Core.CHUNK_SIZE+1), (short)(Core.CHUNK_SIZE+1), downScaleFactor, this.img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    this.x = x;
    this.y = z;
  }

  // public float[] getHeightMap() {
  //   return heightMap;
  // }

  public BufferedImage getImg() {
    return img;
  }

  public RigidBody getBody() {
    return body;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setVertexDensity(int downScaleFactor) {
    ((Map3D)body).setVertexDensity(downScaleFactor);
  }
}
