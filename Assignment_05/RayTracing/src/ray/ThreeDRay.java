package ray;

import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Created by liangmanman1 on 11/29/16.
 */
public class ThreeDRay {

  private Vector4f startP;
  private Vector4f dir;

  /**
   * Constructs a ray with empty construct
   */
  public ThreeDRay() {
    Vector4f startP= new Vector4f(0, 0, 0, 1);
    Vector4f dir = new Vector4f(0, 0, 0, 0);
  }

  /**
   * Constructs a ray with given startP and dir
   * @param startP the start position
   * @param dir the direction vector
   */
  public ThreeDRay(Vector4f startP, Vector4f dir) {
    this.startP = new Vector4f(startP);
    this.dir = new Vector4f(dir);
  }

  /**
   *
   * @return the start Position in Vector4f
   */
  public Vector4f getStartP() {
    return startP;
  }

  /**
   *
   * @return the direction of ray in Vector4f
   */
  public Vector4f getDir() {
    return dir;
  }

  /**
   * set the startP to the given Position
   *
   */
  public void setStartP(Vector4f startP) {
    this.startP = new Vector4f(startP);
  }

  /**
   * set the direction to the given direction
   *
   */
  public void setDir(Vector4f direction) {
    this.dir = new Vector4f(direction);
  }


  /**
   * Given a transformMatrix, transform this ray's start position and direction
   * @param transMatrix
   * @return a new ray based on its own value and transMatrix
   */
  public ThreeDRay transRay(Matrix4f transMatrix) {
    ThreeDRay newRay= new ThreeDRay(this.getStartP(), this.getDir());

    newRay.setStartP(newRay.getStartP().mul(transMatrix));
    newRay.setDir(newRay.getDir().mul(transMatrix));

    return newRay;
  }

  /**
   * return the intersection of the ray
   * @param t time
   * @return
   */
  public Vector4f intersection(float t) {
    return new Vector4f(startP)
      .add(new Vector4f(dir).mul(t));
  }

}
