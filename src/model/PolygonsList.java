package model;

import java.util.*;
/* Specifická třída spravující práci s listem polygonů */
public class PolygonsList {
    private Deque<Polygon> polygons;

    public PolygonsList() {
        this.polygons = new ArrayDeque<>(){{add(new Polygon());}};
    }

    public void addEmptyPolygon() {
        this.polygons.push(new Polygon());
    }

    public void remove(Polygon polygon) {
        this.polygons.remove(polygon);

        // Vždy musí alespoň jeden polygon existovat
        if (this.polygons.isEmpty()) this.polygons.push(new Polygon());
    }


    public Polygon peek() {
        return this.polygons.peek();
    }

    public int size() {
        return this.polygons.size();
    }

    public void clear() {
        this.polygons.clear();
        this.polygons.push(new Polygon());
    }

    public List<Polygon> getList() {
        return new ArrayList<>(this.polygons);
    }
}
