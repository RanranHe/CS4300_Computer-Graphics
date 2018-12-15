package sgraph;

import com.jogamp.opengl.GL3;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ray.HitRecord;
import ray.ThreeDRay;
import util.IVertexData;
import util.Light;
import util.Material;
import util.PolygonMesh;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A specific implementation of this scene graph. This implementation is still
 * independent of the rendering technology (i.e. OpenGL)
 *
 * @author Amit Shesh
 */
public class Scenegraph<VertexType extends IVertexData> implements IScenegraph<VertexType> {
  /**
   * The root of the scene graph tree
   */
  protected INode root;

  /**
   * A map to store the (name,mesh) pairs. A map is chosen for efficient search
   */
  protected Map<String, util.PolygonMesh<VertexType>> meshes;

  /**
   * A map to store the (name,node) pairs. A map is chosen for efficient search
   */
  protected Map<String, INode> nodes;

  protected Map<String, String> textures;

  /**
   * The associated renderer for this scene graph. This must be set before
   * attempting to render the scene graph
   */
  protected IScenegraphRenderer renderer;


  public Scenegraph() {
    root = null;
    meshes = new HashMap<String, util.PolygonMesh<VertexType>>();
    nodes = new HashMap<String, INode>();
    textures = new HashMap<String, String>();
  }

  public void dispose() {
    renderer.dispose();
  }

  /**
   * Sets the renderer, and then adds all the meshes to the renderer. This
   * function must be called when the scene graph is complete, otherwise not all
   * of its meshes will be known to the renderer
   *
   * @param renderer The {@link IScenegraphRenderer} object that will act as its
   *                 renderer
   */
  @Override
  public void setRenderer(IScenegraphRenderer renderer) throws Exception {
    this.renderer = renderer;

    //now add all the meshes
    for (String meshName : meshes.keySet()) {
      this.renderer.addMesh(meshName, meshes.get(meshName));
    }

    //pass all the texture objects
    for (Map.Entry<String, String> entry : textures.entrySet()) {
      renderer.addTexture(entry.getKey(), entry.getValue());
    }

  }


  /**
   * Set the root of the scenegraph, and then pass a reference to this scene
   * graph object to all its node. This will enable any node to call functions
   * of its associated scene graph
   */

  @Override
  public void makeScenegraph(INode root) {
    this.root = root;
    this.root.setScenegraph(this);

  }

  /**
   * Draw this scene graph. It delegates this operation to the renderer
   */
  @Override
  public void draw(Stack<Matrix4f> modelView) {
    if ((root != null) && (renderer != null)) {
      List<Light> listOfLights = root.getLightsInView(modelView);
      renderer.initLightsInShader(listOfLights);
      renderer.draw(root, modelView);
    }
  }


  @Override
  public void addPolygonMesh(String name, util.PolygonMesh<VertexType> mesh) {
    meshes.put(name, mesh);
  }


  @Override
  public void animate(float time) {

  }

  @Override
  public void addNode(String name, INode node) {
    nodes.put(name, node);
  }


  @Override
  public INode getRoot() {
    return root;
  }

  @Override
  public Map<String, PolygonMesh<VertexType>> getPolygonMeshes() {
    Map<String, util.PolygonMesh<VertexType>> meshes = new HashMap<String, PolygonMesh<VertexType>>(this.meshes);
    return meshes;
  }

  @Override
  public Map<String, INode> getNodes() {
    Map<String, INode> nodes = new TreeMap<String, INode>();
    nodes.putAll(this.nodes);
    return nodes;
  }

  @Override
  public void addTexture(String name, String path) {
    textures.put(name, path);
  }

  @Override
  public Color[][] raytrace(int width, int height, Stack<Matrix4f> stack) {
    System.out.println("Raytrace Begin in scenegraph");
    Color[][] colors = new Color[width][height];
    /**
     * create ray in view coordinates
     * start point: 0,0,0 always!
     * going through near plane pixel (i,j)
     * So 3D location of that pixel in view coordinates is
     * x = i-width/2
     * y = j-height/2
     * z = -0.5*height/tan(0.5*FOVY(theta))
     */
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        float vX = i - width / 2;
        float vY = j - height / 2;
        float vZ = (float) (-0.5f * height / Math
          .tan((float) Math.toRadians(60f)));

        float sX = 0;
        float sY = 0;
        float sZ = 0;

        Vector4f rayStartP = new Vector4f(sX, sY, sZ, 1);
        Vector4f rayDir = new Vector4f(vX, vY, vZ, 0);
        ThreeDRay ray = new ThreeDRay(rayStartP, rayDir);


        Vector4f c = raycast(ray, stack);
        colors[i][j] = new Color(c.x, c.y, c.z);
      }
    }
    System.out.println("Raytrace Finish in scenegraph");
