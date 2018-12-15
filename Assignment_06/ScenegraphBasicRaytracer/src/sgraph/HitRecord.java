package sgraph;

import org.joml.Vector4f;

/**
 * Created by ashesh on 4/12/2016.
 */
public class HitRecord {
    public float time;
    public Vector4f point,normal, texture;
    public util.Material material;

    public HitRecord() {
        time = Float.POSITIVE_INFINITY;
        point = new Vector4f(0,0,0,1);
        normal = new Vector4f(0,0,1,0);
        texture = new Vector4f(1,1,1,1);
        material = new util.Material();
    }

    public boolean intersected()
    {
        return time < Float.POSITIVE_INFINITY;
    }
}
