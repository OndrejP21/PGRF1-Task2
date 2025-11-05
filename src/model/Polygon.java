package model;

import java.util.*;

public class Polygon {

    private final List<Point> points;

    public Polygon() {
        this.points = new ArrayList<Point>();
    }

    public Polygon(List<Point> points) {
        this.points = points;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public int getSize() {
        return points.size();
    }

    public void clear() {
        this.points.clear();
    }

    public List<Point> getPoints() {
        return points;
    }

    public boolean hasAtleastThreePoints() {
        return points.size() >= 3;
    }

    public List<Line> getLines() {
        List<Line> lines = new ArrayList<Line>();

        for(int i = 0; i < points.size(); i++) {
            lines.add(new Line(points.get(i), points.get(i + 1 == points.size() ? 0 : i + 1)));
        }

        return lines;
    }
}
