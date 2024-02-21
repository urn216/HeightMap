package code.models;

import code.core.Core;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public class MapCubes extends Model {

  public MapCubes(float[] map, int w, int h) {
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
    Vector3[] res = new Vector3[heights.length*4];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        double x = i-(w/2.0);
        double y = (Math.max(Math.floor(heights[i+j*w]*100*Core.MAP_SCALE), Math.floor(-0.5*Core.MAP_SCALE)+0.875));
        double z = j-(h/2.0);
        res[i*2   + w*2*(j*2)  ] = new Vector3(x-0.5, y, z-0.5);
        res[i*2+1 + w*2*(j*2)  ] = new Vector3(x+0.5, y, z-0.5);
        res[i*2   + w*2*(j*2+1)] = new Vector3(x-0.5, y, z+0.5);
        res[i*2+1 + w*2*(j*2+1)] = new Vector3(x+0.5, y, z+0.5);
      }
    }
    return res;
  }

  private static Vector2[] generateUVs(int w, int h) {
    Vector2[] res = new Vector2[w*h];

    for (int i = 0; i < res.length; i++) {
      res[i] = new Vector2((i%w+0.5)/w, 1-(i/w+0.5)/h);
    }

    return res;
  }

  private static Tri[] generateFaces(Vector3[] verts, Vector2[] vertUVs, int w, int h) {
    int numTops = (w  )*(h  );
    int numEWSs = (w-1)*(h  );
    int numNSSs = (w  )*(h-1);
    Tri[] res = new Tri[2*(numTops + numEWSs + numNSSs)];

    int w2 = 2 * w;

    int i = 0;
    for (int z = 0; z < 2*h-1; z++) {
      for (int x = 0; x < w2-1; x+=(z%2==0?1:2)) {
        int a = (x) + (z) * w2, b = (x+1) + (z) * w2, c = (x) + (z+1) * w2, d = (x+1) + (z+1) * w2;
        int uv = (int)(x/(2.0*w-1)*w) + (int)(z/(2.0*h-1)*h)*w;
        res[i++] = new Tri(
          new Vector3[]{verts[a], verts[d], verts[b]},
          new Vector2[]{vertUVs[uv], vertUVs[uv], vertUVs[uv]},
          new int[]{a+1, d+1, b+1},
          new int[]{uv+1, uv+1, uv+1}
        );
        res[i++] = new Tri(
          new Vector3[]{verts[a], verts[c], verts[d]},
          new Vector2[]{vertUVs[uv], vertUVs[uv], vertUVs[uv]},
          new int[]{a+1, c+1, d+1},
          new int[]{uv+1, uv+1, uv+1}
        );
      }
    }
    return res;
  }
}
