package sgraph;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLOffscreenAutoDrawable;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import ray.HitRecord;
import ray.ThreeDRay;
import util.TextureImage;

/**
 * This node represents the leaf of a scene graph. It is the only type of node that has
 * actual geometry to render.
 * @author Amit Shesh
 */
public class LeafNode extends AbstractNode
{
    /**
     * The name of the object instance that this leaf contains. All object instances are stored
     * in the scene graph itself, so that an instance can be reused in several leaves
     */
    protected String objInstanceName;
    /**
     * The material associated with the object instance at this leaf
     */
    protected util.Material material;

    protected String textureName;

    public LeafNode(String instanceOf,IScenegraph graph,String name)
    {
        super(graph,name);
        this.objInstanceName = instanceOf;
    }



    /*
	 *Set the material of each vertex in this object
	 */
    @Override
    public void setMaterial(util.Material mat)
    {
        material = new util.Material(mat);
    }

    /**
     * Set texture ID of the texture to be used for this leaf
     * @param name
     */
    @Override
    public void setTextureName(String name)
    {
        textureName = name;
    }

    /*
     * gets the material
     */
    public util.Material getMaterial()
    {
        return material;
    }

    @Override
    public INode clone()
    {
        LeafNode newclone = new LeafNode(this.objInstanceName,scenegraph,name);
        newclone.setMaterial(this.getMaterial());
        return newclone;
    }


    /**
     * Delegates to the scene graph for rendering. This has two advantages:
     * <ul>
     *     <li>It keeps the leaf light.</li>
     *     <li>It abstracts the actual drawing to the specific implementation of the scene graph renderer</li>
     * </ul>
     * @param context the generic renderer context {@link sgraph.IScenegraphRenderer}
     * @param modelView the stack of modelview matrices
     * @throws IllegalArgumentException
     */
    @Override
    public void draw(IScenegraphRenderer context,Stack<Matrix4f> modelView) throws IllegalArgumentException
    {
        if (objInstanceName.length()>0)
        {
            context.drawMesh(objInstanceName,material,textureName,modelView.peek());
        }
    }

    @Override
    public HitRecord intersect(ThreeDRay ray, Stack<Matrix4f> stack) {
        Matrix4f matrixRay = new Matrix4f(stack.peek());
        matrixRay.invert();
        ThreeDRay newRay = ray.transRay(matrixRay);
        HitRecord newOne = new HitRecord();

        if (this.objInstanceName.contains("box")) {
            newOne = this.intersectBox(newRay, stack);
        }
        else if (this.objInstanceName.contains("sphere")) {
            newOne = this.intersectSphere(newRay, stack);
        }
        return newOne;
    }



    private HitRecord intersectBox(ThreeDRay ray, Stack<Matrix4f> stack) {
        HitRecord result = new HitRecord();
        Matrix4f invert = new Matrix4f(stack.peek()).invert();

        ThreeDRay objRay = ray.transRay(invert);
//        Vector4f startP = objRay.getStartP();
//        Vector4f dir = objRay.getDir();
        Vector4f startP = ray.getStartP();
        Vector4f dir = ray.getDir();



        float tMin = Float.NEGATIVE_INFINITY;
        float tMax = Float.POSITIVE_INFINITY;

        if (dir.x !=0) {
            float txMin = ((-0.5f-startP.x)/dir.x);
            float txMax =  ((0.5f-startP.x)/dir.x);
            if (tMin > tMax) {
                float temp = tMax;
                tMax = tMin;
                tMin =temp;
            }
            tMin = Math.max(tMin, Math.min(txMax, txMin));
            tMax = Math.min(tMax, Math.max(txMax, txMin));
        }

        if (dir.y !=0) {
            float tyMin =((-0.5f-startP.y)/dir.y);
            float tyMax =((0.5f-startP.y)/dir.y);
            if (tyMin > tyMax) {
                float temp = tyMax;
                tyMax = tyMin;
                tyMin =temp;
            }
            tMin = Math.max(tMin, Math.min(tyMax, tyMin));
            tMax = Math.min(tMax, Math.max(tyMax, tyMin));
        }
        if (dir.z !=0) {
            float tzMin = ((-0.5f-startP.z)/dir.z);
            float tzMax =  ((0.5f-startP.z)/dir.z);
            if (tzMin > tzMax) {
                float temp = tzMax;
                tzMax = tzMin;
                tzMin =temp;
            }
            tMin = Math.max(tMin, Math.min(tzMax, tzMin));
            tMax = Math.min(tMax, Math.max(tzMax, tzMin));
        }

        float near;


        if (tMax > 0 && tMin <= tMax) {
            if (!(tMin <=0)) {
                near = tMin;
            }
            else {
                near = tMax;
            }
            Vector4f newIntersectP = ray.intersection(near);


            Matrix4f transpose = new Matrix4f(stack.peek()).invert().transpose();



            Vector4f newObjRay = objRay.intersection(near);

            Vector4f objNormal;
            int xNormal = 0;
            int yNormal = 0;
            int zNormal = 0;

            if(Math.abs(newObjRay.x + 0.5f) < 1f) {
                xNormal = -1;
            }

            else if(Math.abs(newObjRay.x - (0.5f)) < 1f) {
                xNormal = 1;
            }

            else if(Math.abs(newObjRay.y+ 0.5f) < 1f) {
                yNormal = -1;
            }

            else if(Math.abs(newObjRay.y - (0.5f)) < 1f) {
                yNormal= 1;
            }

            else if(Math.abs(newObjRay.z + 0.5f) < 1f) {
                zNormal = -1;
            }

            else if(Math.abs(newObjRay.z- (0.5f)) < 1f) {
                zNormal = 1;
            }
            objNormal  = new Vector4f(xNormal, yNormal, zNormal, 0);


            System.out.println("normal " + objNormal.toString());
            Vector4f normal = transpose.transform(objNormal);

            result = new HitRecord(near, newIntersectP, normal, this.getMaterial(),this
              .textureName);
        }



        return result;
    }

    private HitRecord intersectSphere(ThreeDRay ray, Stack<Matrix4f> stack) {
        HitRecord result = new HitRecord();
        Vector4f startP = ray.getStartP();
        Vector4f dir = ray.getDir();

        float A = dir.x * dir.x + dir.y*dir.y + dir.z*dir.z;
        float B = 2* (dir.x*(startP.x-0) +dir.y*(startP.y-0)+dir.z*(startP.z-0)); // center: (0,0,0)
        float C = (startP.x - 0) * (startP.x - 0)
          + (startP.y - 0)*(startP.y - 0)
          + (startP.z - 0)*(startP.z - 0)
          -1;

        float delta = B*B - 4 * A * C;

        if (delta >= 0 ) {
            float t1 = B*(-1)-((float) Math.sqrt(delta))/(2*A);
            float t2 = B*(-1)+((float) Math.sqrt(delta))/(2*A);
            float tMin = Math.min(t1, t2);
            if (tMin > 0 ) {
                Vector4f newIntersectP = startP.add(dir.mul(tMin));
                stack.push(stack.peek());
                stack.peek().invert().transpose();
                Vector4f normal = newIntersectP.mul(stack.peek());
                normal = new Vector4f(normal.x, normal.y, normal.z, 0);
                stack.pop();
                result = new HitRecord(tMin, newIntersectP, normal, this.getMaterial(),this.textureName);
            }
        }
        return result;

    }


}
