package algorithm;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.*;

/** Pomocná třída se statickými metodami */
public class PointsAlgorithm {
    /** Metoda pro získání nejbližšího bodu
     * radiusPx je nastaven defaultně na velikost okna */
    public static Point findNearestPoint(int mx, int my, int radiusPx, List<Line> lines, Polygon polygon) {
        Point nearest = null;
        int bestDist2 = radiusPx;

        for (Line L : lines) {
            Point a = L.getP1();
            Point b = L.getP2();

            int da2 = dist(mx, my, a.getX(), a.getY());
            if (da2 <= bestDist2) {
                bestDist2 = da2; nearest = a;
            }

            int db2 = dist(mx, my, b.getX(), b.getY());
            if (db2 <= bestDist2) {
                bestDist2 = db2; nearest = b;
            }
        }

        for (int i = 0; i < polygon.getSize(); i++) {
            Point p = polygon.getPoint(i);
            int dp2 = dist(mx, my, p.getX(), p.getY());
            if (dp2 <= bestDist2) {
                bestDist2 = dp2; nearest = p;
            }
        }

        return nearest;
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

    /** Kontrola, zda je bod oproti bodu pořád v dané maximální vzdálenosti */
    public static boolean isPointsInMaxDistance(Point p1, Point p2, int size) {
        int x = Math.abs(p1.getX() - p2.getX());
        int y = Math.abs(p1.getY() - p2.getY());

        return x < size || y < size;
    }

}
