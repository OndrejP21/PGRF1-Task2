package controller;


import algorithm.PointsAlgorithm;
import clip.Clipper;
import clip.Orientation;
import constants.Constants;
import enums.FillerType;
import enums.LineRasterizerType;
import enums.RasterizeType;
import enums.SeedFillerType;
import fill.ScanLineFiller;
import model.*;
import model.Point;
import model.Polygon;
import model.Rectangle;
import raster.RasterBufferedImage;
import rasterize.PolygonRasterizer;
import view.ColorPickerDialog;
import view.EditorDialog;
import view.Panel;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

public class Controller2D {
    private final Panel panel;
    private final RasterBufferedImage raster;
    private LineRasterizerController lineRasterizerController;
    private FillerController fillerController;
    private model.Point firstPoint;
    private Polygon polygon;
    private Polygon polygonClipper;
    private Rectangle rectangle;
    /** Seznam polygonů, vždycky pracujeme s posledním polygonem */
    private PolygonsList polygons;
    private java.util.List<Line> lines;
    private RasterizeType type;
    private PolygonRasterizer polygonRasterizer;
    // povolení změny pozice polygonů (posouvání bodu)
    private boolean changingPositionEnabled;
    // Bod, který přesouváme po kliknutí
    private PolygonPointModel dragging;
    private Orientation orientation;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRaster();

        lineRasterizerController = new LineRasterizerController(raster);

        polygonRasterizer = new PolygonRasterizer(lineRasterizerController);

        fillerController = new FillerController(this.raster, this.lineRasterizerController, this.polygonRasterizer);
        lines = new ArrayList<>();
        type = RasterizeType.Lines;
        polygons = new PolygonsList();
        this.polygon = new Polygon();
        this.polygonClipper = new Polygon();
        this.rectangle = new Rectangle();

        this.changingPositionEnabled = false;

        dragging = null;
        this.orientation = Orientation.CCW;

        initListeners();

        drawScrene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point p = new Point(e.getX(), e.getY());

                // Pokud je povolená změna bodů, nevykreslujeme nic nového, pouze vybereme bod pro přesun
                if (changingPositionEnabled && e.getButton() == MouseEvent.BUTTON3) {
                    dragging = PointsAlgorithm.findNearestPoint(
                            e.getX(), e.getY(), Constants.MAX_FINDING_RADIUS, lines, polygon, polygonClipper, polygons.getList()
                    );
                    if (dragging != null) {
                        panel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
                    }

                    drawScrene();
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON2) {
                    fillerController.addFillingPoint(new Point(e.getX(), e.getY()));
                    drawScrene();
                    return;
                }

                switch (type) {
                    case RasterizeType.Lines:
                        if (firstPoint == null) {
                            firstPoint = p;
                            return;
                        } else {
                            lines.add(new Line(p, firstPoint));
                            firstPoint = null;
                        }
                        break;

                    case RasterizeType.Polygon:
                        Polygon peekedPolygon = polygons.peek();
                        assert peekedPolygon != null;

                        peekedPolygon.addPoint(p);
                        break;

                    case RasterizeType.ClipperPolygons:
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            polygonClipper.addPoint(p);
                        } else {
                            polygon.addPoint(p);
                        }
                        break;

