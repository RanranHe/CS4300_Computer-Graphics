package sgraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import util.Light;
import util.Material;
import util.PolygonMesh;
import util.TextureImage;

/**
 * Created by ashesh on 4/12/2016.
 */
public class RTScenegraphRenderer implements IScenegraphRenderer {
    private List<Light> lights;
    private Color color, refractionColor;
    /**
     * A map to store all the textures
     */
    private Map<String, TextureImage> textures;


    public RTScenegraphRenderer() {

        textures = new HashMap<String,TextureImage>();
        refractionColor = new Color(0,0,0);
    }

    @Override
    public void setContext(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException("Not valid for this renderer");
    }

    @Override
    public void initShaderProgram(util.ShaderProgram shaderProgram, Map<String, String> shaderVarsToVertexAttribs) {
        throw new IllegalArgumentException("Not valid for this renderer");

    }

    @Override
    public int getShaderLocation(String name) {
        throw new IllegalArgumentException("Not valid for this renderer");

    }

    @Override
    public void addMesh(String name, PolygonMesh mesh) throws Exception {

    }

    public void initLightsInShader(List<Light> lights) {
        throw new IllegalArgumentException("Not valid for this renderer");
    }

    @Override
    public void draw(INode root, Stack<Matrix4f> modelView) {
        int i,j;
        int width = 800;
        int height = 800;
        float FOVY = 120.0f;
        Ray rayView = new Ray();

        this.lights = root.getLightsInView(modelView);

        BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        rayView.start = new Vector4f(0,0,0,1);
        for (i=0;i<width;i++)
        {
            for (j=0;j<height;j++)
            {
                /*
                 create ray in view coordinates
                 start point: 0,0,0 always!
                 going through near plane pixel (i,j)
                 So 3D location of that pixel in view coordinates is
                 x = i-width/2
                 y = j-height/2
                 z = -0.5*height/tan(FOVY)
                */
                rayView.direction = new Vector4f(i-0.5f*width,
                        j-0.5f*height,
                        -0.5f*height/(float)Math.tan(Math.toRadians(0.5*FOVY)),
                        0.0f);

                HitRecord hitR = new HitRecord();
                this.color = new Color(0,0,0);
                mainRayCast(rayView, root, modelView, hitR);
                output.setRGB(i,height-1-j,this.color.getRGB());
            }
        }

        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream("output/raytrace.png");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not write raytraced image!");
        }

