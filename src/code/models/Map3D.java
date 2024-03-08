package code.models;

import code.core.Core;
import mki.math.tri.Tri3D;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.math.vector.Vector3I;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Map3D extends RigidBody {

  public Map3D(float[] map, int w, int h, int[] img) {
    super(new Vector3(w/2.0, 0, h/2.0), generateMesh(map, w, h));
    // super(new Vector3(), Model.generateMesh("chunk.obj"));

    this.model.setMat(new Material(new Vector3I(150), 0, new Vector3(), img, new int[]{-8355585}));
    // this.model.setMat(new Material(new Vector3I(150), 0, new Vector3(), "env/terrain_map.png"));
    this.model.calculateRadius();
  }

  protected static Model generateMesh(float[] map, int w, int h) {
    Vector3[] verts = generateVerts(map, w, h);
    Vector2[] vertUVs = generateUVs(w, h);
    Tri3D[] faces = generateFaces(verts, vertUVs, w, h);

    return new Model(verts, faces, vertUVs);
  }

  private static Vector3[] generateVerts(float[] heights, int w, int h) {
    Vector3[] res = new Vector3[heights.length+4];

    double oceanHeight = -0.0078125*Core.MAP_SCALE;

    for (int i = 0; i < res.length-4; i++) {
      res[i] = new Vector3((i%w)-(w/2.0), heights[i]*100*Core.MAP_SCALE, (i/w)-(h/2.0));
    }

    res[res.length-4] = new Vector3(-w/2.0    , oceanHeight,  h/2.0 - 1);
    res[res.length-3] = new Vector3( w/2.0 - 1, oceanHeight,  h/2.0 - 1);
    res[res.length-2] = new Vector3(-w/2.0    , oceanHeight, -h/2.0    );
    res[res.length-1] = new Vector3( w/2.0 - 1, oceanHeight, -h/2.0    );

    return res;
  }

  private static Vector2[] generateUVs(int w, int h) {
    Vector2[] res = new Vector2[w*h + 4];

    for (int i = 0; i < res.length-4; i++) {
      res[i] = new Vector2((i%w+0.5)/w, (i/w+0.5)/h); //TODO band-aid fix. figure out why these should be 1-y
    }

    res[res.length-4] = new Vector2(      0.5/w, 1.0 - 0.5/h); //Same with these. top two swapped with bottom two
    res[res.length-3] = new Vector2(1.0 - 0.5/w, 1.0 - 0.5/h);
    res[res.length-2] = new Vector2(      0.5/w,       0.5/h);
    res[res.length-1] = new Vector2(1.0 - 0.5/w,       0.5/h);

    return res;
  }

  private static Tri3D[] generateFaces(Vector3[] verts, Vector2[] vertUVs, int w, int h) {
    Tri3D[] res = new Tri3D[2*(w-1)*(h-1) + 2];

    for (int z = 0; z < h-1; z++) {
      for (int x = 0; x < w-1; x++) {
        int a = (x) + (z) * w, b = (x+1) + (z) * w, c = (x) + (z+1) * w, d = (x+1) + (z+1) * w;
        res[2*(x + z * (w-1))]   = new Tri3D(
          new Vector3[]{verts  [a], verts  [d], verts  [b]}, 
          new Vector2[]{vertUVs[a], vertUVs[d], vertUVs[b]},
          new int[]{a+1, d+1, b+1},
          new int[]{a+1, d+1, b+1}
        );
        res[2*(x + z * (w-1))+1] = new Tri3D(
          new Vector3[]{verts  [a], verts  [c], verts  [d]}, 
          new Vector2[]{vertUVs[a], vertUVs[c], vertUVs[d]}, 
          new int[]{a+1, c+1, d+1},
          new int[]{a+1, c+1, d+1}
        );
      }
    }

    int a = verts.length-2, b = verts.length-1, c = verts.length-4, d = verts.length-3;

    res[res.length-2] = new Tri3D(
      new Vector3[]{verts  [a], verts  [d], verts  [b]}, 
      new Vector2[]{vertUVs[a], vertUVs[d], vertUVs[b]}, 
      new int[]{a+1, d+1, b+1},
      new int[]{a+1, d+1, b+1}
    );
    res[res.length-1] = new Tri3D(
      new Vector3[]{verts  [a], verts  [c], verts  [d]}, 
      new Vector2[]{vertUVs[a], vertUVs[c], vertUVs[d]}, 
      new int[]{a+1, c+1, d+1},
      new int[]{a+1, c+1, d+1}
    );

    return res;
  }
}
