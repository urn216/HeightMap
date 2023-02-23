package code.math;
/**
* 3D Vector
*/
public class Vector3  // implements Comparable<Vector3>
{
  public double x;
  public double y;
  public double z;

  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  private Vector3(Vector3 old) {
    this.x = old.x;
    this.y = old.y;
    this.z = old.z;
  }

  public Vector3() {x = y = z = 0;}

  public Vector3(double v) {x = y = z = v;}

  public static Vector3 fromAngle(Vector2 ang) {
    double up = Math.sin(Math.toRadians(ang.y));
    // double aUp = Math.abs(up);
    double aUp = 0;
    return new Vector3((1-aUp) * Math.sin(Math.toRadians(ang.x)), up, (1-aUp) * Math.cos(Math.toRadians(ang.x)));
  }

  public static Vector3 abs(Vector3 input) {
    return new Vector3(Math.abs(input.x), Math.abs(input.y), Math.abs(input.z));
  }

  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }

  public double magnitude() {
    return Math.sqrt((x*x)+(y*y)+(z*z));
  }

  public double magsquare() {
    return (x*x)+(y*y)+(z*z);
  }

  public Vector3 unitize() {
    double mag = magnitude();
    if (mag == 0) {mag = 1;}
    return new Vector3(x/mag, y/mag, z/mag);
  }

  public Vector3 subtract(Vector3 other) {
    return new Vector3(this.x-other.x, this.y-other.y, this.z-other.z);
  }

  public Vector3 subtract(double other) {
    return new Vector3(this.x-other, this.y-other, this.z-other);
  }

  public Vector3 add(Vector3 other) {
    return new Vector3(this.x+other.x, this.y+other.y, this.z+other.z);
  }

  public Vector3 add(double other) {
    return new Vector3(this.x+other, this.y+other, this.z+other);
  }

  public Vector3 multiply(Vector3 other) {
    return new Vector3(this.x*other.x, this.y*other.y, this.z*other.z);
  }

  public Vector3 multiply(double other) {
    return new Vector3(this.x*other, this.y*other, this.z*other);
  }

  public double dot(Vector3 other) {
    return this.x*other.x + this.y*other.y + this.z*other.z;
  }

  public Vector3 cross(Vector3 other) {
    return new Vector3(this.y*other.z-this.z*other.y, this.z*other.x-this.x*other.z, this.x*other.y-this.y*other.x);
  }

  public Vector3 copy() {
    return new Vector3(this);
  }

  public double toAngle() {
    return Math.atan2(y, x);
  }
}
