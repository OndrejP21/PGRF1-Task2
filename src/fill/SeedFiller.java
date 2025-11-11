package fill;

import algorithm.PointsAlgorithm;
import constants.Constants;
import model.Point;
import raster.Raster;
import java.util.*;

public class SeedFiller implements Filler{
    private Raster raster;
    private int x;
    private int y;
    private int fillColor;

    public SeedFiller(Raster raster) {
        this.raster = raster;
        this.x = 0;
        this.y = 0;
        this.fillColor = 0xffffff;
    }

    public SeedFiller(Raster raster, int x, int y, int fillColor) {
        this.raster = raster;
        this.x = x;
        this.y = y;
        this.fillColor = fillColor;
    }

    /** Metoda omezující barvou pozadí, na kterou se klikne */
    public void fill(boolean isPatternFill) {
        // Omezení barvou pozadí
        OptionalInt pixel = raster.getPixel(x, y);

        if (pixel.isPresent())
            seedFiller(x, y, fillColor, pixel.getAsInt(), isPatternFill);
    }

    public void fill(int borderColor, boolean isPatternFill) {
        seedFillerBorder(x, y, fillColor, borderColor, isPatternFill);
    }

    /** SeedFiller pro omezení barvou hranice */
    public void seedFillerBorder(int x, int y, int fillColor, int borderColor, boolean isPatternFill) {
        Deque<Point> points = new ArrayDeque<>();
        points.push(new Point(x, y));

        while (!points.isEmpty()) {
            Point actualPoint = points.pop();

            int actualX = actualPoint.getX();
            int actualY = actualPoint.getY();

            OptionalInt pixel = this.raster.getPixel(actualX, actualY);

            if (pixel.isEmpty()) continue;

            int rgb  = pixel.getAsInt() & 0x00FFFFFF; // maska kvůli průhlednosti

            if (rgb == borderColor || rgb == fillColor) continue;

            // Pokud se vyplňuje vzorem, musíme vyřadit i barvy vzoru
            if (isPatternFill && (rgb == Constants.PATTERN_COLOR_1 || rgb == Constants.PATTERN_COLOR_2)) continue;

            // Pokud je zvolený pattern, getujeme barvu patternu
            int useColor = isPatternFill ? PointsAlgorithm.getPatternColor(actualX, actualY) : fillColor;

            this.raster.setPixel(actualX, actualY, useColor);

            points.push(new Point(actualX - 1, actualY));
            points.push(new Point(actualX + 1, actualY));
            points.push(new Point(actualX, actualY - 1));
            points.push(new Point(actualX, actualY + 1));
        }
    }

    /** SeedFiller pro omezení barvou pozadí */
    private void seedFiller(int x, int y, int fillColor, int backgroundColor, boolean isPatternFill) {
        Deque<Point> points = new ArrayDeque<>();
        points.push(new Point(x, y));

        while (!points.isEmpty()) {
            Point actualPoint = points.pop();

            int actualX = actualPoint.getX();
            int actualY = actualPoint.getY();

            OptionalInt pixel = this.raster.getPixel(actualX, actualY);

            if (pixel.isEmpty() || pixel.getAsInt() != backgroundColor) continue;

            // Pokud je zvolený pattern, getujeme barvu patternu
            int useColor = isPatternFill ? PointsAlgorithm.getPatternColor(actualX, actualY) : fillColor;

            this.raster.setPixel(actualX, actualY, useColor);

            points.push(new Point(actualX - 1, actualY));
            points.push(new Point(actualX + 1, actualY));
            points.push(new Point(actualX, actualY - 1));
            points.push(new Point(actualX, actualY + 1));
        }
    }

    public void changePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void changePoint(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }
}
