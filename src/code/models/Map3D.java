package code.models;

import code.math.Vector2;
import code.math.Vector3;

public class Map3D extends Model {

  public Map3D(int[] map, int w, int h) {
    super(generateMesh(map, w, h));
  }

  private static Object[][] generateMesh(int[] map, int w, int h) {
    Object[][] res = new Object[3][];

    Vector3[] verts = generateVerts(map, w);
    res[0] = verts;
    res[1] = generateFaces(verts, w, h);
    res[2] = new Vector2[]{};

    return res;
  }

  private static Vector3[] generateVerts(int[] heights, int w) {
    Vector3[] res = new Vector3[heights.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Vector3(i % w, (heights[i] & 255)/5.0, i / w);
    }
    return res;
  }

  private static Tri[] generateFaces(Vector3[] verts, int w, int h) {
    Tri[] res = new Tri[2*(w-1)*(h-1)];
    for (int i = 0; i < w-1; i++) {
      for (int j = 0; j < h-1; j++) {
        int a = (i) + (j) * w, b = (i+1) + (j) * w, c = (i) + (j+1) * w, d = (i+1) + (j+1) * w;
        res[2*(i + j * (w-1))]   = new Tri(new Vector3[]{verts[a], verts[d], verts[b]}, new int[]{a+1, d+1, b+1});
        res[2*(i + j * (w-1))+1] = new Tri(new Vector3[]{verts[a], verts[c], verts[d]}, new int[]{a+1, c+1, d+1});
      }
    }
    return res;
  }
}
