package rasterize.lineRasterizers;

import algorithm.PointsAlgorithm;
import constants.Constants;
import raster.RasterBufferedImage;

/** DDA algoritmus
 * algoritmus pracují s teorií „zvětším-li x o 1, o kolik zvětším y?“ anebo naopak
 * jde po malých krocích buď na ose x nebo y a vždy o stejný krok zvětšuje krok na druhé ose
 * Výhoda: nemá speciální případy (například vertikály), funguje pro všechny kvadranty
 * Nevýhoda: pracuje s desetinnými čísly => nutnost zaokrouhelní; pomalejší
 * */
public class LineRasterizerDDA extends LineRasterizer {
    public LineRasterizerDDA(RasterBufferedImage image) {
        super(image);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, boolean isPatternFill) {
        super.rasterize(x1, y1, x2, y2, isPatternFill);

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Jednoduché řešení pro výpočet incrementů
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        // backup, aby nedošlo k dělení nulou => všechny souřadnice jsou stejné
        if (steps == 0) {
            this.image.setPixel(x1, y1, this.color);
        }

        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;

        // Počátek
        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            int useX = Math.round(x);
            int useY = Math.round(y);
            int useColor = isPatternFill ? PointsAlgorithm.getPatternColor(useX, useY) : this.color;

            this.image.setPixel(useX, useY, useColor);
            x += xIncrement;
            y += yIncrement;
        }
    }
}
