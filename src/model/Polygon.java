package model;

import java.util.*;

public class Polygon {

    private final List<Point> points;

    public Polygon() {
        this.points = new ArrayList<Point>();
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
}
