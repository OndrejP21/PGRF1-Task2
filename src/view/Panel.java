package view;

import algorithm.PointsAlgorithm;
import constants.Constants;
import enums.LineRasterizerType;
import enums.RasterizeType;
import model.Point;
import raster.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;

import java.util.*;

public class Panel extends JPanel {

    private final RasterBufferedImage raster;
    private String[] drawStringInfo = new String[]{"", ""};

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        raster = new RasterBufferedImage(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(raster.getImage(), 0, 0, null);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        int i = 1;
        for (String info : drawStringInfo)
            g.drawString(info, 5, 20 * i++);
    }

    public void setDrawStringInfo(String[] drawStringInfo) {
        this.drawStringInfo = drawStringInfo;
    }

    /** Nakreslní většího bodu */
    public void drawBigPoint(model.Point p, int radius) {
        Deque<Point> dq = new ArrayDeque<>();
        dq.add(p);

        // Pole již navštívených
        boolean[][] visited = new boolean[getWidth()][getHeight()];

        while (!dq.isEmpty()) {
            Point cur = dq.pop();
            int x = cur.getX();
            int y = cur.getY();

            if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight() || visited[x][y]) continue;
            visited[x][y] = true;

            int dx = x - p.getX();
            int dy = y - p.getY();

            // Jsme mimo hranice radiusu
            if (dx*dx + dy*dy > radius*radius) continue;

            raster.setPixel(x, y, Constants.RED_COLOR);

            dq.push(new Point(x - 1, y));
            dq.push(new Point(x + 1, y));
            dq.push(new Point(x, y - 1));
            dq.push(new Point(x, y + 1));
        }
    }

    public RasterBufferedImage getRaster() {
        return raster;
    }
}
