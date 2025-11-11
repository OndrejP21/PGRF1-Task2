package algorithm;

import constants.Constants;
import model.Line;
import model.Point;
import model.Polygon;
import model.PolygonPointModel;

import java.util.*;

/** Pomocná třída se statickými metodami */
public class PointsAlgorithm {
    /** Metoda pro získání nejbližšího bodu
     * radiusPx je nastaven defaultně na velikost okna */
    public static PolygonPointModel findNearestPoint(int mx, int my, int radiusPx, List<Line> lines, Polygon polygon, Polygon clipperPolygon, List<Polygon> polygons) {
        Point nearest = null;
        Polygon nearestPolygon = null;

        int bestDist2 = radiusPx;

        polygons.add(polygon);
        polygons.add(clipperPolygon);

        for (Polygon poly : polygons) {
            for (int i = 0; i < poly.getSize(); i++) {
                Point p = poly.getPoint(i);
                int dp2 = dist(mx, my, p.getX(), p.getY());
                if (dp2 <= bestDist2) {
                    bestDist2 = dp2; nearest = p; nearestPolygon = poly;
                }
            }
        }

        return nearest != null ? new PolygonPointModel(nearestPolygon, nearest) : null;
    }

    private static int dist(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2, dy = y1 - y2;
        return (int) Math.sqrt(dx*dx + dy*dy);
    }

    /** Omezení na rozsah okna */
    public static int clampX(int x, int width) {
        return Math.max(0, Math.min(width - 1, x));
    }

    /** Omezení na rozsah okna */
    public static int clampY(int y, int height) {
        return Math.max(0, Math.min(height - 1, y));
    }

    /** Metoda vrající data po zarovnání úsečky při držení shiftu */
    public static Point[] getShiftedPoints(int x1, int y1, int x2, int y2) {
        int dxRaw = x2 - x1;
        int dyRaw = y2 - y1;

        int dx = Math.abs(dxRaw);
        int dy = Math.abs(dyRaw);

        // cílový bod
        int sx = x2;
        int sy = y2;

        // svisle
        if (dx == 0) {
            sx = x1;
            // vodorovně
        } else if (dy == 0) {
            sy = y1;
        } else {
            double k = dy / (double) dx;
            if (k <= Math.tan(Math.toRadians(22.5))) { // čtvrtina
                // vodorovně
                sy = y1;
            } else if (k >= Math.tan(Math.toRadians(67.5))) {
                // svisle
                sx = x1;
            } else {
                // diagonálně (45°): zvolí kratší osu, pomocí funkce signum zajistí zachování kvadrantu
                int step = Math.min(dx, dy);
                sx = x1 + Integer.signum(dxRaw) * step;
                sy = y1 + Integer.signum(dyRaw) * step;
            }
        }

        return new Point[]{new Point(x1, y1), new Point(sx, sy)};
    }

    /** Metoda pro získání barvy z PATTERNU */
    public static int getPatternColor(int x, int y) {
        int modX = x % Constants.PATTERN[0].length;
        int modY = y % Constants.PATTERN.length;

        return Constants.PATTERN[modY][modX];
    }

}
