//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Vector4f;
import util.IVertexData;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

public class ObjectInstance {
    protected IntBuffer vao = IntBuffer.allocate(1);
    protected IntBuffer vbo = IntBuffer.allocate(2);
    protected PolygonMesh<?> mesh;
    protected String name;

    public ObjectInstance(GL3 gl, String name) {
        gl.glGenVertexArrays(1, this.vao);
        gl.glBindVertexArray(this.vao.get(0));
        gl.glGenBuffers(2, this.vbo);
        this.setName(name);
    }

    public <K extends IVertexData> ObjectInstance(GL3 gl, ShaderProgram program, ShaderLocationsVault shaderLocations, Map<String, String> shaderVarsToAttributeNames, PolygonMesh<K> mesh, String name) {
        gl.glGenVertexArrays(1, this.vao);
        gl.glBindVertexArray(this.vao.get(0));
        gl.glGenBuffers(2, this.vbo);
        this.setName(name);
        this.initPolygonMesh(gl, program, shaderLocations, shaderVarsToAttributeNames, mesh);
    }

    protected <K extends IVertexData> void initPolygonMesh(GL3 gl, ShaderProgram program, ShaderLocationsVault shaderLocations, Map<String, String> shaderVarsToAttributeNames, PolygonMesh<K> mesh) {
        List vertexDataList = mesh.getVertexAttributes();
        List primitives = mesh.getPrimitives();
        int[] primitivesAsArray = new int[primitives.size()];

        int i;
        for(i = 0; i < primitives.size(); ++i) {
            primitivesAsArray[i] = ((Integer)primitives.get(i)).intValue();
        }

        IntBuffer indexBuffer = IntBuffer.wrap(primitivesAsArray);
        int sizeOfOneVertex = 0;
        HashMap offsets = new HashMap();

        Entry vertexDataAsFloats;
        for(Iterator stride = shaderVarsToAttributeNames.entrySet().iterator(); stride.hasNext(); sizeOfOneVertex += ((IVertexData)vertexDataList.get(0)).getData((String)vertexDataAsFloats.getValue()).length) {
            vertexDataAsFloats = (Entry)stride.next();
            offsets.put(vertexDataAsFloats.getValue(), Integer.valueOf(sizeOfOneVertex));
        }

        int var21;
        if(shaderVarsToAttributeNames.size() > 1) {
            var21 = sizeOfOneVertex;
        } else {
            var21 = 0;
        }

        float[] var22 = new float[sizeOfOneVertex * vertexDataList.size()];
        i = 0;
        Iterator vertexDataAsBuffer = vertexDataList.iterator();

        while(vertexDataAsBuffer.hasNext()) {
            IVertexData v = (IVertexData)vertexDataAsBuffer.next();
            Iterator e = shaderVarsToAttributeNames.entrySet().iterator();

            while(e.hasNext()) {
                Entry shaderLocation = (Entry)e.next();
                float[] data = v.getData((String)shaderLocation.getValue());

                for(int j = 0; j < data.length; ++i) {
                    var22[i] = data[j];
                    ++j;
                }
            }
        }

        FloatBuffer var23 = FloatBuffer.wrap(var22);
        this.mesh = mesh;
        this.mesh.computeBoundingBox();
        gl.glBindVertexArray(this.vao.get(0));
        program.enable(gl);
        gl.glBindBuffer(34962, this.vbo.get(0));
        gl.glBufferData(34962, (long)(var23.capacity() * 4), var23, 35044);
        Iterator var24 = shaderVarsToAttributeNames.entrySet().iterator();

        while(var24.hasNext()) {
            Entry var25 = (Entry)var24.next();
            int var26 = shaderLocations.getLocation((String)var25.getKey()).intValue();
            if(var26 >= 0) {
                gl.glVertexAttribPointer(var26, ((IVertexData)vertexDataList.get(0)).getData((String)var25.getValue()).length, 5126, false, 4 * var21, (long)(4 * ((Integer)offsets.get(var25.getValue())).intValue()));
                gl.glEnableVertexAttribArray(var26);
            }
        }

        gl.glBindBuffer(34963, this.vbo.get(1));
        gl.glBufferData(34963, (long)(indexBuffer.capacity() * 4), indexBuffer, 35044);
        gl.glBindVertexArray(0);
        program.disable(gl);
    }

    public void cleanup(GLAutoDrawable gla) {
        GL3 gl = gla.getGL().getGL3();
        if(this.vao.get(0) != 0) {
            gl.glDeleteBuffers(2, this.vbo);
            gl.glDeleteVertexArrays(1, this.vao);
        }

    }

    public void draw(GLAutoDrawable gla) {
        GL3 gl = gla.getGL().getGL3();
        gl.glBindVertexArray(this.vao.get(0));
        gl.glBindBuffer(34962, this.vbo.get(0));
        gl.glBindBuffer(34963, this.vbo.get(1));
        gl.glDrawElements(this.mesh.getPrimitiveType(), this.mesh.getPrimitiveCount(), 5125, 0L);
        gl.glBindBuffer(34962, 0);
        gl.glBindBuffer(34963, 0);
        gl.glBindVertexArray(0);
    }

    public PolygonMesh getMesh() {
        return this.mesh;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Vector4f getMinimumBounds() {
        return this.mesh.getMinimumBounds();
    }

    public Vector4f getMaximumBounds() {
        return this.mesh.getMaximumBounds();
    }
}
