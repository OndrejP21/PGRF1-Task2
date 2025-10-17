package rasterize;

import controller.LineRasterizerController;
import enums.LineRasterizerType;
import model.Polygon;
import rasterize.lineRasterizers.LineRasterizer;

public class PolygonRasterizer {

    private LineRasterizerController controller;

    public PolygonRasterizer(LineRasterizerController controller) {
        this.controller = controller;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getSize() >= 3) {
            for (int i = 0; i < polygon.getSize(); i++) {
                controller.getRasterizer().rasterize(polygon.getPoint(i), polygon.getPoint(i == polygon.getSize() - 1 ? 0 : i + 1));
            }
        }
    }
}
