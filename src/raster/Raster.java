package raster;

import java.awt.image.BufferedImage;

public interface Raster {
    BufferedImage image = null;
    void setPixel(int x, int y, int color);
    int getColor(int x, int y);
    int getWidth();
    int getHeight();
    void clear();
}
