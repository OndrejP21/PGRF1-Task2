package rasterize;

import model.Polygon;
import rasterize.lineRasterizers.LineRasterizer;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getSize() >= 3) {
            for (int i = 0; i < polygon.getSize(); i++) {
                lineRasterizer.rasterize(polygon.getPoint(i), polygon.getPoint(i == polygon.getSize() - 1 ? 0 : i + 1));
            }
        }
    }
}