                    case RasterizeType.Rectangle:
                        if (rectangle.getPoints().size() < 2) rectangle.addPoint(p);
                        break;
                }

                drawScrene();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (changingPositionEnabled) return;

                if (rectangle.getPoints().size() == 2) {
                    rectangle.addPoint(new Point(e.getX(), e.getY()));
                    drawScrene();
                };
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (rectangle.hasAtleastThreePoints())
                    rectangle.setCanChange(false);


                if (dragging != null) {
                    panel.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    dragging = null;
                    drawScrene();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = new Point(e.getX(), e.getY());
                boolean rightDown = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;

                // Pokud je povolená změna bodů, začneme přesouvat bod
                if (changingPositionEnabled && dragging != null && rightDown) {
                    dragging.Polygon.changePoint(dragging.Point, p);
                    dragging.Point = p;
                    drawScrene();
                    return;
                }

            if (rectangle.getPoints().size() >= 2) {
                    rectangle.addPoint(p);
                    drawScrene();
                };
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        type = type == RasterizeType.Lines ?
                                RasterizeType.Polygon :
                                type == RasterizeType.Polygon ?
                                        RasterizeType.ClipperPolygons :
                                        type == RasterizeType.ClipperPolygons ? RasterizeType.Rectangle :
                                        RasterizeType.Lines;
                        break;

                        // Změna typu filleru
                    case KeyEvent.VK_F:
                        fillerController.changeFillerType();
                        break;

                        // Kreslení nového polygonu a zároveň přidání aktuálního polygonu do ScanLine
                    case KeyEvent.VK_A:
                        fillerController.addPolygon(polygons.peek());
                        polygons.addEmptyPolygon();
                        break;

                        // Smazání aktuálního vybraného vrcholu
                    case KeyEvent.VK_D:
                        // Odstraníme daný vrchol polygonu
                        if (dragging != null) {
                            // Pokud náhodou odstraníme třetí vrchol, a tak zůstanou v Polygonu pouze dva, musíme ho smazat
                            if (!dragging.removePointFromPolygon()) {
                                // Musíme najít, o který polygon se jednalo
                                // Nejdříve ověříme polygony pro ořezávání
                                if (polygon == dragging.Polygon) {
                                    polygon = new Polygon();
                                } else if (polygonClipper == dragging.Polygon) {
                                    polygonClipper = new Polygon();
                                } else {
                                    polygons.remove(dragging.Polygon);

                                    // Pokud jsme odstranili poslední, musíme jeden přidat => vždy musí v poli jeden být
                                }
                            }

                            // Odstraníme i referenci na polygon, který jsme upravovali
                            dragging = null;
                            panel.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        }
                        break;

                    case KeyEvent.VK_G:
                        fillerController.ChangeSeedFillerType();
                        // Zeptáme se na barvu hranice => pokud chceme měnit podle hranice
                        if (fillerController.getSeedFillerType() == SeedFillerType.Border) {
                            OptionalInt c = ColorPickerDialog.showDialog(panel, "Vyber barvu čáry");
                            if (c.isPresent())
                                fillerController.setSeedFillerBorderColor(c.getAsInt());
                        }
                        break;

                    case KeyEvent.VK_H:
                        changingPositionEnabled = !changingPositionEnabled;
                        break;

                    case KeyEvent.VK_C:
                        lines.clear();

                        polygons.clear();

                        rectangle.clear();

                        polygon = new Polygon();
                        polygonClipper = new Polygon();

                        fillerController.clearStructures();
                        break;

                    case KeyEvent.VK_P:
                        fillerController.changeIsPatternFill();
                            break;

                    case KeyEvent.VK_O:
                        orientation = orientation == Orientation.CCW ? Orientation.CW : Orientation.CCW;
                        break;

                    case KeyEvent.VK_E:
                        java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                        assert polygons.peek() != null;
                        EditorDialog dialog = new EditorDialog(
                                owner,
                                lines,
                                polygons.getList(),
                                () -> drawScrene(),
                                panel.getWidth(),
                                panel.getHeight()
                        );
                        dialog.setVisible(true);
                        break;

                        // Změna typu LineRasterizeru na Trivial
                    case KeyEvent.VK_1:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Trivial);
                        break;

                    // Změna typu LineRasterizeru na Midpoint
                    case KeyEvent.VK_2:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Midpoint);
                        break;

                    // Změna typu LineRasterizeru na DDA
                    case KeyEvent.VK_3:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.DDA);
                        break;
                    // Změna typu LineRasterizeru na Transition
                    case KeyEvent.VK_4:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Color);
                        break;
                }

                drawScrene();
            }
        });
    }

    private void drawScrene() {
        panel.getRaster().clear();

        // Rasterizace úseček
        for (Line line : lines)
            lineRasterizerController.getRasterizer().rasterize(line, false);

        // Rasterizace polygonu
        for (Polygon p : this.polygons.getList()) {
            this.polygonRasterizer.rasterize(p, p == polygons.peek());
        }

        // Clipping
        this.polygonRasterizer.rasterize(this.polygon, false);
        this.polygonRasterizer.rasterize(this.polygonClipper, false);

        this.polygonRasterizer.rasterize(this.rectangle, false);

        Clipper clipper = new Clipper();
        List<Point> clippedPoints = clipper.clip(polygonClipper.getPoints(), polygon.getPoints(), this.orientation);

        ScanLineFiller scanLineFiller = new ScanLineFiller(new Polygon(clippedPoints), this.polygonRasterizer, this.lineRasterizerController.getRasterizer());
        scanLineFiller.fill(this.fillerController.isPatternFill());

        int polygonSize = polygons.size() - 1 + (polygons.peek().hasAtleastThreePoints() ? 1 : 0);

        if (dragging != null) panel.drawBigPoint(dragging.Point, 5);

        // Dodatečné stringové informace k vykreslení
        panel.setDrawStringInfo(new String[]{
                (changingPositionEnabled ? "PŘESOUVÁNÍ STRUKTUR POVOLENO" : "Aktuální režim: " + type + "; LineRasterizerAlgoritmus: " + lineRasterizerController.getLineRasterizerType()) + "; Počet polygonů: " + polygonSize,
                "Filler: " + fillerController.getFillerType() + (fillerController.getFillerType() == FillerType.SeedFill ? "; Typ seedFilleru: " + fillerController.getSeedFillerType() : ""), "Vyplňování vzorem: " + (this.fillerController.isPatternFill() ? "Ano" : "Ne") + " Orientace ořezávacího polygonu: " + (this.orientation == Orientation.CCW ? "Proti směru" : "Po směru")});

        // fillers, vybere se buď seedFill nebo ScanLine
        this.fillerController.fillStructures();

        panel.repaint();
    }

}
