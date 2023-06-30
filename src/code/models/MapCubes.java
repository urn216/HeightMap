package code.models;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public class MapCubes extends Model {

  public MapCubes(float[] map, int w, int h) {
    super(generateMesh(map, w, h));
  }

  private static Object[][] generateMesh(float[] map, int w, int h) {
    Object[][] res = new Object[3][];

    Vector3[] verts = generateVerts(map, w, h);
    res[0] = verts;
    res[1] = generateFaces(verts, w, h);
    res[2] = new Vector2[]{};

    return res;
  }

  private static Vector3[] generateVerts(float[] heights, int w, int h) {
    Vector3[] res = new Vector3[heights.length*4];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        double x = i-(w/2);
        double y = (int)(heights[i+j*w]*100);
        double z = j-(h/2);
        res[i*2   + w*2*(j*2)  ] = new Vector3(x-0.5, y, z-0.5);
        res[i*2+1 + w*2*(j*2)  ] = new Vector3(x+0.5, y, z-0.5);
        res[i*2   + w*2*(j*2+1)] = new Vector3(x-0.5, y, z+0.5);
        res[i*2+1 + w*2*(j*2+1)] = new Vector3(x+0.5, y, z+0.5);
      }
    }
    return res;
  }

  private static Tri[] generateFaces(Vector3[] verts, int w, int h) {
    w*=2; h*=2;
    Tri[] res = new Tri[2*(w-1)*(h-1)];
    for (int z = 0; z < h-1; z++) {
      for (int x = 0; x < w-1; x++) {
        int a = (x) + (z) * w, b = (x+1) + (z) * w, c = (x) + (z+1) * w, d = (x+1) + (z+1) * w;
        res[2*(x + z * (w-1))]   = new Tri(new Vector3[]{verts[a], verts[d], verts[b]}, new int[]{a+1, d+1, b+1});
        res[2*(x + z * (w-1))+1] = new Tri(new Vector3[]{verts[a], verts[c], verts[d]}, new int[]{a+1, c+1, d+1});
      }
    }
    return res;
  }
}
