//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.joml.Vector4f;

public class TextureImage {
    private BufferedImage image;
    private String name;
    private Texture texture;

    public TextureImage(String filepath, String imageFormat, String name) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filepath);
        this.texture = TextureIO.newTexture(in, true, imageFormat);
        in = this.getClass().getClassLoader().getResourceAsStream(filepath);
        this.image = ImageIO.read(in);
        this.name = new String(name);
    }

    public Texture getTexture() {
        return this.texture;
    }

    public String getName() {
        return this.name;
    }

    Vector4f getColor(float x, float y) {
        x -= (float)((int)x);
        y -= (float)((int)y);
        int x1 = (int)(x * (float)this.image.getWidth());
        int y1 = (int)(y * (float)this.image.getHeight());
        x1 = (x1 + this.image.getWidth()) % this.image.getWidth();
        y1 = (y1 + this.image.getHeight()) % this.image.getHeight();
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        if(x2 >= this.image.getWidth()) {
            x2 = this.image.getWidth() - 1;
        }

        if(y2 >= this.image.getHeight()) {
            y2 = this.image.getHeight() - 1;
        }

        Vector4f one = this.ColorToVector4f(new Color(this.image.getRGB(x1, y1)));
        Vector4f two = this.ColorToVector4f(new Color(this.image.getRGB(x2, y1)));
        Vector4f three = this.ColorToVector4f(new Color(this.image.getRGB(x1, y2)));
        Vector4f four = this.ColorToVector4f(new Color(this.image.getRGB(x2, y2)));
        Vector4f inter1 = one.lerp(three, y - (float)y1);
        Vector4f inter2 = two.lerp(four, y - (float)y1);
        Vector4f inter3 = inter1.lerp(inter2, x - (float)x1);
        return inter3;
    }

    private Vector4f ColorToVector4f(Color c) {
        return new Vector4f((float)c.getRed() / 255.0F, (float)c.getGreen() / 255.0F, (float)c.getBlue() / 255.0F, (float)c.getAlpha() / 255.0F);
    }
}
