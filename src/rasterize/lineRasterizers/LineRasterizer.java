package rasterize.lineRasterizers;

import constants.Constants;
import model.Line;
import model.Point;
import raster.RasterBufferedImage;

public abstract class LineRasterizer {
    protected RasterBufferedImage image;
    protected int color;

    public LineRasterizer(RasterBufferedImage image) {
        this.image = image;
        this.color = Constants.COLOR;
    }

    /** isPatternFill je povětšinou false, true pouze v případě vyplňování ScanLine algoritmem, kdy chceme vyplňovat vzorem */
    public void rasterize(int x1, int y1, int x2, int y2, boolean isPatternFill) {

    }

    public void rasterize(Point p1, Point p2, boolean isPatternFill) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY(), isPatternFill);
    }

    public void rasterize(Line line, boolean isPatternFill) {
        rasterize(line.getP1(), line.getP2(), isPatternFill);
    }

    public void setColor(int color) {
        this.color = color;
    }
}
