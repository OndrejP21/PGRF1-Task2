package controller;


import algorithm.PointsAlgorithm;
import clip.Clipper;
import constants.Constants;
import enums.FillerType;
import enums.LineRasterizerType;
import enums.RasterizeType;
import enums.SeedFillerType;
import fill.Filler;
import fill.ScanLineFiller;
import fill.SeedFiller;
import model.Line;
import model.Point;
import model.Polygon;
import raster.RasterBufferedImage;
import rasterize.PolygonRasterizer;
import view.ColorPickerDialog;
import view.EditorDialog;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.*;

public class Controller2D {
    private final Panel panel;
    private final RasterBufferedImage raster;
    private LineRasterizerController lineRasterizerController;
    private FillerController fillerController;
    private model.Point firstPoint;
    private Polygon polygon;
    private Polygon polygonClipper;
    /** Seznam polygonů, vždycky pracujeme s posledním polygonem */
    private Deque<Polygon> polygons;
    private java.util.List<Line> lines;
    private RasterizeType type;
    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRaster();

        lineRasterizerController = new LineRasterizerController(raster);

        polygonRasterizer = new PolygonRasterizer(lineRasterizerController);

        fillerController = new FillerController(this.raster, this.lineRasterizerController, this.polygonRasterizer);
        lines = new ArrayList<>();
        type = RasterizeType.Lines;
        polygons = new ArrayDeque<>(){{add(new Polygon());}};
        this.polygon = new Polygon();
        this.polygonClipper = new Polygon();

        initListeners();

        drawScrene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getButton() == MouseEvent.BUTTON2) {
                    fillerController.addFillingPoint(new Point(e.getX(), e.getY()));
                    drawScrene();
                    return;
                }

                Point p = new Point(e.getX(), e.getY());
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
                        assert polygons.peekLast() != null;
                        polygons.peekLast().addPoint(p);
                        break;

                    case RasterizeType.ClipperPolygons:
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            polygonClipper.addPoint(p);
                        } else {
                            polygon.addPoint(p);
                        }
                        break;
                }

                drawScrene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        type = type == RasterizeType.Lines ?  RasterizeType.Polygon : type == RasterizeType.Polygon ? RasterizeType.ClipperPolygons : RasterizeType.Lines;
                        drawScrene();
                        break;

                        // Změna typu filleru
                    case KeyEvent.VK_F:
                        fillerController.changeFillerType();
                        drawScrene();
                        break;

                        // Kreslení nového polygonu a zároveň přidání aktuálního polygonu do ScanLine
                    case KeyEvent.VK_A:
                        fillerController.addPolygon(polygons.peekLast());
                        polygons.add(new Polygon());
                        drawScrene();
                        break;

                    case KeyEvent.VK_G:
                        fillerController.ChangeSeedFillerType();
                        // Zeptáme se na barvu hranice => pokud chceme měnit podle hranice
                        if (fillerController.getSeedFillerType() == SeedFillerType.Border) {
                            OptionalInt c = ColorPickerDialog.showDialog(panel, "Vyber barvu čáry");
                            if (c.isPresent())
                                fillerController.setSeedFillerBorderColor(c.getAsInt());
                        }
                        drawScrene();
                        break;

                    case KeyEvent.VK_C:
                        panel.getRaster().clear();
                        lines.clear();

                        polygons.clear();
                        polygons.add(new Polygon());

                        panel.repaint();
                        break;

                    case KeyEvent.VK_E:
                        java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                        assert polygons.peekLast() != null;
                        EditorDialog dialog = new EditorDialog(
                                owner,
                                lines,
                                polygons.peekLast(),
                                () -> drawScrene(),
                                panel.getWidth(),
                                panel.getHeight()
                        );
                        dialog.setVisible(true);
                        break;

                        // Změna typu LineRasterizeru na Trivial
                    case KeyEvent.VK_1:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Trivial);
                        drawScrene();
                        break;

                    // Změna typu LineRasterizeru na Midpoint
                    case KeyEvent.VK_2:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Midpoint);
                        drawScrene();
                        break;

                    // Změna typu LineRasterizeru na DDA
                    case KeyEvent.VK_3:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.DDA);
                        drawScrene();
                        break;
                    // Změna typu LineRasterizeru na Transition
                    case KeyEvent.VK_4:
                        lineRasterizerController.setLineRasterizerType(LineRasterizerType.Color);
                        drawScrene();
                        break;
                }


            }
        });
    }

    private void drawScrene() {
        panel.getRaster().clear();

        // Rasterizace úseček
        for (Line line : lines)
            lineRasterizerController.getRasterizer().rasterize(line);

        // Rasterizace polygonu
        for (Polygon p : this.polygons) {
            this.polygonRasterizer.rasterize(p, p == polygons.peekLast());
        }

        // Clipping
        this.polygonRasterizer.rasterize(this.polygon, false);
        this.polygonRasterizer.rasterize(this.polygonClipper, false);

        Clipper clipper = new Clipper();
        List<Point> clippedPoints = clipper.clip(polygonClipper.getPoints(), polygon.getPoints());

        ScanLineFiller scanLineFiller = new ScanLineFiller(new Polygon(clippedPoints), this.polygonRasterizer, this.lineRasterizerController.getRasterizer());
        scanLineFiller.fill();

        int polygonSize = polygons.size() - 1 + (polygons.peekLast().hasAtleastThreePoints() ? 1 : 0);

        // Dodatečné stringové informace k vykreslení
        panel.setDrawStringInfo(new String[]{
                "Aktuální režim: " + type + "; LineRasterizerAlgoritmus: " + lineRasterizerController.getLineRasterizerType() + "; Počet polygonů: " + polygonSize,
                "Filler: " + fillerController.getFillerType() + (fillerController.getFillerType() == FillerType.SeedFill ? "; Typ seedFilleru: " + fillerController.getSeedFillerType() : "")});

        // fillers, vybere se buď seedFill nebo ScanLine
        this.fillerController.fillStructures();

        panel.repaint();
    }

}
