//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import com.jogamp.opengl.GL3;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;
import util.ShaderLocationsVault;

public class ShaderProgram {
    private int program = -1;
    private ShaderProgram.ShaderInfo[] shaders = new ShaderProgram.ShaderInfo[2];
    private boolean enabled = false;

    public ShaderProgram() {
    }

    public int getProgram() {
        return this.program;
    }

    public void createProgram(GL3 gl, String vertShaderFile, String fragShaderFile) throws FileNotFoundException, Exception {
        this.releaseShaders(gl);
        this.shaders[0] = new ShaderProgram.ShaderInfo(35633, vertShaderFile, -1);
        this.shaders[1] = new ShaderProgram.ShaderInfo(35632, fragShaderFile, -1);
        this.program = this.createShaders(gl);
    }

    public void releaseShaders(GL3 gl) {
        for(int i = 0; i < this.shaders.length; ++i) {
            if(this.shaders[i] != null && this.shaders[i].shader != 0) {
                gl.glDeleteShader(this.shaders[i].shader);
                this.shaders[i].shader = 0;
            }
        }

        gl.glDeleteProgram(this.program);
        this.program = 0;
    }

    public void enable(GL3 gl) {
        gl.glUseProgram(this.program);
        this.enabled = true;
    }

    public void disable(GL3 gl) {
        gl.glUseProgram(0);
        this.enabled = false;
    }

    public ShaderLocationsVault getAllShaderVariables(GL3 gl) {
        ShaderLocationsVault vault = new ShaderLocationsVault();
        IntBuffer numVars = IntBuffer.allocate(1);
        gl.glGetProgramiv(this.program, 35718, numVars);

        int i;
        IntBuffer length;
        for(i = 0; i < numVars.get(0); ++i) {
            length = IntBuffer.allocate(1);
            ByteBuffer size = ByteBuffer.allocate(80);
            gl.glGetActiveUniformName(this.program, i, 80, length, size);
            String type = new String(size.array(), 0, length.get(0));
            vault.add(type, Integer.valueOf(this.getUniformLocation(gl, type)));
        }

        gl.glGetProgramiv(this.program, 35721, numVars);

        for(i = 0; i < numVars.get(0); ++i) {
            length = IntBuffer.allocate(1);
            IntBuffer var10 = IntBuffer.allocate(1);
            IntBuffer var11 = IntBuffer.allocate(1);
            ByteBuffer nameVar = ByteBuffer.allocate(80);
            gl.glGetActiveAttrib(this.program, i, 80, length, var10, var11, nameVar);
            String name = new String(nameVar.array(), 0, length.get(0));
            vault.add(name, Integer.valueOf(this.getAttributeLocation(gl, name)));
        }

        return vault;
    }

    private int getUniformLocation(GL3 gl, String name) {
        boolean enabledStatus = this.enabled;
        if(!enabledStatus) {
            this.enable(gl);
        }

        int id = gl.glGetUniformLocation(this.program, name);
        if(!enabledStatus) {
            this.disable(gl);
        }

        return id;
    }

    private int getAttributeLocation(GL3 gl, String name) {
        boolean enabledStatus = this.enabled;
        if(!enabledStatus) {
            this.enable(gl);
        }

        int id = gl.glGetAttribLocation(this.program, name);
        if(!enabledStatus) {
            this.disable(gl);
        }

        return id;
    }

    private int createShaders(GL3 gl) throws FileNotFoundException, Exception {
        int shaderProgram = gl.glCreateProgram();

        for(int i = 0; i < this.shaders.length; ++i) {
            Scanner file = new Scanner(this.getClass().getClassLoader().getResourceAsStream(this.shaders[i].filename));
            IntBuffer compiled = IntBuffer.allocate(1);

            String source;
            String line;
            for(source = ""; file.hasNext(); source = source + "\n" + line) {
                line = file.nextLine();
            }

            file.close();
            this.shaders[i].shader = gl.glCreateShader(this.shaders[i].type);
            gl.glShaderSource(this.shaders[i].shader, 1, new String[]{source}, (IntBuffer)null);
            gl.glCompileShader(this.shaders[i].shader);
            gl.glGetShaderiv(this.shaders[i].shader, 35713, compiled);
            if(compiled.get(0) != 1) {
                boolean infologLen = false;
                boolean charsWritten = false;
                IntBuffer infoLogLen = IntBuffer.allocate(1);
                gl.glGetShaderiv(this.shaders[i].shader, 35716, infoLogLen);
                int size = infoLogLen.get(0);
                if(size > 0) {
                    ByteBuffer infoLog = ByteBuffer.allocate(size);
                    gl.glGetShaderInfoLog(this.shaders[i].shader, size, infoLogLen, infoLog);
                    System.err.print(new String(infoLog.array()));
                }

                this.releaseShaders(gl);
            }

            gl.glAttachShader(shaderProgram, this.shaders[i].shader);
        }

        IntBuffer linked = IntBuffer.allocate(1);
        gl.glLinkProgram(shaderProgram);
        gl.glGetProgramiv(shaderProgram, 35714, linked);
        if(linked.get(0) != 1) {
            return 0;
        } else {
            return shaderProgram;
        }
    }

    class ShaderInfo {
        int type;
        String filename;
        int shader;

        public ShaderInfo(int type, String filename, int shader) {
            this.type = type;
            this.filename = filename;
            this.shader = shader;
        }
    }
}
