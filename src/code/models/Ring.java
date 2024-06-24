package code.models;

import java.awt.image.BufferedImage;

import code.core.Core;
import code.core.World;
import code.generation.ImageProc;
import code.generation.TerrainGenerator;
import mki.io.FileIO;
import mki.math.tri.Tri3D;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;
import mki.world.Texture;

public class Ring extends RigidBody {

  private static final int nPoints = 240;
  private static final double offset = ((nPoints+1)%2)*0.5;

  private static final double defaultTextureScale = 1.0/1000;

  private static final double defaultWallHeight = 5000;
  private static final double defaultRadius = 5000000;
  private static final double defaultWidth = 318000;

  private final double radius;
  private final double width;

  public static class WithWalls extends Ring {
    public WithWalls(TerrainGenerator tG) {
      this(tG, defaultRadius, defaultWidth, defaultWallHeight, defaultTextureScale);
    }
  
    public WithWalls(TerrainGenerator tG, double textureScale) {
      this(tG, defaultRadius, defaultWidth, defaultWallHeight, textureScale);
    }
  
    public WithWalls(TerrainGenerator tG, double radius, double width, double wallHeight) {
      this(tG, radius, width, wallHeight, defaultTextureScale);
    }
  
    public WithWalls(TerrainGenerator tG, double radius, double width, double wallHeight, double textureScale) {
      super(
        tG,
        new Vector3(Core.CHUNK_SIZE/2.0+0.5, radius*Math.cos(Math.PI/nPoints)-10, Core.CHUNK_SIZE/2.0+0.5), 
        generateMeshWithWalls(radius, width, wallHeight),
        radius,
        width,
        textureScale
      );
    }
  }

  public static class Surface extends Ring {
    public Surface(TerrainGenerator tG) {
      this(tG, defaultRadius, defaultWidth, defaultTextureScale);
    }
  
    public Surface(TerrainGenerator tG, double textureScale) {
      this(tG, defaultRadius, defaultWidth, textureScale);
    }
  
    public Surface(TerrainGenerator tG, double radius, double width) {
      this(tG, radius, width, defaultTextureScale);
    }
  
    public Surface(TerrainGenerator tG, double radius, double width, double textureScale) {
      super(
        tG,
        new Vector3(Core.CHUNK_SIZE/2.0+0.5, radius*Math.cos(Math.PI/nPoints)-10, Core.CHUNK_SIZE/2.0+0.5), 
        generateMesh(radius, width),
        radius,
        width,
        textureScale
      );
    }
  }

  protected Ring(TerrainGenerator tG, Vector3 position, Model model, double radius, double width, double textureScale) {
    super(position, model);

    this.radius = radius;
    this.width = width;
  
    int w = (int)(width*textureScale); int h = (int)(2*Math.PI*radius*textureScale);
    float[] heightMap = tG.generateHeights(0, 0, w, h, 10, textureScale*Core.MAP_RANGE_SCALE, false);
    BufferedImage img = ImageProc.mapToImage(heightMap, w, h);
    Texture.createTexture("ringSurface", w, h, img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));

    this.model.setMat(new Material(Core.SOME_DIM, 0, new Vector3(), "ringSurface"));
    this.model.calculateRadius();

    setSlide(-World.getCentreChunkX()*Core.CHUNK_SIZE);
    setSpin ( World.getCentreChunkZ()*Core.CHUNK_SIZE);