//    for (int i = 0; i < colors.length; i++) {
//      for (int j =0; j < colors[i].length; j++) {
//        System.out.print(colors[i][j].getGreen() + " ");
//      }
//      System.out.println();
//    }

    return colors;
  }

  @Override
  public Vector4f raycast(ThreeDRay Ray, Stack<Matrix4f> stack) {

    Vector4f c = new Vector4f(0, 0, 0, 1);  //black



    HitRecord hitRecord = getRoot().intersect(Ray, stack);


    // Update color reference with HitRecord properties
    if (hitRecord.getIsHit()) {
      List<Light> lights = this.getRoot().getLightsInView(stack);
      Vector4f newColor = this.shade(hitRecord, lights);
      c.set(newColor.x, newColor.y, newColor.z, 0);
    } else {
      c.set(0, 0, 0, 1); // Black
    }

    return c;
  }

  private  Vector4f shade(HitRecord hitRecord, List<Light> lights) {
    Vector4f color = new Vector4f(0, 0, 0, 1);
    Vector4f intersectP = new Vector4f(hitRecord.getIntersectionP());
    Vector4f N = new Vector4f(hitRecord.getNormal());
    Vector4f intersectPNegate = new Vector4f(intersectP).negate().normalize();

    for (int i = 0; i < lights.size(); i++) {
      Light light = lights.get(i);
      Vector4f lightDir = new Vector4f();
      if (light.getPosition().w!=0) {
        lightDir = new Vector4f(light.getPosition()).sub(intersectP);
      }
      else {
        lightDir = new Vector4f(light.getPosition()).negate();
        lightDir = new Vector4f(lightDir.x, lightDir.y, lightDir.z, 0);
      }
      lightDir = lightDir.normalize();
      float normalDotLight = N.dot(lightDir);

      Vector4f I = lightDir.negate();
      Vector4f R = I.sub(N.mul(N.dot(I)).mul(2));//   R = I - 2 (N dot I) N
      R = R.normalize();

      float RdotP = Math.max(0.0f, R.dot(intersectPNegate));
      Material material = hitRecord.getMaterial();
      //ambient
      Vector4f materialAmbient = material.getAmbient();
      Vector3f lightAmbient = new Vector3f(light.getAmbient());
      Vector4f ambient =
        new Vector4f(materialAmbient).mul(lightAmbient.x, lightAmbient.y, lightAmbient.z, 1);
      //diffuse
      Vector4f materialDiffuse = material.getDiffuse();
      Vector3f lightDiffuse = new Vector3f(light.getDiffuse());
      Vector4f diffuse =
        new Vector4f(materialDiffuse).mul(lightDiffuse.x, lightDiffuse.y, lightDiffuse.z, 1)
        .mul(Math.max(0, normalDotLight));
//      System.out.println("diffuse :  " + diffuse.x);
      //specular
      Vector4f specular;
      if (normalDotLight > 0) {
        Vector4f materialSpecular = material.getSpecular();
        Vector3f lightSpecular = new Vector3f(light.getSpecular());
        specular =
          new Vector4f(materialSpecular).mul(lightSpecular.x, lightSpecular.y, lightSpecular.z, 1)
            .mul((float) Math.pow(RdotP, material.getShininess()));
      }
      else {
        specular = new Vector4f(0,0,0,1);
      }

      Vector4f spotDir = new Vector4f(light.getSpotDirection()).normalize();
      Vector4f lightDirNegate = new Vector4f(lightDir).negate();

      float spotAngle = (float) Math.cos(Math.toRadians(light.getSpotCutoff()));
      int start = 1;
      if (light.getPosition().w !=0) {
        if (new Vector4f(lightDirNegate).dot(new Vector4f(spotDir.normalize())) > spotAngle) {
          start = 1;
        }
        else {
          start = 0;
        }
      }




      System.out.println("diffuse: " + diffuse.x+" "+diffuse.y+" " + diffuse.z);
      Vector4f added = new Vector4f(0, 0, 0, 0).add(ambient).add(diffuse).add(specular);
      Vector4f multi = added.mul(start);
      color = color.add(multi.x,multi.y, multi.z, multi.w);



    }


    this.limit(color);



    return color;
  }

  private void limit(Vector4f vector) {
    if (vector.x > 1) {
      vector.x = 1;
    }
    if (vector.x < 0) {
      vector.x = 0;
    }

    if (vector.y > 1) {
      vector.y = 1;
    }
    if (vector.y < 0) {
      vector.y = 0;
    }

    if (vector.z > 1) {
      vector.z = 1;
    }
    if (vector.z < 0) {
      vector.z = 0;
    }
  }


}
