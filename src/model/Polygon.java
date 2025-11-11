package model;

import java.util.*;

public class Polygon {

    protected final List<Point> points;

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

    public void removePoint(Point p) {
        this.points.remove(p);
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

    public void changePoint(Point oldPoint, Point newPoint) {
        int index = this.points.indexOf(oldPoint);
        if (index == -1) return;

        this.points.set(index, newPoint);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polygon polygon)) return false;
        return Objects.equals(points, polygon.points);
    }
}
