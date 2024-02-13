package code.models;

import code.core.Core;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public class Map3D extends Model {

  public Map3D(float[] map, int w, int h) {
    super(generateMesh(map, w, h));
  }

  private static Object[][] generateMesh(float[] map, int w, int h) {
    Object[][] res = new Object[3][];

    Vector3[] verts = generateVerts(map, w, h);
    Vector2[] vertUVs = generateUVs(w, h);
    res[0] = verts;
    res[1] = generateFaces(verts, vertUVs, w, h);
    res[2] = vertUVs;

    return res;
  }

  private static Vector3[] generateVerts(float[] heights, int w, int h) {
    Vector3[] res = new Vector3[heights.length+4];

    double oceanHeight = 0.5*Core.MAP_SCALE;

    for (int i = 0; i < res.length-4; i++) {
      res[i] = new Vector3((i%w)-(w/2.0), heights[i]*100*Core.MAP_SCALE, h-1-(i/w)-(h/2.0));
    }

    res[res.length-4] = new Vector3(-w/2.0    , oceanHeight, -h/2.0    );
    res[res.length-3] = new Vector3( w/2.0 - 1, oceanHeight, -h/2.0    );
    res[res.length-2] = new Vector3(-w/2.0    , oceanHeight,  h/2.0 - 1);
    res[res.length-1] = new Vector3( w/2.0 - 1, oceanHeight,  h/2.0 - 1);

    return res;
  }

  private static Vector2[] generateUVs(int w, int h) {
    Vector2[] res = new Vector2[w*h + 4];

    for (int i = 0; i < res.length-1; i++) {
      res[i] = new Vector2((i%w)/(1.0*w), (i/w)/(1.0*h));
    }

    res[res.length-4] = new Vector2(0.0, 1.0);
    res[res.length-3] = new Vector2(1.0, 1.0);
    res[res.length-2] = new Vector2(0.0, 0.0);
    res[res.length-1] = new Vector2(1.0, 0.0);

    return res;
  }

  private static Tri[] generateFaces(Vector3[] verts, Vector2[] vertUVs, int w, int h) {
    Tri[] res = new Tri[2*(w-1)*(h-1) + 2];

    for (int z = 0; z < h-1; z++) {
      for (int x = 0; x < w-1; x++) {
        int a = (x) + (z+1) * w, b = (x+1) + (z+1) * w, c = (x) + (z) * w, d = (x+1) + (z) * w;
        res[2*(x + z * (w-1))]   = new Tri(
          new Vector3[]{verts[a], verts[d], verts[b]}, 
          new Vector2[]{vertUVs[a], vertUVs[d], vertUVs[b]},
          new int[]{a+1, d+1, b+1},
          new int[]{a+1, d+1, b+1}
        );
        res[2*(x + z * (w-1))+1] = new Tri(
          new Vector3[]{verts[a], verts[c], verts[d]}, 
          new Vector2[]{vertUVs[a], vertUVs[c], vertUVs[d]}, 
          new int[]{a+1, c+1, d+1},
          new int[]{a+1, c+1, d+1}
        );
      }
    }

    res[res.length-2] = new Tri(
      new Vector3[]{verts  [verts  .length-4], verts  [verts  .length-1], verts  [verts  .length-3]}, 
      new Vector2[]{vertUVs[vertUVs.length-4], vertUVs[vertUVs.length-1], vertUVs[vertUVs.length-3]}, 
      new int[]{verts  .length-3, verts  .length  , verts  .length-2},
      new int[]{vertUVs.length-3, vertUVs.length  , vertUVs.length-2}
    );
    res[res.length-1] = new Tri(
      new Vector3[]{verts  [verts  .length-4], verts  [verts  .length-2], verts  [verts  .length-1]}, 
      new Vector2[]{vertUVs[vertUVs.length-4], vertUVs[vertUVs.length-2], vertUVs[vertUVs.length-1]}, 
      new int[]{verts  .length-3, verts  .length-1, verts  .length  },
      new int[]{vertUVs.length-3, vertUVs.length-1, vertUVs.length  }
    );

    return res;
  }
}
