package rasterize.lineRasterizers;

import constants.Constants;
import raster.RasterBufferedImage;

import java.awt.*;

/** Triviální algoritmus
 * jednoduchý algoritmus pro vykreslení úsečky, který pracuje s výpočtem hodnoty y na základě lineární funkce
 * Výhoda: při drobných úpravách funkční ve všech kvadrantech (nutné si dát pozor na to, který koncový bod je menší)
 * Nevýhoda: pracuje s desetinnými čísly => nutnost zaokrouhelní; nutné speciálně ošetřit případ svislých úseček
 * */
public class LineRasterizerColorTransition extends LineRasterizer {
    public LineRasterizerColorTransition(RasterBufferedImage image) {
        super(image);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, boolean isPatternFill) {
        super.rasterize(x1, y1, x2, y2, false);

        Color c1 = Color.red;
        Color c2 = Color.green;

        // Stejný bod, vykreslíme pouze bod => degenerovaná úsečka
        if (x1 == x2 && y1 == y2) {
            image.setPixel(x1, y1, Constants.COLOR);
        }

        int deltay = y2 - y1;
        int deltax = x2 - x1;

        float k = deltay / (float) deltax;
        float q = y1 - k * x1;

        // Prohození řídících os
        if (Math.abs(k) < 1) {
            // Prohození koncových bodů v případě, že je první větší
            if (x1 > x2) {
                int tempx = x2;
                int tempy = y2;
                x2 = x1;
                x1 = tempx;
                y2 = y1;
                y1 = tempy;
            }

            // Vykreslování podle osy x
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);

                // Interpolace barvy
                Color interpolateColor = this.getInterpolateColor(x1, x2, x, c1, c2);

                image.setPixel(x, y, interpolateColor.getRGB());
            }
        } else {
            // Prohození koncových bodů v případě, že je první větší
            if (y1 > y2) {
                int tempx = x2;
                int tempy = y2;
                x2 = x1;
                x1 = tempx;
                y2 = y1;
                y1 = tempy;
            }

            // Vykreslování podle osy y
            for (int y = y1; y <= y2; y++) {
                int x = Math.round((y - q) / k);

                // Interpolace barvy
                Color interpolateColor = this.getInterpolateColor(y1, y2, y, c1, c2);

                image.setPixel(deltax != 0 ? x : x1, y, interpolateColor.getRGB());
            }
        }
    }

    private Color getInterpolateColor(int x1, int x2, int xi, Color c1, Color c2) {
        float t = (xi - x1) / (float) (x2 - x1);

        float[] newColors = new float[3];

        float[] colorComponents1 = c1.getColorComponents(null);
        float[] colorComponents2 = c2.getColorComponents(null);

        for (int i = 0; i < colorComponents1.length; i++) {
            newColors[i] = (1 - t) * colorComponents1[i] + t * colorComponents2[i];
        }

        return new Color(newColors[0], newColors[1], newColors[2]);
    }
}
