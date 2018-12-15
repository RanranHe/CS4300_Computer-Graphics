//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import util.IVertexData;
import util.PolygonMesh;

public class ObjExporter {
    public ObjExporter() {
    }

    public static boolean exportFile(PolygonMesh<IVertexData> mesh, OutputStream out) {
        PrintWriter printer = new PrintWriter(out);
        List vertexData = mesh.getVertexAttributes();
        if(vertexData.size() == 0) {
            return true;
        } else {
            new ArrayList();
            new ArrayList();
            new ArrayList();
            List primitives = mesh.getPrimitives();
            Iterator var10 = vertexData.iterator();

            while(true) {
                int j;
                IVertexData v;
                float[] data;
                do {
                    if(!var10.hasNext()) {
                        var10 = vertexData.iterator();

                        while(true) {
                            do {
                                if(!var10.hasNext()) {
                                    var10 = vertexData.iterator();

                                    while(true) {
                                        do {
                                            if(!var10.hasNext()) {
                                                for(int i = 0; i < primitives.size(); i += mesh.getPrimitiveSize()) {
                                                    printer.print("f ");

                                                    for(j = 0; j < mesh.getPrimitiveSize(); ++j) {
                                                        printer.print(((Integer)primitives.get(i + j)).intValue() + 1 + " ");
                                                    }

                                                    printer.println();
                                                }

                                                printer.close();
                                                return true;
                                            }

                                            v = (IVertexData)var10.next();
                                        } while(!v.hasData("texcoord"));

                                        data = v.getData("texcoord");
                                        printer.print("vt ");

                                        for(j = 0; j < data.length; ++j) {
                                            printer.print(data[j] + " ");
                                        }

                                        printer.println();
                                    }
                                }

                                v = (IVertexData)var10.next();
                            } while(!v.hasData("normal"));

                            data = v.getData("normal");
                            printer.print("vn ");

                            for(j = 0; j < data.length; ++j) {
                                printer.print(data[j] + " ");
                            }

                            printer.println();
                        }
                    }

                    v = (IVertexData)var10.next();
                } while(!v.hasData("position"));

                data = v.getData("position");
                printer.print("v ");

                for(j = 0; j < data.length; ++j) {
                    printer.print(data[j] + " ");
                }

                printer.println();
            }
        }
    }
}
