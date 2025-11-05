package raster;

import java.awt.image.BufferedImage;
import java.util.OptionalInt;

public interface Raster {
    BufferedImage image = null;
    void setPixel(int x, int y, int color);
    OptionalInt getPixel(int x, int y);
    int getWidth();
    int getHeight();
    void clear();
}
