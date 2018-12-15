package Fans;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

import util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by ashesh on 9/18/2015.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly
 * encapsulates all our OpenGL functionality from the rest of Java GUI, managed
 * by the JOGLFrame class.
 */
public class View {
  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  private Matrix4f proj;
  Stack<Matrix4f> modelview;
  private util.ObjectInstance meshObject;
  private util.ObjectInstance meshObject2;
  private util.Material material;

  private util.ShaderProgram program;
  float angleOfRotation;
  private ShaderLocationsVault shaderLocations;
  public View() {
	proj = new Matrix4f();
	proj.identity();

	modelview = new Stack<Matrix4f>();

	angleOfRotation = 0;
  }

  private void initObjects(GL3 gl) throws FileNotFoundException
  {
	util.PolygonMesh tmesh;

	InputStream in;

	in = new FileInputStream("models/sphere.obj");

	tmesh = util.ObjImporter.importFile(new VertexAttribProducer(),in,true);

	Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();

	//currently there is only one per-vertex attribute: position
	shaderToVertexAttribute.put("vPosition", "position");


	meshObject2 = new util.ObjectInstance(gl,
			program,
			shaderLocations,
			shaderToVertexAttribute,
			tmesh,new
			String(""));

	Vector4f min = tmesh.getMinimumBounds();
	Vector4f max = tmesh.getMaximumBounds();


	util.Material mat =  new util.Material();

	mat.setAmbient(1,1,1);
	mat.setDiffuse(1,1,1);
	mat.setSpecular(1,1,1);

	material = mat;




  }

  public void init(GLAutoDrawable gla) throws Exception {
	GL3 gl = (GL3) gla.getGL().getGL3();

	// compile and make our shader program. Look at the ShaderProgram class
	// for details on how this is done
	program = new ShaderProgram();
	program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

	shaderLocations = program.getAllShaderVariables(gl);

	//initObjects(gl);

	List<Vector4f> positions = new ArrayList<>();
	int SLICES = 100;
	for(int i=0;i<SLICES;i++){
	  float theta = (float)(i*2*Math.PI/SLICES);
	  positions.add(new Vector4f((float)Math.cos(theta),0,(float)Math.sin(theta),1));
	}
	positions.add(new Vector4f(1,0,0,1));

	List<Vector4f> colors = new ArrayList<Vector4f>();
	colors.add(new Vector4f(1, 1, 1, 1));

	List<IVertexData> vertexData = new ArrayList<>();
	VertexAttribWithColorProducer producer = new VertexAttribWithColorProducer();
	for(Vector4f pos:positions){
	  IVertexData v = producer.produce();
	  v.setData("position", new float[]{pos.x,pos.y,pos.z,pos.w});
	  v.setData("color", new float[] { colors.get(0).x, colors.get(0).y, colors.get(0).z, colors.get(0).w });
	  vertexData.add(v);
	}

	List<Integer> indices = new ArrayList<>();
	for(int i=0;i<positions.size();i++){
	  indices.add(i);
	}

	PolygonMesh<IVertexData> mesh;
	mesh = new PolygonMesh<IVertexData>();
	mesh.setVertexData(vertexData);
	mesh.setPrimitives(indices);
	mesh.setPrimitiveType(GL.GL_LINES);
	mesh.setPrimitiveSize(2);

	Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();

	shaderToVertexAttribute.put("vPosition", "position");
	shaderToVertexAttribute.put("vColor", "color");
	meshObject = new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh, "circles");

