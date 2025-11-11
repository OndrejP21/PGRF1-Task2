package model;

/** Model, ve kterém vrátíme polygon zároveň s bodem, který přesouváme */
public class PolygonPointModel {
    public Polygon Polygon;
    public Point Point;

    public PolygonPointModel(Polygon polygon, Point point) {
        this.Polygon = polygon;
        this.Point = point;
    }

    // odstraní bod a vrací true, pokud je polygon v pořád, false, pokud už má méně než 3 body
    public boolean removePointFromPolygon() {
        this.Polygon.removePoint(this.Point);

        return this.Polygon.hasAtleastThreePoints();
    }
}
