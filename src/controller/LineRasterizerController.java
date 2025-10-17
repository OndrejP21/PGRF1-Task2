package controller;

import enums.LineRasterizerType;
import raster.RasterBufferedImage;
import rasterize.lineRasterizers.*;

import java.util.HashMap;

/** Třída spravující všechny lineRasterizery
 * obsahuje seznam všech lineRasterizerů,
 * spravuje výběr lineRasterizeru */
public class LineRasterizerController {
    private HashMap<LineRasterizerType, LineRasterizer> lineRasterizers;
    private LineRasterizerType lineRasterizerType;
    private boolean isShiftHold;

    public LineRasterizerController(RasterBufferedImage raster) {
        this.lineRasterizerType = LineRasterizerType.Trivial;
        isShiftHold = false;

        lineRasterizers = new HashMap<>() {{
            put(LineRasterizerType.Trivial, new LineRasterizerTrivial(raster));
            put(LineRasterizerType.Midpoint, new LineRasterizerMidpoint(raster));
            put(LineRasterizerType.DDA, new LineRasterizerDDA(raster));
            put(LineRasterizerType.Color, new LineRasterizerColorTransition((raster)));
        }};
    }

    public LineRasterizerType getLineRasterizerType() {
        return lineRasterizerType;
    }

    public LineRasterizer getRasterizer() {
        return lineRasterizers.get(lineRasterizerType);
    }

    public void setLineRasterizerType(LineRasterizerType lineRasterizerType) {
        this.lineRasterizerType = lineRasterizerType;
    }

    public boolean isShiftHold() {
        return isShiftHold;
    }

    public void setShiftHold(boolean shiftHold) {
        isShiftHold = shiftHold;
    }
}