    FileIO.writeImage("../results/ring_map.png", img);
    FileIO.saveToFile("../results/ring.obj", this.model.toString());
  }

  protected static Model generateMesh(double radius, double width) {
    Vector3[] verts = generateVerts(radius, width);
    Vector2[] vertUVs = generateUVs();
    Tri3D[] faces = generateFaces(verts, vertUVs);

    return new Model(verts, faces, vertUVs);
  }

  private static Vector3[] generateVerts(double radius, double width) {
    Vector3[] res = new Vector3[2*nPoints];

    for (int i = 0; i < nPoints; i++) {
      double ang = (i)/(nPoints/2.0);
      res[i*2  ] = new Vector3(-width/2, radius*Math.cos(Math.PI*ang), -radius*Math.sin(Math.PI*ang));
      res[i*2+1] = res[i*2].add(width, 0, 0);
    }

    return res;
  }

  private static Vector2[] generateUVs() {
    Vector2[] res = new Vector2[2*nPoints+2];

    for (int i = 0; i < nPoints+1; i++) {
      res[i*2  ] = new Vector2(0, (i+offset)/nPoints);
      res[i*2+1] = new Vector2(1, (i+offset)/nPoints);
    }

    return res;
  }

  private static Tri3D[] generateFaces(Vector3[] verts, Vector2[] vertUVs) {
    Tri3D[] res = new Tri3D[2*nPoints];

    int nPoints2=2*nPoints;

    for (int i = 0; i < nPoints; i++) {
      int a = i*2, b = i*2+1, c = i*2+2, d = i*2+3;
      res[i*2  ] = new Tri3D(
        new Vector3[]{verts[a], verts[d%nPoints2], verts[b]},
        new Vector2[]{vertUVs[a], vertUVs[d], vertUVs[b]},
        new int[]{a+1, (d%nPoints2)+1, b+1},
        new int[]{a+1, d+1, b+1}
      );
      res[i*2+1] = new Tri3D(
        new Vector3[]{verts[a], verts[c%nPoints2], verts[d%nPoints2]},
        new Vector2[]{vertUVs[a], vertUVs[c], vertUVs[d]},
        new int[]{a+1, (c%nPoints2)+1, (d%nPoints2)+1},
        new int[]{a+1, c+1, d+1}
      );
    }

    return res;
  }

  protected static Model generateMeshWithWalls(double radius, double width, double wallHeight) {
    Vector3[] verts = generateVertsWithWalls(radius, width, wallHeight);
    Vector2[] vertUVs = generateUVs();
    Tri3D[] faces = generateFacesWithWalls(verts, vertUVs);

    return new Model(verts, faces, vertUVs);
  }

  private static Vector3[] generateVertsWithWalls(double radius, double width, double wallHeight) {
    Vector3[] res = new Vector3[8*nPoints];

    for (int i = 0; i < nPoints; i++) {
      double ang = (i+offset)/(nPoints/2.0);
      double cos =  Math.cos(Math.PI*ang);
      double sin = -Math.sin(Math.PI*ang);
      res[i*8  ] = new Vector3(-(width+wallHeight/2)/2, (radius-wallHeight)*cos, (radius-wallHeight)*sin);
      res[i*8+1] = res[i*8  ].add(wallHeight/8, 0, 0);
      res[i*8+2] = new Vector3(-width/2, (radius-wallHeight*0.875)*cos, (radius-wallHeight*0.875)*sin);
      res[i*8+3] = new Vector3(-width/2, radius*cos, radius*sin);
      res[i*8+4] = res[i*8+3].scale(-1, 1, 1);
      res[i*8+5] = res[i*8+2].scale(-1, 1, 1);
      res[i*8+6] = res[i*8+1].scale(-1, 1, 1);
      res[i*8+7] = res[i*8  ].scale(-1, 1, 1);
    }

    return res;
  }

  private static Tri3D[] generateFacesWithWalls(Vector3[] verts, Vector2[] vertUVs) {
    Tri3D[] res = new Tri3D[14*nPoints]; // - 2
    // int off = 0;

    for (int i = 0; i < nPoints; i++) {
      int aT = i*2, bT = i*2+1, cT = i*2+2, dT = i*2+3;
      for (int j = 0; j < 7; j++) {
        // if (i == nPoints/2 - 1 && j == 3) {off = -2; continue;}
        int aV = (i)*8+(j), bV = (i)*8+(j+1), cV = ((i+1)%nPoints)*8+(j), dV = ((i+1)%nPoints)*8+(j+1);
        res[i*14+j*2  ] = new Tri3D( // +off
          new Vector3[]{verts  [aV], verts  [dV], verts  [bV]},
          new Vector2[]{vertUVs[aT], vertUVs[dT], vertUVs[bT]},
          new int[]{aV+1, dV+1, bV+1},
          new int[]{aT+1, dT+1, bT+1}
        );
        res[i*14+j*2+1] = new Tri3D( // +off
          new Vector3[]{verts  [aV], verts  [cV], verts  [dV]},
          new Vector2[]{vertUVs[aT], vertUVs[cT], vertUVs[dT]},
          new int[]{aV+1, cV+1, dV+1},
          new int[]{aT+1, cT+1, dT+1}
        );
      }
    }

    return res;
  }
  
  public void setSlide(double x) {
    this.position = new Vector3(x+Core.CHUNK_SIZE/2.0+0.5, this.position.y, this.position.z);
  }

  public void setSpin(double y) {
    // this.setPitch(57.29577951308232*(y/radius));
    Tri3D[] faces = this.model.getFaces();
    Vector2[] uvs = this.model.getVertUVs();
    double percent = y/(2*Math.PI*radius);
    for (int i = 0; i < faces.length; i++) {
      Vector2[] fUVs = faces[i].getVertUVs();
      int[] inds = faces[i].getVertexTextureIndeces();
      fUVs[0] = uvs[inds[0]-1].add(0, percent);
      fUVs[1] = uvs[inds[1]-1].add(0, percent);
      fUVs[2] = uvs[inds[2]-1].add(0, percent);
    }
  }

  public double getRadius() {
    return radius;
  }

  public double getWidth() {
    return width;
  }
}
