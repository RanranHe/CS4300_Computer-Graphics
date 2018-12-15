//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector4f;
import util.IVertexData;

public class PolygonMesh<VertexType extends IVertexData> {
    protected List<VertexType> vertexData = new ArrayList();
    protected List<Integer> primitives = new ArrayList();
    protected int primitiveType;
    protected int primitiveSize;
    protected Vector4f minBounds;
    protected Vector4f maxBounds;

    public PolygonMesh() {
        this.primitiveType = this.primitiveSize = 0;
        this.minBounds = new Vector4f();
        this.maxBounds = new Vector4f();
    }

    public void setPrimitiveType(int v) {
        this.primitiveType = v;
    }

    public int getPrimitiveType() {
        return this.primitiveType;
    }

    public void setPrimitiveSize(int s) {
        this.primitiveSize = s;
    }

    public int getPrimitiveSize() {
        return this.primitiveSize;
    }

    public int getPrimitiveCount() {
        return this.primitives.size();
    }

    public int getVertexCount() {
        return this.vertexData.size();
    }

    public Vector4f getMinimumBounds() {
        return new Vector4f(this.minBounds);
    }

    public Vector4f getMaximumBounds() {
        return new Vector4f(this.maxBounds);
    }

    public List<VertexType> getVertexAttributes() {
        return new ArrayList(this.vertexData);
    }

    public List<Integer> getPrimitives() {
        return new ArrayList(this.primitives);
    }

    public void setVertexData(List<VertexType> vp) {
        this.vertexData = new ArrayList(vp);
        this.computeBoundingBox();
    }

    public void setPrimitives(List<Integer> t) {
        this.primitives = new ArrayList(t);
    }

    protected void computeBoundingBox() {
        if(this.vertexData.size() > 0) {
            if(((IVertexData)this.vertexData.get(0)).hasData("position")) {
                ArrayList positions = new ArrayList();
                Iterator p = this.vertexData.iterator();

                while(p.hasNext()) {
                    IVertexData v = (IVertexData)p.next();
                    float[] data = v.getData("position");
                    Vector4f pos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
                    switch(data.length) {
                    case 4:
                        pos.w = data[3];
                    case 3:
                        pos.z = data[2];
                    case 2:
                        pos.y = data[1];
                    case 1:
                        pos.x = data[0];
                    default:
                        positions.add(pos);
                    }
                }

                this.minBounds = new Vector4f((Vector4f)positions.get(0));
                this.maxBounds = new Vector4f((Vector4f)positions.get(0));

                for(int j = 0; j < positions.size(); ++j) {
                    Vector4f var7 = (Vector4f)positions.get(j);
                    if(var7.x < this.minBounds.x) {
                        this.minBounds.x = var7.x;
                    }

                    if(var7.x > this.maxBounds.x) {
                        this.maxBounds.x = var7.x;
                    }

                    if(var7.y < this.minBounds.y) {
                        this.minBounds.y = var7.y;
                    }

                    if(var7.y > this.maxBounds.y) {
                        this.maxBounds.y = var7.y;
                    }

                    if(var7.z < this.minBounds.z) {
                        this.minBounds.z = var7.z;
                    }

                    if(var7.z > this.maxBounds.z) {
                        this.maxBounds.z = var7.z;
                    }
                }

            }
        }
    }

    public void computeNormals() {
        if(this.vertexData.size() > 0) {
            if(((IVertexData)this.vertexData.get(0)).hasData("position")) {
                if(((IVertexData)this.vertexData.get(0)).hasData("normal")) {
                    ArrayList positions = new ArrayList();
                    Iterator normals = this.vertexData.iterator();

                    while(normals.hasNext()) {
                        IVertexData norm = (IVertexData)normals.next();
                        float[] v = norm.getData("position");
                        Vector4f pos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
                        switch(v.length) {
                        case 4:
                            pos.w = v[3];
                        case 3:
                            pos.z = v[2];
                        case 2:
                            pos.y = v[1];
                        case 1:
                            pos.x = v[0];
                        default:
                            positions.add(pos);
                        }
                    }

                    ArrayList var9 = new ArrayList();

                    int i;
                    for(i = 0; i < positions.size(); ++i) {
                        var9.add(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));
                    }

                    for(i = 0; i < this.primitives.size(); i += this.primitiveSize) {
                        Vector4f var10 = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
                        int[] var11 = new int[this.primitiveSize];

                        int k;
                        for(k = 0; k < this.primitiveSize; ++k) {
                            var11[k] = ((Integer)this.primitives.get(i + k)).intValue();
                        }

                        for(k = 0; k < this.primitiveSize; ++k) {
                            var10.x += (((Vector4f)positions.get(var11[k])).y - ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).y) * (((Vector4f)positions.get(var11[k])).z + ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).z);
                            var10.y += (((Vector4f)positions.get(var11[k])).z - ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).z) * (((Vector4f)positions.get(var11[k])).x + ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).x);
                            var10.z += (((Vector4f)positions.get(var11[k])).x - ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).x) * (((Vector4f)positions.get(var11[k])).y + ((Vector4f)positions.get(var11[(k + 1) % this.primitiveSize])).y);
                        }

                        var10 = var10.normalize();

                        for(k = 0; k < this.primitiveSize; ++k) {
                            var9.set(var11[k], ((Vector4f)var9.get(var11[k])).add(var10));
                        }
                    }

                    for(i = 0; i < var9.size(); ++i) {
                        var9.set(i, ((Vector4f)var9.get(i)).normalize());
                    }

                    for(i = 0; i < this.vertexData.size(); ++i) {
                        ((IVertexData)this.vertexData.get(i)).setData("normal", new float[]{((Vector4f)var9.get(i)).x, ((Vector4f)var9.get(i)).y, ((Vector4f)var9.get(i)).z, ((Vector4f)var9.get(i)).w});
                    }

                }
            }
        }
    }
}
