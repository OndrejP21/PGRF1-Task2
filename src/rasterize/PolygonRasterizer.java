package rasterize;

import constants.Constants;
import controller.LineRasterizerController;
import model.Polygon;
import model.Rectangle;
import rasterize.lineRasterizers.LineRasterizer;

public class PolygonRasterizer {

    private LineRasterizerController controller;
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public PolygonRasterizer(LineRasterizerController controller) {
        this.controller = controller;
    }

    public void rasterize(Polygon polygon, boolean isLast) {
        // pokud se jedná o obdélník, stačí dva body pro začátek rasterizace => pro základnu
        int minPoints = polygon instanceof Rectangle ? 2 : 3;

        if (polygon.getSize() >= minPoints) {
            for (int i = 0; i < polygon.getSize(); i++) {
                // možnost zadat rasterizer jak pomocí controlleru, tak lineRasterizeru
                LineRasterizer rasterizer = (controller != null ? controller.getRasterizer() : lineRasterizer);

                // Pokud se jedná o poslední polygon, změníme barvu na červenou, poté zresetujeme
                if (isLast) rasterizer.setColor(Constants.RED_COLOR);

                rasterizer.rasterize(polygon.getPoint(i), polygon.getPoint(i == polygon.getSize() - 1 ? 0 : i + 1), false);

                rasterizer.setColor(Constants.COLOR);
            }
        }
    }
}
