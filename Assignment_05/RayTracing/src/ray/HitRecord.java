package ray;

/**
 * Created by liangmanman1 on 11/29/16.
 */

import org.joml.Matrix4f;
import org.joml.Vector4f;

import sgraph.LeafNode;
import util.Material;

/**
 Stores all the information that you will need to determine the closest object that was hit, and
 information about that object to calculate shading.

 1. t
 2. intersection point
 3. 3D Normal
 4. material
 5. texture
 */
public class HitRecord {

  private float t;
  private Vector4f intersectionP;
  private Vector4f normal;
  private util.Material material;
  private String texture;
  boolean isHit;

  /**
   * empty constructor
   */
  public HitRecord() {
    t = Float.MAX_VALUE;
    intersectionP = new Vector4f(0, 0, 0, 1);
    normal = new Vector4f(0, 0, 0, 0);
    material = new Material();
    texture = null;
    isHit = false;
  }

  /**
   * 2rd constructor
   * @param t
   * @param intersectionP
   * @param normal
   * @param material
   * @param texture
   */
  public HitRecord(float t,
                   Vector4f intersectionP,
                   Vector4f normal,
                   Material material,
                   String texture) {
    this.t = t;
    this.intersectionP = intersectionP;
    this.normal = normal;
    this.material = material;
    this.texture = texture;
    this.isHit = true;
  }

  /**
   * the methods below return the attributes of the hitRecord
   *
   */

  public float getT() {
    return t;
  }

  public Vector4f getIntersectionP() {
    return intersectionP;
  }

  public Vector4f getNormal() {
    return normal;
  }

  public Material getMaterial() {
    return material;
  }

  public String getTexture() {
    return texture;
  }


  public boolean getIsHit() {
    return this.isHit;
  }

  /**
   *
   * the methods below updates the attributes of the hitRecord
   */

  public void updateHitRecord(HitRecord newHit) {
    this.t = newHit.getT();
    this.intersectionP = newHit.getIntersectionP();
    this.normal = newHit.getNormal();
    this.material = newHit.material;
    this.texture = newHit.getTexture();
    isHit = true;
  }


  public void setT(float t) {
    this.t = t;
  }

  public void setIntersectionP(Vector4f intersectionP) {
    this.intersectionP = new Vector4f(intersectionP);
  }

  public void setNormal(Vector4f normal) {
    this.normal = new Vector4f(normal);
  }

  public void setMaterial(Material material) {
    this.material = new Material(material);
  }

  public void setTexture(String texture) {
    this.texture = texture;
  }

}
