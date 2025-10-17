package controller;


import algorithm.PointsAlgorithm;
import constants.Constants;
import enums.LineRasterizerType;
import enums.RasterizeType;
import model.Line;
import model.Point;
import model.Polygon;
import raster.RasterBufferedImage;
import rasterize.PolygonRasterizer;
import view.EditorDialog;
import view.Panel;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.*;

public class Controller2D {
    private final Panel panel;
    private final RasterBufferedImage raster;
    private LineRasterizerController lineRasterizerController;
    private model.Point firstPoint;
    private Polygon polygon;
    /** Aktuálně zvolený bod při stisku pravým tlačítkem (nejbližší) */
    private Point draggingPoint = null;
    /** Bod sloužící pro pružné překreslení (mění se při tažení myší) */
    private Point actualPoint = null;

    private java.util.List<Line> lines;
    private RasterizeType type;

    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRaster();

        lineRasterizerController = new LineRasterizerController(raster);

        polygonRasterizer = new PolygonRasterizer(lineRasterizerController);
        lines = new ArrayList<>();
        type = RasterizeType.Lines;
        polygon = new Polygon();

        initListeners();

        this.panel.setDrawStringInfo("Aktuální režim: " + type + "; LineRasterizerAlgoritmus: " + lineRasterizerController.getLineRasterizerType());
        drawScrene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (SwingUtilities.isRightMouseButton(e)) {
                    draggingPoint = PointsAlgorithm.findNearestPoint(e.getX(), e.getY(), Constants.MAX_FINDING_RADIUS, lines, polygon);
                    if (draggingPoint != null) panel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));

                    drawScrene();
                    return;
                }

                switch (type) {
                    case RasterizeType.Lines:
                        if (firstPoint == null) {
                            firstPoint = new model.Point(e.getX(), e.getY());
                            return;
                        }

                        // Pro zarovnanou úsečku
                        Point[] shiftedPoints = PointsAlgorithm.getShiftedPoints(firstPoint.getX(), firstPoint.getY(), e.getX(), e.getY());
                        lines.add(new Line(firstPoint, lineRasterizerController.isShiftHold() ? shiftedPoints[1] : new model.Point(e.getX(), e.getY())));
                        firstPoint = null;
                        break;

                    case RasterizeType.Polygon:
                        polygon.addPoint(new Point(e.getX(), e.getY()));
                        break;
                }


                drawScrene();
            }

            // Povolení pravého tlačítka myši
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (draggingPoint != null) {
                    draggingPoint = null;
                    panel.setCursor(java.awt.Cursor.getDefaultCursor());
                    drawScrene();
                }
            }

        });

        panel.addMouseMotionListener(new MouseAdapter() {
            // Přesouvání bodu při tažení
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (draggingPoint != null) {
                    draggingPoint.setX(PointsAlgorithm.clampX(e.getX(), raster.getWidth()));
                    draggingPoint.setY(PointsAlgorithm.clampY(e.getY(), raster.getHeight()));
                    drawScrene();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                actualPoint = new Point(e.getX(), e.getY());
                drawScrene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        type = type == RasterizeType.Lines ? RasterizeType.Polygon : RasterizeType.Lines;
                        break;

                    case KeyEvent.VK_C:
                        panel.getRaster().clear();
                        lines.clear();
                        polygon.clear();
                        panel.repaint();
                        break;

                    case KeyEvent.VK_E:
                        java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                        EditorDialog dialog = new EditorDialog(
                                owner,
                                lines,
                                polygon,
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
                        // Držení shiftu
                    case KeyEvent.VK_SHIFT:
                        lineRasterizerController.setShiftHold(true);
                        drawScrene();
                        break;
                }


            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    lineRasterizerController.setShiftHold(false);
                    drawScrene();
                }

            }
        });
    }

    private void drawScrene() {
        panel.getRaster().clear();

        for (Line line : lines)
            lineRasterizerController.getRasterizer().rasterize(line);

        polygonRasterizer.rasterize(polygon);

        if (draggingPoint != null) panel.drawBigPoint(draggingPoint, 5);

        // Pružné vykreslení při tažen myší
        if (actualPoint != null && firstPoint != null) lineRasterizerController.getRasterizer().rasterize(new Line(firstPoint, actualPoint), lineRasterizerController.isShiftHold());

        // Dodatečné stringové informace k vykreslení
        panel.setDrawStringInfo("Aktuální režim: " + type + "; LineRasterizerAlgoritmus: " + lineRasterizerController.getLineRasterizerType());

        panel.repaint();
    }

}
