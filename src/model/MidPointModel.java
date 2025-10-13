package model;

public class MidPointModel {
    private Point point1;
    private Point point2;

    public MidPointModel(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    @Override
    public String toString() {
        return "MidPointModel{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }
}
