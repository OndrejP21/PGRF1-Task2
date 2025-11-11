package model;

public class Rectangle extends Polygon {
    // určuje, zda se může rectangle měnit, až ho dovytvoříme (přestaneme tahat), nebude možné jej měnit
    private boolean canChange;
    public Rectangle() {
        super();

        canChange = true;
    }


    @Override
    public void addPoint(Point p) {
        if (!canChange) return;

        // Pokud už máme více bodů než dva, musíme ponechat pouze první dva
        if (this.points.size() > 2) {
            points.subList(2, points.size()).clear();
        }

        // Pokud jsou méně jak dva body, prostě přidáme nový
        if (this.points.size() < 2) {
            this.points.add(p);
            return;
        }

        changeRectanglePoints(p);
    }

    private void changeRectanglePoints(Point p) {
        // Pokud by byl zvolen stejný bod => denegerovaný obdélník, nepřidáme, stejně tak, pokud už máme více jak 2 body => obdélník zrasterizován
        if (this.points.stream().anyMatch(x -> x.getX() == p.getX() && x.getY() == p.getY()) || this.points.size() > 2) {
            return;
        }

        // Souřadnice
        int x1 = this.points.get(0).getX();
        int y1 = this.points.get(0).getY();
        int x2 = this.points.get(1).getX();
        int y2 = this.points.get(1).getY();
        int x3 = p.getX();
        int y3 = p.getY();

        // vektor v
        int dx = x2 - x1;
        int dy = y2 - y1;

        // délka
        double length = Math.hypot(dx, dy);

        // normalizovaný jednotkový vektor u => vychází z v
        double ux = dx / length;
        double uy = dy / length;

        // vektor bodu A k C => p
        int px = (x3 - x1);
        int py = (y3 - y1);

        // Vektorový součin pro zjištění, zda je bod C vlevo nebo vpravo
        double crossProduct = dx * py - dy * px;

        // třetí bod na stejné přímce => degenerovaný obdélník
        if (crossProduct == 0) return;

        // normálový vektor n k vektoru u (kolmý k u) => pokud je vektorový součin menší než 0, musíme vektor otočit
        double nx = -uy * (crossProduct < 0 ? -1 : 1);
        double ny = ux * (crossProduct < 0 ? -1 : 1);

        // projekce AC na n
        double h = px * nx + py * ny;

        double offx = h * nx;
        double offy = h * ny;

        Point A2 = new Point((int)Math.round(x1 + offx), (int)Math.round(y1 + offy));
        Point B2 = new Point((int)Math.round(x2 + offx), (int)Math.round(y2 + offy));

        this.points.add(B2);
        this.points.add(A2);
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }
}
