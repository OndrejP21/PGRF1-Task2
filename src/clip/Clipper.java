package clip;

import model.Point;
import java.util.*;

public class Clipper {
    /** Metoda zjišťující, zda je bod vlevo od hrany (proti směru hodinových ručiček) */
    private boolean isInside(Point p, Point a, Point b, Orientation orientation) {
        double cross = (b.getX() - a.getX()) * (p.getY() - a.getY()) - (b.getY() - a.getY()) * (p.getX() - a.getX());

        // cross > 0 = vlevo, cross < 0 = vpravo, cross == 0 = na přímce
        return orientation == Orientation.CCW ? cross >= 0 : cross <= 0;
    }

    /** Metoda hledající bod průsečíku dvou přímek (L1 = q-p, L2 = a-b) */
    private Point getIntersectionPoint(Point q, Point p, Point a, Point b) {
        // V podstatě řešíme parametrické vyjádření dvou přímek (PŘÍMKA = BOD1 + t * (BOD2 - BOD1))

        int qpx = q.getX() - p.getX();
        int qpy = q.getY() - p.getY();
        int abx = b.getX() - a.getX();
        int aby = b.getY() - a.getY();

        // Budeme sestavovat soustavu rovnic, kde se musí rovnat body x jedné přímky s body x druhé přímky, to samé pro y
        // Příklad: px + t * (qpx) = ax + u * (abx)
        // py + t * (qpy) = ay + u * (aby)

        // Náš cíl je dostat k sobě honoty s parametrem
        // t * qpx - u * abx = ax - px
        // t * qpy - u * aby = ay - py
        // Můžeme převést na vektorový součin, protože bude platit rovnost
        // cross(t * qp - u * ab, ab) = cross(a - p, ab)
        // vektorové součiny jdou rozložit
        // cross(t * qp, ab) - cross(u * ab, ab) = cross(a - p, ab)
        // můžeme vytknout skalár
        // t * cross(qp, ab) - u * cross(ab, ab) = cross(a - p, ab)
        // vektorový součin stejného čísla dává 0, takže cross(ab, ab) = 0
        // t * cross(qp, ab) = cross(a - p, ab)
        // t = cross(a - p, ab) / cross(qp, ab)

        int apx = (a.getX() - p.getX());
        int apy = (a.getY() - p.getY());
        double t = (apx * aby - apy * abx) / (double) (qpx * aby - qpy * abx);

        return new Point((int) Math.round(p.getX() + t * qpx), (int) Math.round(p.getY() + t * qpy));
    }

    public List<Point> clip(List<Point> clipperPoints, List<Point> pointsToClip, Orientation orientation) {
        if (clipperPoints.size() < 3 || pointsToClip.isEmpty()) return new ArrayList<>();

        List<Point> in = new ArrayList<>(pointsToClip);

        int m = clipperPoints.size();
        for (int i = 0; i < m; i++) {
            Point edge1 = clipperPoints.get(i);
            Point edge2 = clipperPoints.get((i + 1) % m); // zajistíme spojení posledního s prvním

            // Pokud už nemáme nic v ořezávaném polygonu, vrátíme výsledek
            List<Point> out = new ArrayList<>();
            if (in.isEmpty()) return out;

            Point v1 = in.getLast();
            for (Point v2 : in) {
                if (this.isInside(v2, edge1, edge2, orientation)) {
                    // bod v1 není uvnitř obrazce, musíme zjistit průsečík
                    if (!this.isInside(v1, edge1, edge2, orientation))
                        out.add(this.getIntersectionPoint(v1, v2, edge1, edge2));

                    // jeden bod je uvnitř obrazce
                    out.add(v2);
                } else {
                    if (this.isInside(v1, edge1, edge2, orientation))
                        out.add(this.getIntersectionPoint(v1, v2, edge1, edge2));
                }

                v1 = v2;
            }

            in = out;
        }

        return in;
    }
}
