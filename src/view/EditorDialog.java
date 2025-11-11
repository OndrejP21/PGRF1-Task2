package view;

import algorithm.PointsAlgorithm;
import model.Line;
import model.Point;
import model.Polygon;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;


public class EditorDialog extends JDialog {
    private final LinesTableModel linesModel;
    private final PointsTableModel pointsModel;
    private final List<model.Polygon> polygons;
    private int currentPolyIndex;
    private JComboBox<String> polygonSelect;
    private final Runnable redrawCallback;
    private int width;
    private int height;

    public EditorDialog(java.awt.Window owner, List<Line> lines, List<model.Polygon> polygons, Runnable redrawCallback, int w, int h) {
        super(owner, "Editor: Úsečky a polygon", ModalityType.MODELESS);
        this.redrawCallback = redrawCallback;

        width = w;
        height = h;
        this.polygons = polygons;
        currentPolyIndex = 0;

        // Pokud nemáme žádný polygon, dáme prázdný list
        List<Point> initialPoints = polygons.isEmpty() ? new java.util.ArrayList<>() : polygons.getFirst().getPoints();
        this.pointsModel = new PointsTableModel(initialPoints);

        this.linesModel = new LinesTableModel(lines);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Úsečky", buildLinesPanel());
        tabs.addTab("Polygon",  buildPointsPanel(redrawCallback));

        JButton btnRedraw = new JButton("Překreslit");
        btnRedraw.addActionListener(e -> {
            if (redrawCallback != null) redrawCallback.run();
        });

        JButton btnClose = new JButton("Zavřít");
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRedraw);
        south.add(btnClose);

