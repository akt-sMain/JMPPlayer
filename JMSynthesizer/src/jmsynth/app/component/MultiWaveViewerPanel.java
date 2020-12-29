package jmsynth.app.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JPanel;

import jmsynth.JMSoftSynthesizer;

public class MultiWaveViewerPanel extends JPanel {

    private static Color BG_COLOR = Color.BLACK; // new Color(0, 0, 102);
    private static Color[] WAVE_COLOR = new Color[] { new Color(255, 255, 102), new Color(255, 204, 255), new Color(255, 204, 102), new Color(255, 102, 102),
            new Color(255, 255, 255), new Color(204, 255, 102), new Color(204, 204, 255), new Color(204, 255, 204), new Color(255, 0, 255),
            new Color(255, 0, 0), new Color(204, 102, 255), new Color(102, 51, 255), new Color(0, 128, 128), new Color(51, 204, 51), new Color(230, 230, 250),
            new Color(0, 0, 255), };
    private static Color CENTER_COLOR = Color.DARK_GRAY;

    byte[] d1 = null;
    byte[] d2 = null;
    byte[] d3 = null;
    byte[] d4 = null;
    byte[] d5 = null;
    byte[] d6 = null;
    byte[] d7 = null;
    byte[] d8 = null;
    byte[] d9 = null;
    byte[] d10 = null;
    byte[] d11 = null;
    byte[] d12 = null;
    byte[] d13 = null;
    byte[] d14 = null;
    byte[] d15 = null;
    byte[] d16 = null;
    public boolean[] visibleWave = new boolean[16];
    public boolean traceShift = true;

    private Color[] waveColorTable = null;

