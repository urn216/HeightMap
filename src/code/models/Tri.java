package code.models;

import code.math.Vector2;
import code.math.Vector3;

public class Tri {

  private final Vector3[] verts;
  private final Vector3[] edges;
  private final Vector2[] vertUVs;

  private final int[] vertexIndeces;
  private final int[] vertexTextureIndeces;
  
  private Vector3 normal;

  public Tri(Vector3[] verts, int[] vertexIndeces) {
    this(verts, new Vector2[3], vertexIndeces, new int[3]);
  }

  public Tri(Vector3[] verts, Vector2[] vertUVs, int[] vertexIndeces, int[] vertexTextureIndeces) {
    if (verts.length != 3 || vertexIndeces.length != 3 || vertUVs.length != 3 || vertexTextureIndeces.length != 3) {
      throw new RuntimeException("Triangles have 3 points!");
    }

    this.verts = verts;
    this.vertexIndeces = vertexIndeces;

    this.edges = new Vector3[3];
    edges[0] = verts[1].subtract(verts[0]);
    edges[1] = verts[2].subtract(verts[0]);
    edges[2] = edges[0].cross(edges[1]).unitize();

    normal = edges[0].cross(edges[1]);

    this.vertUVs = vertUVs;
    this.vertexTextureIndeces = vertexTextureIndeces;
  }

  /**
   * @return the verts
   */
  public Vector3[] getVerts() {
  	return verts;
  }

  /**
   * @return the vertUVs
   */
  public Vector2[] getVertUVs() {
  	return vertUVs;
  }

  /**
   * @return the edges
   */
  public Vector3[] getEdges() {
  	return edges;
  }

  /**
   * @return the normal
   */
  public Vector3 getNorm() {
  	return normal;
  }

  public Vector2 getUVCoords(double u, double v) {
    return vertUVs[0] == null ? new Vector2() : vertUVs[1].scale(u).add(vertUVs[2].scale(v)).add(vertUVs[0].scale(1-u-v));
  }

  public String toString() {
    if (vertUVs[0] == null) {
      return vertexIndeces[0]+" "+vertexIndeces[1]+" "+vertexIndeces[2];
    }
    return vertexIndeces[0]+"/"+vertexTextureIndeces[0]+" "+vertexIndeces[1]+"/"+vertexTextureIndeces[1]+" "+vertexIndeces[2]+"/"+vertexTextureIndeces[2];
  }
}