	initObjects(gl);

  }

  public void draw(GLAutoDrawable gla) {
	angleOfRotation += 0.02;

	GL3 gl = gla.getGL().getGL3();
	FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
	gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	program.enable(gl);

	modelview.push(new Matrix4f());
	modelview.peek().lookAt(new Vector3f(0, 0, 2000), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
	gl.glUniformMatrix4fv(shaderLocations.getLocation("projection"), 1, false, proj.get(fb16));

	//sun------------------------------------------------------------
	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().scale(70,70,70);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"),1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{0.945f,0.918f,0.251f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_COLOR); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();
	//------------------------------------------------------------
	//mercury------------------------------------------------------------
	modelview.push(new Matrix4f(modelview.peek()));
	float phi = (float) Math.toRadians(30);
	float theta = (float) Math.toRadians(30);
	modelview.peek().rotate(theta,0f,1f,0f).rotate(phi,0f,0f,1f);
	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(3*angleOfRotation),0f,1f,0f).scale(200f,200f,200f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(3*angleOfRotation),0f,1f,0f).translate(200f,0f,0f).scale(10f,10f,10f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{0.965f,0.624f,0.690f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_COLOR); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();
	modelview.pop();
	//------------------------------------------------------------
	//venus------------------------------------------------------------
	modelview.push(new Matrix4f(modelview.peek()));
	phi = (float) Math.toRadians(10);
	theta = (float) Math.toRadians(335);
	modelview.peek().rotate(theta,0f,1f,0f).rotate(phi,0f,0f,1f);
	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(5*angleOfRotation),0f,1f,0f).scale(300f,300f,300f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(5*angleOfRotation),0f,1f,0f).translate(300f,0f,0f).scale(20f,20f,20f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{0.965f,0.949f,0.604f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();
	modelview.pop();
	//------------------------------------------------------------
	//earth------------------------------------------------------------
	modelview.push(new Matrix4f(modelview.peek()));
	phi = (float) Math.toRadians(-60);
	theta = (float) Math.toRadians(50);
	modelview.peek().rotate(theta,0f,1f,0f).rotate(phi,0f,0f,1f);
	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(7*angleOfRotation),0f,1f,0f).scale(550f,550f,550f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(7*angleOfRotation),0f,1f,0f).translate(550f,0f,0f).scale(40f,40f,40f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{0.557f,0.557f,0.776f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(7*angleOfRotation),0f,1f,0f).translate(550f,0f,0f)
			.rotate((float)Math.toRadians(10),0f,1f,0f)
			.rotate((float)Math.toRadians(20),0f,0f,1f).scale(100f,100f,100f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(7*angleOfRotation),0f,1f,0f).translate(550f,0f,0f)
			.rotate((float)Math.toRadians(10),0f,1f,0f)
			.rotate((float)Math.toRadians(20),0f,0f,1f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(100f,0f,0f).scale(20f,20f,20f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();
	modelview.pop();
	//------------------------------------------------------------
	//jupiter------------------------------------------------------------
	modelview.push(new Matrix4f(modelview.peek()));
	phi = (float) Math.toRadians(60);
	theta = (float) Math.toRadians(20);
	modelview.peek().rotate(theta,0f,1f,0f).rotate(phi,0f,0f,1f);
	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).scale(900f,900f,900f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(900f,0f,0f).scale(60f,60f,60f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{0.961f,0.592f,0.224f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(900f,0f,0f)
			.rotate((float)Math.toRadians(50),0f,1f,0f)
			.rotate((float)Math.toRadians(-80),0f,0f,1f).scale(120f,120f,120f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(900f,0f,0f)
			.rotate((float)Math.toRadians(50),0f,1f,0f)
			.rotate((float)Math.toRadians(-80),0f,0f,1f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(120f,0f,0f).scale(20f,20f,20f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(900f,0f,0f)
			.rotate((float)Math.toRadians(100),0f,1f,0f)
			.rotate((float)Math.toRadians(30),0f,0f,1f).scale(120f,120f,120f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_LINE_STRIP, GL3.GL_LINE_STRIP);
	meshObject.draw(gla);
	modelview.pop();

	modelview.push(new Matrix4f(modelview.peek()));
	modelview.peek().rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(900f,0f,0f)
			.rotate((float)Math.toRadians(100),0f,1f,0f)
			.rotate((float)Math.toRadians(30),0f,0f,1f).rotate((float)Math.toRadians(10*angleOfRotation),0f,1f,0f).translate(120f,0f,0f).scale(20f,20f,20f);
	gl.glUniformMatrix4fv(shaderLocations.getLocation("modelview"), 1, false, modelview.peek().get(fb16));
	gl.glUniform4fv(shaderLocations.getLocation("vColor"),1, FloatBuffer.wrap(new float[]{1f,1f,1f,1f}));
	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
	meshObject2.draw(gla);
	modelview.pop();

	modelview.pop();
	//------------------------------------------------------------
	modelview.pop();
	gl.glFlush();
	program.disable(gl);
  }

  // this method is called from the JOGLFrame class, everytime the window
  // resizes
  public void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
	GL gl = gla.getGL();
	WINDOW_WIDTH = width;
	WINDOW_HEIGHT = height;
	gl.glViewport(0, 0, width, height);

	proj = new Matrix4f().perspective((float) Math.toRadians(60.0f), (float) width / height, 0.1f, 10000.0f);
	// proj = new Matrix4f().ortho(-400,400,-400,400,0.1f,10000.0f);

  }

  public void dispose(GLAutoDrawable gla) {
	meshObject.cleanup(gla);
	meshObject2.cleanup(gla);
  }
}
