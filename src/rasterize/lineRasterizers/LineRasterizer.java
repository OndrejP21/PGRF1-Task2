package rasterize.lineRasterizers;

import model.Line;
import model.Point;
import raster.RasterBufferedImage;

public abstract class LineRasterizer {
    protected RasterBufferedImage image;

    public LineRasterizer(RasterBufferedImage image) {
        this.image = image;
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

    public void rasterize(Point p1, Point p2) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public void rasterize(Line line) {
        rasterize(line.getP1(), line.getP2());
    }
}