    /**
     * Create the panel.
     */
    public MultiWaveViewerPanel(JMSoftSynthesizer synth) {

        synth.setWaveRepaintListener(0, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d1 = waveData;
            }
        });
        synth.setWaveRepaintListener(1, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d2 = waveData;
            }
        });
        synth.setWaveRepaintListener(2, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d3 = waveData;
            }
        });
        synth.setWaveRepaintListener(3, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d4 = waveData;
            }
        });
        synth.setWaveRepaintListener(4, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d5 = waveData;
            }
        });
        synth.setWaveRepaintListener(5, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d6 = waveData;
            }
        });
        synth.setWaveRepaintListener(6, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d7 = waveData;
            }
        });
        synth.setWaveRepaintListener(7, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d8 = waveData;
            }
        });
        synth.setWaveRepaintListener(8, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d9 = waveData;
            }
        });
        synth.setWaveRepaintListener(9, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d10 = waveData;
            }
        });
        synth.setWaveRepaintListener(10, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d11 = waveData;
            }
        });
        synth.setWaveRepaintListener(11, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d12 = waveData;
            }
        });
        synth.setWaveRepaintListener(12, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d13 = waveData;
            }
        });
        synth.setWaveRepaintListener(13, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d14 = waveData;
            }
        });
        synth.setWaveRepaintListener(14, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d15 = waveData;
            }
        });
        synth.setWaveRepaintListener(15, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d16 = waveData;
                repaintWavePane();
            }
        });
        cinit();
    }

    private void repaintWavePane() {
        repaint();
    }

    private void cinit() {
        // setPreferredSize(new Dimension(240, 150));
        setBackground(BG_COLOR);
        Arrays.fill(visibleWave, true);

        waveColorTable = new Color[WAVE_COLOR.length];
        for (int i=0; i<waveColorTable.length; i++) {
            waveColorTable[i] = WAVE_COLOR[i];
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        // g.drawString(panelName, 10, 20);

        if (traceShift == false) {
            int yCenter = this.getHeight() / 2;
            g2d.setColor(CENTER_COLOR);
            g2d.drawLine(0, yCenter, this.getWidth(), yCenter);
            mergeWave(g, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14, d15);
        }
        else {
            paintWave(g, d1, 0);
            paintWave(g, d2, 1);
            paintWave(g, d3, 2);
            paintWave(g, d4, 3);
            paintWave(g, d5, 4);
            paintWave(g, d6, 5);
            paintWave(g, d7, 6);
            paintWave(g, d8, 7);
            paintWave(g, d9, 8);
            paintWave(g, d10, 9);
            paintWave(g, d11, 10);
            paintWave(g, d12, 11);
            paintWave(g, d13, 12);
            paintWave(g, d14, 13);
            paintWave(g, d15, 14);
            paintWave(g, d16, 15);
        }
    }

    int[] mergeData = null;
    public void mergeWave(Graphics g, byte[]... acceccer) {
        if (d1 == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        mergeData = new int[d1.length];
        Arrays.fill(mergeData, 0);

        byte[] data;

        int traceCount = 0;
        for (int i=0; i<acceccer.length; i++) {
            data = acceccer[i];
            if (data == null) {
                continue;
            }
            if (data.length <= 2) {
                continue;
            }

            if (visibleWave[i] == true) {
                for (int k=0; k<data.length; k++) {
                    mergeData[k] += data[k];
                }
                traceCount++;
            }
        }

        if (traceCount == 0) {
            return;
        }

        // データ点描画
        boolean output = false;
        Color waveColor = Color.ORANGE;
        int xoffset = 0;
        int yCenter = this.getHeight() / 2;
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();
        int vvHeight = this.getHeight();

        int[] d = mergeData;

        if (d != null && d.length > 2) {
            int length = d.length / 2;
            double xDelta = (double) vWidth / (double) (length);
            double yDelta = vvHeight / (64.0 /* traceCount*/);
            int j = 0;

            int xPoints[] = new int[length];
            int yPoints[] = new int[length];
            for (int i = 0; i < d.length; i += 2) {
                xPoints[j] = xoffset + (int) (xDelta * j);
                yPoints[j] = (int) (yDelta * (d[i] / 1.5)) + yCenter;
                j++;
            }
            g2d.setColor(waveColor);
            g2d.drawPolyline(xPoints, yPoints, length);
        }
    }

    public void paintWave(Graphics g, byte[] d, int ch) {
        Graphics2D g2d = (Graphics2D) g;
        if (d == null) {
            return;
        }
        if (d.length <= 2) {
            return;
        }

        // データ点描画
        boolean output = false;
        Color waveColor = waveColorTable[ch];
        int xoffset = 0;
        int yCenter = this.getHeight() / 2;
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();
        int vvHeight = this.getHeight();
        if (traceShift == true) {
            vHeight /= 4;
            vvHeight /= 4;
            vWidth /= 4;
            yCenter = (vHeight / 2) + (vHeight * (ch / 4));
            xoffset = (vWidth) * (ch % 4);
        }
        else {
            waveColor = new Color(waveColorTable[ch].getRed(), waveColorTable[ch].getGreen(), waveColorTable[ch].getBlue(), 160);
        }

        if (traceShift == true) {
            g2d.setColor(CENTER_COLOR);
            g2d.drawLine(xoffset, yCenter, xoffset + vWidth, yCenter);
        }

        if (d != null && d.length > 2) {
            int length = d.length / 2;
            double xDelta = (double) vWidth / (double) (length);
            double yDelta = vvHeight / 64.0;
            int j = 0;

            int xPoints[] = new int[length];
            int yPoints[] = new int[length];
            for (int i = 0; i < d.length; i += 2) {
                if (d[i] != 0) {
                    output = true;
                }
                xPoints[j] = xoffset + (int) (xDelta * j);
                yPoints[j] = (int) (yDelta * d[i]) + yCenter;
                j++;
            }
            if (visibleWave[ch] == true) {
                g2d.setColor(waveColor);
                g2d.drawPolyline(xPoints, yPoints, length);
            }
        }

        if (traceShift == true) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString("wave" + (ch + 1), xoffset + 5 + 1, (int) (yCenter - (vHeight / 3)) + 1);
            g2d.setColor(waveColor);
            g2d.drawString("wave" + (ch + 1), xoffset + 5, (int) (yCenter - (vHeight / 3)));
            for (int i = 1; i <= 3; i++) {
                g2d.setColor(Color.WHITE);
                g2d.drawLine(vWidth * i, 0, vWidth * i, getHeight());
                g2d.drawLine(0, vHeight * i, getWidth(), vHeight * i);
            }
        }

        // 出力状況
        if (traceShift == false) {
            int x = 5;
            int y = 5;
            int w = 8;
            int h = 8;
            x += ch * 15;
            g2d.setColor(output ? waveColorTable[ch] : Color.DARK_GRAY);
            g2d.fillOval(x, y, w, h);
        }
    }

    public void setWaveColorTable(Color[] waveColorTable) {
        for (int i=0; i<waveColorTable.length; i++) {
            this.waveColorTable[i] = waveColorTable[i];
        }
    }

}
