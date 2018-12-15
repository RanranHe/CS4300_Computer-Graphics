//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

public interface IVertexData {
    boolean hasData(String var1);

    float[] getData(String var1) throws IllegalArgumentException;

    void setData(String var1, float[] var2) throws IllegalArgumentException;

    String[] getAllAttributes();
}