        try {
            ImageIO.write(output,"png",outStream);
            outStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not write raytraced image!");
        }

    }

    private void mainRayCast(Ray rayView,INode root, Stack<Matrix4f> modelView, HitRecord hitR) {
        raycast(rayView,root,modelView,hitR);
        this.color = getRaytracedColor(hitR, root, modelView, rayView);

    }

    private void refractionRayCast(Ray rayView,INode root, Stack<Matrix4f> modelView, HitRecord
      hitR, int count) {
        if (count < 5) {
            raycast(rayView,root,modelView,hitR);
            count++;
            this.refractionColor = getRefractionColor(hitR, root, modelView, rayView, count);
        }
    }

    private Color getRefractionColor(HitRecord hitRecord, INode root, Stack<Matrix4f> stack, Ray
      rayView, int count) {
        if (hitRecord.intersected()) {
            return shade(hitRecord.point,hitRecord.normal,hitRecord.material, hitRecord.texture,
              root, stack, rayView, count, 1);
        }
        else {
            return new Color(0,0,0);
        }

    }

    private void raycast(Ray rayView,INode root,Stack<Matrix4f> modelView,HitRecord hitRecord) {
        root.intersect(rayView,modelView,hitRecord, textures);

    }

    private Color getRaytracedColor(HitRecord hitRecord, INode root, Stack<Matrix4f> stack, Ray
                                    rayView) {
        if (hitRecord.intersected())
            return shade(hitRecord.point,hitRecord.normal,hitRecord.material, hitRecord.texture,
              root, stack, rayView, 0, 1);
        else
            return new Color(0,0,0);
    }

    private boolean rayCastCount(Ray outRay, Stack<Matrix4f>stack, Vector4f reference,
                 int count, float reflection, INode root, float refractionRef) {
        if (count >5) {
            return false;
        }
        HitRecord hitRecord = new HitRecord();
        root.intersect(outRay, stack, hitRecord, textures);
        boolean isHit = hitRecord.intersected();

        if (isHit) {
            count = count+1;
            Color shadeColor = shade(hitRecord.point, hitRecord.normal, hitRecord.material,
              hitRecord.texture, root, stack, outRay, count, refractionRef);
            Vector4f colorIn4f = new Vector4f((float) shadeColor.getRed()/255, (float) shadeColor
              .getGreen()/255, (float) shadeColor.getBlue()/255, 1);

            reference.add(colorIn4f.x*reflection, colorIn4f.y*reflection,
              colorIn4f.z*reflection, 0);

        }
        return isHit;
    }

    private Color shade(Vector4f point,Vector4f normal,Material material, Vector4f texture, INode
     root, Stack<Matrix4f> stack, Ray viewRay, int count, float refractionRef) {
        Vector3f color = new Vector3f(0,0,0);

        for (int i=0;i<lights.size();i++)
        {

            Vector3f lightVec;
            Vector3f spotdirection = new Vector3f(
                    lights.get(i).getSpotDirection().x,
                    lights.get(i).getSpotDirection().y,
                    lights.get(i).getSpotDirection().z);


            if (spotdirection.length()>0)
                spotdirection = spotdirection.normalize();

            if (lights.get(i).getPosition().w!=0) {
                lightVec = new Vector3f(
                        lights.get(i).getPosition().x - point.x,
                        lights.get(i).getPosition().y - point.y,
                        lights.get(i).getPosition().z - point.z);
            }
            else
            {
                lightVec = new Vector3f(
                        -lights.get(i).getPosition().x,
                        -lights.get(i).getPosition().y,
                        -lights.get(i).getPosition().z);
            }
            lightVec = lightVec.normalize();


        /* if point is not in the light cone of this light, move on to next light */
            if (new Vector3f(lightVec).negate().dot(spotdirection)<=Math.cos(Math.toRadians(lights.get(i).getSpotCutoff())))
                continue;


            Vector3f normalView = new Vector3f(normal.x,normal.y,normal.z).normalize();

            float nDotL = normalView.dot(lightVec);

          /**
           * shadow
           */
            boolean notSee = false;
            if (lights.get(i).getPosition().w !=0) {
                Vector4f p = new Vector4f(point);
                Vector4f v = new Vector4f(new Vector3f(lightVec), 0);
                p = p.add(new Vector4f(v).mul(0.1f));

                HitRecord hr = new HitRecord();
                root.intersect(new Ray(p, v), stack, hr, this.textures);
                boolean hasHit = hr.intersected();

                if (hasHit) {
                    notSee = true;
                }
            }
            else {
                Vector4f p = new Vector4f(point);
                Vector4f v = new Vector4f(new Vector3f(lightVec), 0);
                p = p.add(new Vector4f(v).mul(0.1f));

                HitRecord hr = new HitRecord(); // Empty hitrecord
                root.intersect(new Ray(p, v), stack, hr, this.textures);
                boolean hasHit = hr.intersected();

                float dis = lights.get(i).getPosition().distance(new Vector4f(p));

                if (hasHit && dis - hr.time> 0.0001f) {
                    notSee = true;
                }
            }
            if (notSee) {
                continue;
            }

            Vector3f viewVec = new Vector3f(point.x,point.y,point.z).negate();
            viewVec = viewVec.normalize();

            Vector3f reflectVec = new Vector3f(lightVec).negate().reflect(normalView);
            reflectVec = reflectVec.normalize();

            float rDotV = Math.max(reflectVec.dot(viewVec),0.0f);

            Vector3f ambient = new Vector3f(
              material.getAmbient().x * lights.get(i).getAmbient().x,
              material.getAmbient().y * lights.get(i).getAmbient().y,
              material.getAmbient().z * lights.get(i).getAmbient().z);

            Vector3f diffuse = new Vector3f(
              material.getDiffuse().x * lights.get(i).getDiffuse().x * Math.max(nDotL,0),
              material.getDiffuse().y * lights.get(i).getDiffuse().y * Math.max(nDotL,0),
              material.getDiffuse().z * lights.get(i).getDiffuse().z * Math.max(nDotL,0));
            Vector3f specular;
            if (nDotL>0) {
                specular = new Vector3f(
                  material.getSpecular().x * lights.get(i).getSpecular().x * (float) Math.pow(rDotV, material.getShininess()),
                  material.getSpecular().y * lights.get(i).getSpecular().y * (float) Math.pow(rDotV, material.getShininess()),
                  material.getSpecular().z * lights.get(i).getSpecular().z * (float) Math.pow(rDotV, material.getShininess()));
            }
            else
            {
                specular = new Vector3f(0,0,0);
            }
            color = new Vector3f(color).add(ambient).add(diffuse).add(specular);
        }

      /**
       * texture
       */
        Vector3f textureMul = new Vector3f(texture.x, texture.y, texture.z);
        color = color.mul(textureMul);

        float absorption = material.getAbsorption();
        if (absorption >1 || absorption < 0) {
            absorption = 1;
        }
        color = color.mul(absorption);

      /**
       * reflection
       */
        float reflection = material.getReflection();
        if (reflection > 1 || reflection < 0) {
            reflection = 0;
        }
        Vector4f reference = new Vector4f(0, 0, 0, 1);
        if (reflection != 0) {
            Vector3f comeRayDir =
              new Vector3f(viewRay.direction.x, viewRay.direction.y, viewRay.direction.z);
            Vector4f reflectRay = new Vector4f(new Vector3f(comeRayDir).reflect
              (normal.x, normal.y, normal.z), 0);

            Ray outRay = new Ray(point.add(new Vector4f(reflectRay).mul(0.01f)), reflectRay);
            boolean isRayHit = rayCastCount(outRay, stack, reference,
              count, reflection, root, refractionRef);
        }
        color = color.add(new Vector3f(reference.x, reference.y, reference.z));

      /**
       * refraction
       */
        float transparency = material.getTransparency();
        Vector4f refraction = new Vector4f(0,0,0,1);
        if (transparency !=0) {
            Vector3f normalRef = new Vector3f(normal.x, normal.y, normal.z);
            Vector3f rayDir = new Vector3f(viewRay.direction.x, viewRay.direction.y, viewRay
              .direction.z);
            Vector3f refractionDir;
            float cosThetaI = -1f * normalRef.dot(rayDir);
            float sinThetaISqr = 1f - cosThetaI * cosThetaI;
            float glassInd = 1.33f;
            float ratio;
            if (Math.abs(refractionRef - 1f) < 0.0001f) {
                // from vacuum
                ratio = 1f / glassInd;
            } else {
                // from glass
                ratio = glassInd / 1f;
                normal.negate();
            }
            float sinThetaRSqr = ratio * ratio * sinThetaISqr;
            float cosThetaRSqr = 1f - sinThetaRSqr;
            if (cosThetaRSqr >= 0f) {
                float cosThetaR = (float) Math.sqrt(cosThetaRSqr);

                refractionDir = (new Vector3f(normalRef).mul(cosThetaI).add(rayDir)).mul(ratio)
                  .sub(new Vector3f(normalRef).mul(cosThetaR));

                Vector4f refractionDir4f = new Vector4f(refractionDir, 0).normalize();
                Ray outRay = new Ray(point.add(new Vector4f(refractionDir4f).mul(0.01f)),
                  refractionDir4f);
                int countRefraction = 0;
                if (Math.abs(refractionRef - 1f) < 0.0001f) {
                    // from vacuum to galss
                    HitRecord newHit = new HitRecord();
                    refractionRayCast(outRay, root, stack, newHit, count);
                    refraction = new Vector4f((float) this.refractionColor.getRed()/255,
                      (float) this.refractionColor.getGreen()/255, (float) this.refractionColor
                      .getBlue()/255, 1 );
                    refraction = new Vector4f(refraction.x*transparency, refraction
                      .y*transparency, refraction.z*transparency, 1);
                } else {
                    // from glass to vacuum
                    HitRecord newHit = new HitRecord();
                    refractionRayCast(outRay, root, stack, newHit, count);
                    refraction = new Vector4f((float) this.refractionColor.getRed()/255,
                      (float) this.refractionColor.getGreen()/255, (float) this.refractionColor
                      .getBlue()/255, 1 );
                    refraction = new Vector4f(refraction.x*transparency, refraction
                      .y*transparency, refraction.z*transparency, 1);
                }

            }

        }

        color = color.add(new Vector3f(refraction.x, refraction.y, refraction.z));



        if (color.x < 0) {
            color.x = 0;
        }
        else if (color.x > 1) {
            color.x= 1;
        }
        if (color.y < 0) {
            color.y = 0;
        }
        else if (color.y > 1) {
            color.y= 1;
        }
        if (color.z < 0) {
            color.z = 0;
        }
        else if (color.z > 1) {
            color.z= 1;
        }

        return new Color((int)(255*color.x),(int)(255*color.y),(int)(255*color.z));
    }

    @Override
    public void drawMesh(String name, Material material, String textureName, Matrix4f transformation) {
        throw new IllegalArgumentException("Not valid for this renderer");
    }

    @Override
    public void addTexture(String name,String path)
    {
        TextureImage image = null;
        String imageFormat = path.substring(path.indexOf('.')+1);
        try {
            image = new TextureImage(path,imageFormat,name);
        } catch (IOException e) {
            throw new IllegalArgumentException("Texture "+path+" cannot be read!");
        }
        textures.put(name,image);
    }

    @Override
    public void dispose() {

    }

    @Override
    public Map<String, TextureImage> getTextures() {
        return textures;
    }
}
