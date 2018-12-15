//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import org.joml.Vector4f;

public class Material {
    private Vector4f emission;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private float shininess;
    private float absorption;
    private float reflection;
    private float transparency;
    private float refractive_index;

    public Material() {
        this.emission = new Vector4f();
        this.ambient = new Vector4f();
        this.diffuse = new Vector4f();
        this.specular = new Vector4f();
        this.init();
    }

    public Material(Material mat) {
        this.emission = new Vector4f(mat.getEmission());
        this.ambient = new Vector4f(mat.getAmbient());
        this.diffuse = new Vector4f(mat.getDiffuse());
        this.specular = new Vector4f(mat.getSpecular());
        this.setShininess(mat.getShininess());
        this.setAbsorption(mat.getAbsorption());
        this.setReflection(mat.getReflection());
        this.setTransparency(mat.getTransparency());
        this.setRefractiveIndex(mat.getRefractiveIndex());
    }

    public void init() {
        this.setEmission(0.0F, 0.0F, 0.0F);
        this.setAmbient(0.0F, 0.0F, 0.0F);
        this.setDiffuse(0.0F, 0.0F, 0.0F);
        this.setSpecular(0.0F, 0.0F, 0.0F);
        this.setShininess(0.0F);
        this.setAbsorption(1.0F);
        this.setReflection(0.0F);
        this.setTransparency(0.0F);
    }

    public void setEmission(float r, float g, float b) {
        this.emission.x = r;
        this.emission.y = g;
        this.emission.z = b;
        this.emission.w = 1.0F;
    }

    public void setEmission(Vector4f v) {
        this.emission = new Vector4f(v);
    }

    public void setAmbient(float r, float g, float b) {
        this.ambient.x = r;
        this.ambient.y = g;
        this.ambient.z = b;
        this.ambient.w = 1.0F;
    }

    public void setAmbient(Vector4f v) {
        this.ambient = new Vector4f(v);
    }

    public void setDiffuse(float r, float g, float b) {
        this.diffuse.x = r;
        this.diffuse.y = g;
        this.diffuse.z = b;
        this.diffuse.w = 1.0F;
    }

    public void setDiffuse(Vector4f v) {
        this.diffuse = new Vector4f(v);
    }

    public void setSpecular(float r, float g, float b) {
        this.specular.x = r;
        this.specular.y = g;
        this.specular.z = b;
        this.specular.w = 1.0F;
    }

    public void setSpecular(Vector4f v) {
        this.specular = new Vector4f(v);
    }

    public void setShininess(float r) {
        this.shininess = r;
    }

    public void setAbsorption(float a) {
        this.absorption = a;
    }

    public void setReflection(float r) {
        this.reflection = r;
    }

    public void setTransparency(float t) {
        this.transparency = t;
        this.ambient.w = this.diffuse.w = this.specular.w = this.emission.w = 1.0F - t;
    }

    public void setRefractiveIndex(float r) {
        this.refractive_index = r;
    }

    public Vector4f getEmission() {
        return this.emission;
    }

    public Vector4f getAmbient() {
        return this.ambient;
    }

    public Vector4f getDiffuse() {
        return this.diffuse;
    }

    public Vector4f getSpecular() {
        return this.specular;
    }

    public float getShininess() {
        return this.shininess;
    }

    public float getAbsorption() {
        return this.absorption;
    }

    public float getReflection() {
        return this.reflection;
    }

    public float getTransparency() {
        return this.transparency;
    }

    public float getRefractiveIndex() {
        return this.refractive_index;
    }
}
