package raster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.OptionalInt;

public class RasterBufferedImage implements Raster {
    private final BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (x < this.image.getWidth() && x > 0 && y < this.image.getHeight() && y > 0)
            this.image.setRGB(x, y, color);
    }

    @Override
    public OptionalInt getPixel(int x, int y) {
        if (x < this.image.getWidth() && x > 0 && y < this.image.getHeight() && y > 0)
            return OptionalInt.of(this.image.getRGB(x, y));

        return OptionalInt.empty();
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getWidth() {
        return this.image.getWidth();
    }

    @Override
    public int getHeight() {
        return this.image.getHeight();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
    }
}
