package fill;

import model.Line;
import model.Point;
import model.Polygon;
import raster.RasterBufferedImage;
import rasterize.PolygonRasterizer;
import rasterize.lineRasterizers.LineRasterizer;
import rasterize.lineRasterizers.LineRasterizerTrivial;

import java.util.*;


public class ScanLineFiller implements Filler {

    private LineRasterizer rasterizer;
    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

   /** Připravující konstruktor, je potřeba později nastavit property */
    public ScanLineFiller(RasterBufferedImage raster) {
        this.polygon = new Polygon();
        this.rasterizer = new LineRasterizerTrivial(raster);
        this.polygonRasterizer = new PolygonRasterizer(this.rasterizer);
    }

    public ScanLineFiller(Polygon polygon, PolygonRasterizer polygonRasterizer, LineRasterizer rasterizer) {
        this.polygon = polygon;
        this.rasterizer = rasterizer;
        this.polygonRasterizer = polygonRasterizer;
    }

    public void fill(boolean isPatternFill) {
        if (polygon.getSize() < 3) return;

        // Vyfiltrujeme hrany, které jsou horizontální a poté všechny zorientujeme
        List<Line> filteredLines = polygon.getLines().stream().filter((x) -> !x.isHorizontal()).peek(Line::orientate).toList();

        // Pokud neexistují žádné hrany => konec
        if (filteredLines.isEmpty()) return;

        // Najdeme nejmenší
        Optional<Line> firstLine = filteredLines.stream().min(Comparator.comparing(line -> Math.min(line.getP1().getY(), line.getP2().getY())));

        // Najdeme největší
        Optional<Line> lastLine = filteredLines.stream().max(Comparator.comparing(line -> Math.max(line.getP1().getY(), line.getP2().getY())));

        int minY = Math.min(firstLine.get().getP1().getY(), firstLine.get().getP2().getY());
        int maxY = Math.max(lastLine.get().getP1().getY(), lastLine.get().getP2().getY());

        for (int y = minY; y < maxY; y++) {
            List<Point> intersectionPoints = new ArrayList<>();

            // Projdeme všechny hrany a hledáme průsečíky
            for (Line line : filteredLines) {
                Point p1 = line.getP1();
                Point p2 = line.getP2();
                int x1 = p1.getX();
                int y1 = p1.getY();
                int x2 = p2.getX();
                int y2 = p2.getY();

                // Po orientaci musíme vybrat, který bod je menší a který větší
                int yLow  = Math.min(y1, y2);
                int yHigh = Math.max(y1, y2);

                if (yLow <= y && y < yHigh) {
                    int deltay = y2 - y1;
                    int deltax = x2 - x1;

                    int x;

                    // Vertikální hrana
                    if (deltax == 0) {
                        x = x1;
                    } else {
                        float k = deltay / (float) deltax;
                        float q = y1 - k * x1;

                        x = Math.round((y - q) / k);
                    }

                    intersectionPoints.add(new Point(x, y));
                }
            }

            // Pokud je lichý počet, nepracujeme
            if (intersectionPoints.size()%2 != 0) continue;

            // Seřadíme všechny body od nejmenšího zleva doprava
            List<Point> sortedIntersectionPoints = intersectionPoints.stream().sorted(Comparator.comparing(Point::getX)).toList();

            for (int i = 0; i < sortedIntersectionPoints.size(); i+=2) {
                rasterizer.rasterize(sortedIntersectionPoints.get(i), sortedIntersectionPoints.get(i + 1), isPatternFill);
            }
        }

        // Obtáhnout LineRasterizer hranu
        this.polygonRasterizer.rasterize(polygon, false);
    }

    /** Změna hodnot pro rasterizaci polygonu */
    public void ChangeSettings(Polygon polygon, PolygonRasterizer polygonRasterizer, LineRasterizer rasterizer) {
        this.polygon = polygon;
        this.rasterizer = rasterizer;
        this.polygonRasterizer = polygonRasterizer;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
