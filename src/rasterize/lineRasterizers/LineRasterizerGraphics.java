package rasterize.lineRasterizers;

import raster.RasterBufferedImage;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer{
    public LineRasterizerGraphics(RasterBufferedImage image) {
        super(image);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, boolean isPatternFill) {
        Graphics g = image.getImage().getGraphics();
        g.drawLine(x1, y1, x2, y2);
    }

}
