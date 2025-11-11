package controller;

import enums.FillerType;
import enums.SeedFillerType;
import fill.Filler;
import fill.ScanLineFiller;
import fill.SeedFiller;
import model.Point;
import model.Polygon;
import raster.RasterBufferedImage;
import rasterize.PolygonRasterizer;

import java.util.HashMap;

import java.util.*;

/** Controller spravující vyplňování oblastí */
public class FillerController {
    private HashMap<FillerType, Filler> fillers;
    private LineRasterizerController lineRasterizerController;
    private  PolygonRasterizer polygonRasterizer;
    // Typ filleru => jestli SeedFiller nebo ScanLine
    private FillerType fillerType;
    // zda používá seedfiller pozadí nebo hranici
    private SeedFillerType seedFillerType;
    // barva ohraničení v případě seedFillerTypu = border
    private int seedFillerBorderColor;
    // Zda vyplňujeme vzorem
    private boolean isPatternFill;
    // pole uchovávající body, ve kterých probíhá vyplňování pomocí SeedFillu
    // Pomocí barvy pozadí
    private List<Point> fillingPointsBackground;
    // Pomocí barvy borderu
    private List<Point> fillingPointsBorder;
    // pole uchovávající polygony pro vyplnění pomocí ScanLine algoritmu
    private List<Polygon> polygons;

    public FillerController(RasterBufferedImage raster, LineRasterizerController lineRasterizerController, PolygonRasterizer polygonRasterizer) {
        this.fillerType = FillerType.SeedFill;
        fillingPointsBackground = new ArrayList<>();
        fillingPointsBorder = new ArrayList<>();

        polygons = new ArrayList<>();
        this.lineRasterizerController = lineRasterizerController;
        this.polygonRasterizer = polygonRasterizer;
        seedFillerType = SeedFillerType.Background;

        this.isPatternFill = false;

        fillers = new HashMap<>() {{
            put(FillerType.SeedFill, new SeedFiller(raster));
            put(FillerType.ScanLine, new ScanLineFiller(raster));
        }};
    }

    public void clearStructures() {
        this.fillingPointsBackground.clear();
        this.fillingPointsBorder.clear();
        this.polygons.clear();
    }

    public void addFillingPoint(Point p) {
        if (this.seedFillerType == SeedFillerType.Background)
            this.fillingPointsBackground.add(p);
        else
            this.fillingPointsBorder.add(p);
    }

    public void addPolygon(Polygon p) {
        this.polygons.add(p);
    }

    public Filler getFiller() {
        return this.fillers.get(this.fillerType);
    }

    public FillerType getFillerType() {
        return fillerType;
    }

    public void ChangeSeedFillerType() {
        this.seedFillerType = this.seedFillerType == SeedFillerType.Background ? SeedFillerType.Border : SeedFillerType.Background;
    }

    public SeedFillerType getSeedFillerType() {
        return seedFillerType;
    }

    public void setSeedFillerBorderColor(int seedFillerBorderColor) {
        this.seedFillerBorderColor = seedFillerBorderColor;
    }

    public void changeFillerType() {
        this.fillerType = this.fillerType == FillerType.SeedFill ? FillerType.ScanLine : FillerType.SeedFill;
    }

    public boolean isPatternFill() {
        return this.isPatternFill;
    }

    public void changeIsPatternFill() {
        this.isPatternFill = !this.isPatternFill;
    }

    /** Voláme vždy, když chceme vyplnit struktury */
    public void fillStructures() {
        Filler actualFiller = this.getFiller();

        if (actualFiller instanceof SeedFiller seedFiller) {
            for (Point p : this.fillingPointsBackground) {
                seedFiller.changePoint(p);
                seedFiller.fill(isPatternFill);
            }

            for (Point p : this.fillingPointsBorder) {
                seedFiller.changePoint(p);
                seedFiller.fill(this.seedFillerBorderColor, isPatternFill);
            }

        } else if (actualFiller instanceof ScanLineFiller scanLineFiller) {
            for (Polygon p : this.polygons) {
                scanLineFiller.ChangeSettings(p, this.polygonRasterizer, this.lineRasterizerController.getRasterizer());
                scanLineFiller.fill(isPatternFill);
            }

        }

    }
}
