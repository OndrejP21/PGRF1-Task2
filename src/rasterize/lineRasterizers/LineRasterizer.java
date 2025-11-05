package rasterize.lineRasterizers;

import algorithm.PointsAlgorithm;
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

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

    public void rasterize(Point p1, Point p2) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public void rasterize(Line line) {
        rasterize(line.getP1(), line.getP2());
    }
    public void rasterize(Line line, boolean isShiftHold) {
        Point[] points = PointsAlgorithm.getShiftedPoints(line.getP1().getX(), line.getP1().getY(), line.getP2().getX(), line.getP2().getY());

        if (isShiftHold)
            rasterize(points[0], points[1]);
        else
            rasterize(line);
    }

    public void setColor(int color) {
        this.color = color;
    }
}
