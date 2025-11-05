package view;

import algorithm.PointsAlgorithm;
import model.Line;
import model.Point;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;


public class EditorDialog extends JDialog {
    private final LinesTableModel linesModel;
    private final PointsTableModel pointsModel;
    private int width;
    private int height;

    public EditorDialog(java.awt.Window owner, List<Line> lines, model.Polygon polygon, Runnable redrawCallback, int w, int h) {
        super(owner, "Editor: Úsečky a polygon", ModalityType.MODELESS);

        width = w;
        height = h;

        this.linesModel = new LinesTableModel(lines);
        this.pointsModel = new PointsTableModel(polygon.getPoints());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Úsečky", buildLinesPanel());
        tabs.addTab("Polygon", buildPointsPanel());

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

    private JPanel buildPointsPanel() {
        JTable table = new JTable(pointsModel);
        JScrollPane sp = new JScrollPane(table);

        JButton add = new JButton("Přidat vrchol");
        add.addActionListener(e -> pointsModel.addEmptyRow());

        JButton remove = new JButton("Smazat vybraný");
        remove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) pointsModel.removeRow(row);
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
        private final List<Point> data;
        private final String[] cols = {"x","y"};

        PointsTableModel(List<Point> data) { this.data = data; }

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
                if (c == 0) p.setX(PointsAlgorithm.clampX(v, width)); else p.setY(PointsAlgorithm.clampY(v, height));
                fireTableRowsUpdated(r, r);
            } catch (NumberFormatException ignored) { }
        }

        void addEmptyRow() {
            data.add(new Point(0,0));
            int idx = data.size()-1;
            fireTableRowsInserted(idx, idx);
        }

        void removeRow(int r) {
            data.remove(r);
            fireTableRowsDeleted(r, r);
        }
    }
}
