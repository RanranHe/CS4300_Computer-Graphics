//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.IVertexData;
import util.PolygonMesh;
import util.VertexProducer;

public class ObjImporter {
    public ObjImporter() {
    }

    public static <K extends IVertexData> PolygonMesh<K> importFile(VertexProducer<K> producer, InputStream in, boolean scaleAndCenter) throws IllegalArgumentException {
        PolygonMesh mesh = new PolygonMesh();
        ArrayList vertices = new ArrayList();
        ArrayList normals = new ArrayList();
        ArrayList texcoords = new ArrayList();
        ArrayList triangles = new ArrayList();
        ArrayList triangle_texture_indices = new ArrayList();
        ArrayList triangle_normal_indices = new ArrayList();
        Scanner sc = new Scanner(in);
        int lineno = 0;

        while(true) {
            int i;
            String line;
            Vector4f var24;
            do {
                do {
                    if(!sc.hasNext()) {
                        if(scaleAndCenter) {
                            Vector4f vertexData = new Vector4f((Vector4f)vertices.get(0));
                            Vector4f var22 = new Vector4f(vertexData);
                            var24 = new Vector4f(vertexData);

                            for(i = 1; i < vertices.size(); ++i) {
                                var22 = var22.min((Vector4f)vertices.get(i));
                                var24 = var24.max((Vector4f)vertices.get(i));
                            }

                            vertexData = (new Vector4f(var22)).add(var24).mul(0.5F);
                            float var28 = Math.max(var24.x - var22.x, Math.max(var24.y - var22.y, var24.z - var22.z));
                            Matrix4f var29 = (new Matrix4f()).mul((new Matrix4f()).scale(1.0F / var28, 1.0F / var28, 1.0F / var28)).mul((new Matrix4f()).translate(-vertexData.x, -vertexData.y, -vertexData.z));

                            for(i = 0; i < vertices.size(); ++i) {
                                vertices.set(i, var29.transform((Vector4f)vertices.get(i)));
                            }
                        }

                        ArrayList var21 = new ArrayList();

                        for(i = 0; i < vertices.size(); ++i) {
                            IVertexData var23 = producer.produce();
                            var23.setData("position", new float[]{((Vector4f)vertices.get(i)).x, ((Vector4f)vertices.get(i)).y, ((Vector4f)vertices.get(i)).z, ((Vector4f)vertices.get(i)).w});
                            if(texcoords.size() == vertices.size()) {
                                var23.setData("texcoord", new float[]{((Vector4f)texcoords.get(i)).x, ((Vector4f)texcoords.get(i)).y, ((Vector4f)texcoords.get(i)).z, ((Vector4f)texcoords.get(i)).w});
                            }

                            if(normals.size() == vertices.size()) {
                                var23.setData("normal", new float[]{((Vector4f)normals.get(i)).x, ((Vector4f)normals.get(i)).y, ((Vector4f)normals.get(i)).z, ((Vector4f)normals.get(i)).w});
                            }

                            var21.add(var23);
                        }

                        if(normals.size() == 0 || normals.size() != vertices.size()) {
                            mesh.computeNormals();
                        }

                        mesh.setVertexData(var21);
                        mesh.setPrimitives(triangles);
                        mesh.setPrimitiveType(4);
                        mesh.setPrimitiveSize(3);
                        return mesh;
                    }

                    line = sc.nextLine();
                    ++lineno;
                } while(line.length() <= 0);
            } while(line.charAt(0) == 35);

            new Scanner(line);
            String[] v = line.split("[ \\t\\n\\x0B\\f\\r]");
            if(v[0].compareTo("v") == 0) {
                if(v.length < 4 || v.length > 7) {
                    throw new IllegalArgumentException("Line " + lineno + ": Vertex coordinate has an invalid number of values");
                }

                Vector4f var27 = new Vector4f();
                var27.x = Float.parseFloat(v[1]);
                var27.y = Float.parseFloat(v[2]);
                var27.z = Float.parseFloat(v[3]);
                var27.w = 1.0F;
                if(v.length == 5) {
                    float var25 = Float.parseFloat(v[4]);
                    if(var25 != 0.0F) {
                        var27.x /= var25;
                        var27.y /= var25;
                        var27.z /= var25;
                    }
                }

                vertices.add(var27);
            } else if(v[0].compareTo("vt") == 0) {
                if(v.length < 3 || v.length > 4) {
                    throw new IllegalArgumentException("Line " + lineno + ": Texture coordinate has an invalid number of values");
                }

                var24 = new Vector4f();
                var24.x = Float.parseFloat(v[1]);
                var24.y = Float.parseFloat(v[2]);
                var24.z = 0.0F;
                var24.w = 1.0F;
                if(v.length > 3) {
                    var24.z = Float.parseFloat(v[3]);
                }

                texcoords.add(var24);
            } else if(v[0].compareTo("vn") == 0) {
                if(v.length != 4) {
                    throw new IllegalArgumentException("Line " + lineno + ": Normal has an invalid number of values");
                }

                Vector3f var26 = new Vector3f();
                var26.x = Float.parseFloat(v[1]);
                var26.y = Float.parseFloat(v[2]);
                var26.z = Float.parseFloat(v[3]);
                var26 = var26.normalize();
                normals.add(new Vector4f(var26, 0.0F));
            } else if(v[0].compareTo("f") == 0) {
                if(v.length < 4) {
                    throw new IllegalArgumentException("Line " + lineno + ": Face has too few vertices, must be at least 3");
                }

                ArrayList maximum = new ArrayList();
                ArrayList longest = new ArrayList();
                ArrayList transformMatrix = new ArrayList();

                for(i = 1; i < v.length; ++i) {
                    String[] data = v[i].split("[/]");
                    if(data.length < 1 && data.length > 3) {
                        throw new IllegalArgumentException("Line " + lineno + ": Face specification has an incorrect number of values");
                    }

                    maximum.add(Integer.valueOf(Integer.parseInt(data[0]) - 1));
                    if(data.length > 1) {
                        if(data[1].length() > 0) {
                            longest.add(Integer.valueOf(Integer.parseInt(data[1]) - 1));
                        }

                        if(data.length > 2) {
                            transformMatrix.add(Integer.valueOf(Integer.parseInt(data[2]) - 1));
                        }
                    }
                }

                if(maximum.size() < 3) {
                    throw new IllegalArgumentException("Line " + lineno + ": Fewer than 3 vertices for a polygon");
                }

                for(i = 2; i < maximum.size(); ++i) {
                    triangles.add(maximum.get(0));
                    triangles.add(maximum.get(i - 1));
                    triangles.add(maximum.get(i));
                    if(longest.size() > 0) {
                        triangle_texture_indices.add(longest.get(0));
                        triangle_texture_indices.add(longest.get(i - 1));
                        triangle_texture_indices.add(longest.get(i));
                    }

                    if(transformMatrix.size() > 0) {
                        triangle_normal_indices.add(transformMatrix.get(0));
                        triangle_normal_indices.add(transformMatrix.get(i - 1));
                        triangle_normal_indices.add(transformMatrix.get(i));
                    }
                }
            }
        }
    }
}
