package rasterize.lineRasterizers;

import algorithm.PointsAlgorithm;
import constants.Constants;
import model.MidPointModel;
import model.Point;
import raster.RasterBufferedImage;
import java.util.*;

/** Midpoint algoritmus
 * algoritmus pro vykreslení úsečky metodou půlení úsečky a vykeslení prostředního bodu v půlení
 * Výhoda: velmi jednoduchá implementace pomocí rekurze =>
 * rekurze je však neefektivní => lepší přepsat pomocí Stack nebo Queue, i tak však jednoduché
 * Nevýhoda: bez anti-aliasingu, často úsečka připomíná schody
 * */
public class LineRasterizerMidpoint extends LineRasterizer{
    public LineRasterizerMidpoint(RasterBufferedImage image) {
        super(image);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, boolean isPatternFill) {
        super.rasterize(x1, y1, x2, y2, isPatternFill);

        Deque<MidPointModel> stack = new ArrayDeque<>();

        stack.push(new MidPointModel(new Point(x1, y1), new Point(x2, y2)));

        while (!stack.isEmpty()) {
            MidPointModel points = stack.pop();

            // Jednotlivé body
            int p1x = points.getPoint1().getX();
            int p1y = points.getPoint1().getY();
            int p2x = points.getPoint2().getX();
            int p2y = points.getPoint2().getY();

            // Poloviny
            int sx = (p1x + p2x) / 2;
            int sy = (p1y + p2y) / 2;

            int useColor = isPatternFill ? PointsAlgorithm.getPatternColor(sx, sy) : this.color;

            this.image.setPixel(sx, sy, useColor);

            // Pokud rozdíl s krajními body jsou stále větší než 1 px, vykreslujeme dále
            if (Math.abs(sx - p1x) > 1 || Math.abs(sy - p1y) > 1) {
                stack.push(new MidPointModel(new Point(p1x, p1y), new Point(sx, sy)));
            }

            if (Math.abs(p2x - sx) > 1 || Math.abs(p2y - sy) > 1) {
                stack.push(new MidPointModel(new Point(sx, sy), new Point(p2x, p2y)));
            }
        }
    }
}
