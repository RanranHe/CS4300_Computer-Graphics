//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light {
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private Vector4f position;
    private Vector4f spotDirection;
    private float spotCutoff;

    public Light() {
        this.ambient = new Vector3f(0.0F, 0.0F, 0.0F);
        this.diffuse = new Vector3f(0.0F, 0.0F, 0.0F);
        this.specular = new Vector3f(0.0F, 0.0F, 0.0F);
        this.position = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        this.spotDirection = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
        this.spotCutoff = 0.0F;
    }

    public Light(Light l) {
        this.ambient = new Vector3f(l.ambient);
        this.diffuse = new Vector3f(l.diffuse);
        this.specular = new Vector3f(l.specular);
        this.position = new Vector4f(l.position);
        this.spotDirection = new Vector4f(l.spotDirection);
        this.spotCutoff = l.spotCutoff;
    }

    public void setAmbient(float r, float g, float b) {
        this.ambient = new Vector3f(r, g, b);
    }

    public void setDiffuse(float r, float g, float b) {
        this.diffuse = new Vector3f(r, g, b);
    }

    public void setSpecular(float r, float g, float b) {
        this.specular = new Vector3f(r, g, b);
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector4f(x, y, z, 1.0F);
    }

    public void setDirection(float x, float y, float z) {
        this.position = new Vector4f(x, y, z, 0.0F);
    }

    public void setSpotDirection(float x, float y, float z) {
        this.spotDirection = new Vector4f(x, y, z, 0.0F);
    }

    public void setAmbient(Vector3f amb) {
        this.ambient = new Vector3f(amb);
    }

    public void setDiffuse(Vector3f diff) {
        this.diffuse = new Vector3f(diff);
    }

    public void setSpecular(Vector3f spec) {
        this.specular = new Vector3f(spec);
    }

    public void setSpotAngle(float angle) {
        this.spotCutoff = angle;
    }

    public void setPosition(Vector4f pos) {
        this.position = new Vector4f(pos);
    }

    public Vector3f getAmbient() {
        return new Vector3f(this.ambient);
    }

    public Vector3f getDiffuse() {
        return new Vector3f(this.diffuse);
    }

    public Vector3f getSpecular() {
        return new Vector3f(this.specular);
    }

    public Vector4f getPosition() {
        return new Vector4f(this.position);
    }

    public Vector4f getSpotDirection() {
        return new Vector4f(this.spotDirection);
    }

    public float getSpotCutoff() {
        return this.spotCutoff;
    }
}
