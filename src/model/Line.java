package model;

public class Line {
    private Point p1;
    private Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public boolean isHorizontal() {
        return p1.getY() == p2.getY();
    }

    // Orientace, aby byl vyšší bod p1
    public void orientate() {
        if (p1.getY() > p2.getY()) {
            Point tempP = p1;
            p1 = p2;
            p2 = tempP;
        }
    }
}
