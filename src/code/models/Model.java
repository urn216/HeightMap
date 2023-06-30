package code.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import mki.io.FileIO;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public abstract class Model {

  protected Vector3 position;

  protected final Vector3[] verts;
  protected final Tri[]     faces;
  protected final Vector2[] vertUVs;

  protected Model(Vector3[] verts, Tri[] faces, Vector2[] vertUVs) {
    if (verts == null || faces == null || vertUVs == null) throw new RuntimeException("3D models cannot have null fields");
    this.verts   = verts;
    this.faces   = faces;
    this.vertUVs = vertUVs;
  }

  protected Model(Object[][] elems) {
    this(
      (Vector3[])elems[0],
      (Tri    [])elems[1],
      (Vector2[])elems[2]
    );
  }

  protected static Object[][] generateMesh(String model) {
    List<Vector3> vs = new ArrayList<Vector3>();
    List<Vector2> vts = new ArrayList<Vector2>();
    List<Tri> fs = new ArrayList<Tri>();
    String filename = "data/" + model;
    List<String> allLines = FileIO.readAllLines(filename, false);
    for (String line : allLines) {
      Scanner scan = new Scanner(line);
      scan.useDelimiter("[/ ]+");
      String type;
      if (scan.hasNext()) {
        type = scan.next();
      }
      else {type = "gap";}
      if (type.equals("v")) {vs.add(new Vector3(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));}
      else if (type.equals("vt")) {vts.add(new Vector2(scan.nextDouble(), scan.nextDouble()));}
      else if (type.equals("f")) {
        if (vts.isEmpty()) {
          int a = scan.nextInt(), b = scan.nextInt(), c = scan.nextInt();
          fs.add(new Tri(
            new Vector3[]{vs.get(a-1), vs.get(b-1), vs.get(c-1)}, 
            new int[]{a, b, c}
          ));
        }
        else {
          int a = scan.nextInt(), A = scan.nextInt(), b = scan.nextInt(), B = scan.nextInt(), c = scan.nextInt(), C = scan.nextInt();
          fs.add(new Tri(
            new Vector3[]{vs.get(a-1), vs.get(b-1), vs.get(c-1)}, 
            new Vector2[]{vts.get(A-1), vts.get(B-1), vts.get(C-1)}, 
            new int[]{a, b, c}, 
            new int[]{A, B, C}
          ));
        }
      }
      scan.close();
    }
    Object[][] res = new Object[][]{vs.toArray(new Vector3[vs.size()]), fs.toArray(new Tri[fs.size()]), vts.toArray(new Vector2[vts.size()])};
    return res;
  }

  public double calculateRadius() {
    double biggest = 0;
    for (Vector3 vert : verts) {
      double dist = vert.magnitude();
      if (dist > biggest) biggest = dist;
    }
    System.out.println("RADIUS   : "+ biggest);
    System.out.println("NUM VERTS: "+ verts.length);
    return biggest;
  }

  public Vector3 getPos() {return position;}

  public void setPos(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public double getRad() {return 1;}

  public Tri[] getFaces() {return faces;}

  public String toString() {
    StringBuilder res = new StringBuilder(1000);
    res.append("o " + this.getClass().getSimpleName() + "\n");
    for (Vector3 v : verts) res.append("v " + v.x + " " + v.y + " " + v.z + "\n");
    for (Vector2 vt : vertUVs) res.append("vt " + vt.x + " " + vt.y + "\n");
    res.append("s off\n");
    for (Tri f : faces) res.append("f " + f.toString() + "\n");
    return res.toString();
  }
}