        setLayout(new BorderLayout(8,8));
        add(tabs, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(600, 400));
        pack();
        setLocationRelativeTo(owner);
    }

    private void populatePolygonSelect() {
        polygonSelect.removeAllItems();
        for (int i = 0; i < polygons.size(); i++) {
            polygonSelect.addItem("Polygon " + (i + 1));
        }
        if (polygons.isEmpty()) {
            currentPolyIndex = 0;
            polygonSelect.setEnabled(false);
        } else {
            polygonSelect.setEnabled(true);
            if (currentPolyIndex >= polygons.size()) currentPolyIndex = polygons.size() - 1;
            polygonSelect.setSelectedIndex(currentPolyIndex);
        }
    }

    private void onPolygonBecameInvalid() {
        if (polygons.isEmpty()) return;

        // Smažeme aktuální polygon
        polygons.remove(currentPolyIndex);

        // Vybereme data pro nový
        if (polygons.isEmpty()) {
            polygons.add(new Polygon());
            currentPolyIndex = 0;
        } else {
            if (currentPolyIndex >= polygons.size()) currentPolyIndex = polygons.size() - 1;
            pointsModel.setData(polygons.get(currentPolyIndex).getPoints());
        }

        // UI refresh
        populatePolygonSelect();
        pointsModel.fireTableDataChanged();

        if (redrawCallback != null) redrawCallback.run();
    }

    private JPanel buildPointsPanel(Runnable redrawCallback) {
        JTable table = new JTable(pointsModel);
        JScrollPane sp = new JScrollPane(table);

        polygonSelect = new JComboBox<>();
        populatePolygonSelect();
        polygonSelect.addActionListener(e -> {
            if (polygons.isEmpty()) {
                pointsModel.setData(new java.util.ArrayList<>());
                pointsModel.fireTableDataChanged();
                if (redrawCallback != null) redrawCallback.run();
                return;
            }
            int idx = polygonSelect.getSelectedIndex();
            if (idx < 0) return; // nic nevybráno
            currentPolyIndex = idx;
            pointsModel.setData(polygons.get(idx).getPoints());
            pointsModel.fireTableDataChanged();
            if (redrawCallback != null) redrawCallback.run();
        });

        JButton add = new JButton("Přidat vrchol");
        add.addActionListener(e -> {
            if (polygons.isEmpty()) return;
            pointsModel.addEmptyRow();
            if (redrawCallback != null) redrawCallback.run();
        });

        JButton remove = new JButton("Smazat vybraný");
        remove.addActionListener(e -> {
            if (polygons.isEmpty()) return;
            int row = table.getSelectedRow();
            if (row >= 0) {
                pointsModel.removeRow(row);
            }
        });


        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Vyber polygon:"));
        top.add(polygonSelect);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add);
        buttons.add(remove);

        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }


    private JPanel buildLinesPanel() {
        JTable table = new JTable(linesModel);
        JScrollPane sp = new JScrollPane(table);

        JButton add = new JButton("Přidat úsečku");
        add.addActionListener(e -> linesModel.addEmptyRow());

        JButton remove = new JButton("Smazat vybranou");
        remove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) linesModel.removeRow(row);
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add);
        buttons.add(remove);

        JPanel p = new JPanel(new BorderLayout());
        p.add(sp, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    // Tabulka pro práci s body úseček
    class LinesTableModel extends AbstractTableModel {
        private final List<Line> data;
        private final String[] cols = {"x1","y1","x2","y2"};

        LinesTableModel(List<Line> data) { this.data = data; }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public boolean isCellEditable(int r, int c) { return true; }

        @Override
        public Object getValueAt(int r, int c) {
            Line L = data.get(r);
            switch (c) {
                case 0: return L.getP1().getX();
                case 1: return L.getP1().getY();
                case 2: return L.getP2().getX();
                case 3: return L.getP2().getY();
            }
            return null;
        }

        @Override
        public void setValueAt(Object val, int r, int c) {
            try {
                int v = Integer.parseInt(val.toString());
                Line L = data.get(r);
                Point A = L.getP1();
                Point B = L.getP2();

                switch (c) {
                    case 0: A.setX(PointsAlgorithm.clampX(v, width)); break;
                    case 1: A.setY(PointsAlgorithm.clampY(v, height)); break;
                    case 2: B.setX(PointsAlgorithm.clampX(v, width)); break;
                    case 3: B.setY(PointsAlgorithm.clampY(v, height)); break;
                }
                fireTableRowsUpdated(r, r);
            } catch (NumberFormatException ignored) { }
        }

        void addEmptyRow() {
            data.add(new Line(new Point(0,0), new Point(0,0)));
            int idx = data.size()-1;
            fireTableRowsInserted(idx, idx);
        }

        void removeRow(int r) {
            data.remove(r);
            fireTableRowsDeleted(r, r);
        }
    }

    // Tabulka pro práci s body Polygonu
    class PointsTableModel extends AbstractTableModel {
        private List<Point> data;
        private final String[] cols = {"x","y"};

        PointsTableModel(List<Point> data) { this.data = data; }

        public void setData(List<Point> newData) {
            this.data = newData;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public boolean isCellEditable(int r, int c) { return true; }

        @Override
        public Object getValueAt(int r, int c) {
            Point p = data.get(r);
            return c == 0 ? p.getX() : p.getY();
        }

        @Override
        public void setValueAt(Object val, int r, int c) {
            try {
                int v = Integer.parseInt(val.toString());
                Point p = data.get(r);
                if (c == 0) p.setX(PointsAlgorithm.clampX(v, width));
                else        p.setY(PointsAlgorithm.clampY(v, height));
                fireTableRowsUpdated(r, r);
                if (redrawCallback != null) redrawCallback.run();
            } catch (NumberFormatException ignored) { }
        }

        void addEmptyRow() {
            data.add(new Point(0,0));
            int idx = data.size()-1;
            fireTableRowsInserted(idx, idx);
        }

        void removeRow(int r) {
            data.remove(r);
            if (data.size() < 3) {
                EditorDialog.this.onPolygonBecameInvalid();
            } else {
                fireTableRowsDeleted(r, r);
                if (redrawCallback != null) redrawCallback.run();
            }
        }
    }
}
