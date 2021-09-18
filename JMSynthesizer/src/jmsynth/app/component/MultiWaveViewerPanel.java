package jmsynth.app.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JPanel;

import jmsynth.JMSoftSynthesizer;
import jmsynth.sound.SoundSourceChannel;

public class MultiWaveViewerPanel extends JPanel {

    public static final float WAVE_BOLD = 1.0f;
    public static final int TRACE_VIEW_MODE_DETAIL = 0;
    public static final int TRACE_VIEW_MODE_MERGE = 1;
    public static final int TRACE_VIEW_MODE_SPECT = 2;

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
    public boolean autoRepaintTask = false;
    public boolean visibleAuto = false;
    public boolean[] visibleWave = new boolean[16];
    public int traceViewMode = TRACE_VIEW_MODE_DETAIL;

    private Color[] waveColorTable = null;

    private static boolean hispeedSpectrum = false;
    private boolean hispeedSpectrumRunnable = true;
    private byte[] hWaveBuf = null;
    private double[] hSpetrumBuf = null;
    private Thread spectrumMonitorThread = null;

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

                // 16chをトリガーにして再描画する
                if (autoRepaintTask == true) {
                    repaintWavePane();
                }
            }
        });
        cinit();
    }

    public void repaintWavePane() {
        if (isVisible() == true) {
            repaint();
        }
    }

    private void cinit() {
        // setPreferredSize(new Dimension(240, 150));
        setBackground(BG_COLOR);
        Arrays.fill(visibleWave, true);

        waveColorTable = new Color[WAVE_COLOR.length];
        for (int i = 0; i < waveColorTable.length; i++) {
            waveColorTable[i] = WAVE_COLOR[i];
        }

        if (hispeedSpectrum == true) {
            spectrumMonitorThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (hispeedSpectrumRunnable) {

                        if (hWaveBuf == null) {
                            //System.out.println("a");
                            continue;
                        }
                        try {
                            hSpetrumBuf = DFT(hWaveBuf);
                        } catch (Exception e) {
                        }

//                        try {
//                            Thread.sleep(100);
//                        }
//                        catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            });
            spectrumMonitorThread.start();
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

        if (traceViewMode == TRACE_VIEW_MODE_DETAIL) {
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
        else {
            paintSpectrum(g2d);
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

        if (visibleAuto == true) {
            for (int i = 0; i < d.length; i += 2) {
                if (d[i] != 0) {
                    visibleWave[ch] = true;
                    break;
                }
            }
        }

        // データ点描画
        boolean output = false;
        Color waveColor = waveColorTable[ch];
        int xoffset = 0;
        int yCenter = this.getHeight() / 2;
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();
        int vvHeight = this.getHeight();

        int visibleIndex = ch;
        int col = 4;
        int row = 4;
        int cnt = 0;
        for (int i=0; i<visibleWave.length; i++) {
            if (i == ch) {
                visibleIndex = cnt;
            }
            else {
            }

            if (visibleWave[i] == true) {
                cnt++;
            }
        }
        if (cnt == 0) {
            return;
        }

        switch(cnt) {
            case 1:
                row = 1;
                col = 1;
                break;
            case 2:
                row = 2;
                col = 1;
                break;
            case 3:
            case 4:
                row = 2;
                col = 2;
                break;
            case 5:
            case 6:
                row = 3;
                col = 2;
                break;
            case 7:
            case 8:
            case 9:
                row = 3;
                col = 3;
                break;
            case 10:
            case 11:
            case 12:
                row = 4;
                col = 3;
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            default:
                row = 4;
                col = 4;
                break;
        }

        if (traceViewMode == TRACE_VIEW_MODE_DETAIL) {
            vHeight /= row;
            vvHeight /= row;
            vWidth /= col;
            yCenter = (vHeight / 2) + (vHeight * (visibleIndex / col));
            xoffset = (vWidth) * (visibleIndex % col);
        }
        else {
            waveColor = new Color(waveColorTable[ch].getRed(), waveColorTable[ch].getGreen(), waveColorTable[ch].getBlue(), 160);
        }

        if (visibleWave[ch] == true) {
            if (traceViewMode == TRACE_VIEW_MODE_DETAIL) {
                g2d.setColor(CENTER_COLOR);
                g2d.drawLine(xoffset, yCenter, xoffset + vWidth, yCenter);
            }
        }

        if (d != null && d.length > 2) {
            int length = d.length / 2;
            double xDelta = (double) vWidth / (double) (length);
            double yDelta = vvHeight / 64.0;
            int j = 0;

            int CLIPPING_PX = (vHeight / 2);
            int xPoints[] = new int[length];
            int yPoints[] = new int[length];
            for (int i = 0; i < d.length; i += 2) {
                if (d[i] != 0) {
                    output = true;
                }

                xPoints[j] = xoffset + (int) (xDelta * j);
                yPoints[j] = (int) (yDelta * d[i]) + yCenter;
                if (yPoints[j] > (yCenter + CLIPPING_PX)) {
                    yPoints[j] = (yCenter + CLIPPING_PX);
                }
                else if (yPoints[j] < (yCenter - CLIPPING_PX)) {
                    yPoints[j] = (yCenter - CLIPPING_PX);
                }
                j++;
            }
            if (visibleWave[ch] == true) {
                g2d.setColor(waveColor);
                g2d.setStroke(new BasicStroke(WAVE_BOLD, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
                g2d.drawPolyline(xPoints, yPoints, length);
                g2d.setStroke(new BasicStroke());
            }
        }

        if (visibleWave[ch] == true) {
            if (traceViewMode == TRACE_VIEW_MODE_DETAIL) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString("wave" + (ch + 1), xoffset + 5 + 1, (int) (yCenter - (vHeight / 3)) + 1);
                g2d.setColor(waveColor);
                g2d.drawString("wave" + (ch + 1), xoffset + 5, (int) (yCenter - (vHeight / 3)));
                g2d.setStroke(new BasicStroke(1.5f));
                for (int i = 1; i <= 3; i++) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawLine(vWidth * i, 0, vWidth * i, getHeight());
                    g2d.drawLine(0, vHeight * i, getWidth(), vHeight * i);
                }
                g2d.setStroke(new BasicStroke());
            }
        }

        // 出力状況
        if (traceViewMode != TRACE_VIEW_MODE_DETAIL) {
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
        for (int i = 0; i < waveColorTable.length; i++) {
            this.waveColorTable[i] = waveColorTable[i];
        }
    }

    int width, height;              // グラフ描画領域の幅と高さ
    Insets margin = new Insets(17, 40, 20, 30); // 上, 左, 下, 右
    int nx(double x){ return (int)Math.round(margin.left + x*width); }
    int ny(double y){ return (int)Math.round(margin.top + (1-y)*height); }
    int yVal(double v){ return ny((v - vMin)/ (vMax - vMin)); }
    double vMax = 0.0, vMin = 0.0;
    static double[] spectrumBuf;

    class Complex {
        double r, i;
        public Complex(double re, double im) { r = re; i = im; }
    }

    private double[] DFT(byte[] data) {
        double Pi = Math.PI;
        int N = data.length;
        Complex[] x = new Complex[N];
        double[] amp = new double[N / 2]; // 振幅スペクトル
        for (int i = 0; i < N; i++) {
            x[i] = new Complex((data[i] & 0xFF) - 128, 0);
        }
        for (int n = 0; n < N / 2; n++) {
            double r = 0.0, i = 0.0;
            for (int k = 0; k < N; k++) {
                r += x[k].r * Math.cos(2.0 * Pi * n * k / N) + x[k].i * Math.sin(2.0 * Pi * n * k / N);
                i += x[k].i * Math.cos(2.0 * Pi * n * k / N) - x[k].r * Math.sin(2.0 * Pi * n * k / N);
            }
            amp[n] = Math.sqrt(r * r + i * i); // 振幅スペクトル
            if (amp[n] > vMax)
                vMax = amp[n];
        }
        return amp;
    }

    private void tick(Graphics g, int nX, int nY) {
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getAscent(); // 文字の高さ
        double freq1 = SoundSourceChannel.SAMPLE_RATE / spectrumBuf.length / 2;
        g.setColor(Color.WHITE);
        for (int d = 0; d <= nX; d++) {
            double x = (double)d / nX;
            double freq = freq1 * x * spectrumBuf.length;
            //double time = x * data.length / af.getSampleRate();
            g.drawLine(nx(x), ny(0), nx(x), ny(0)+3);
            String str = String.format("%.2f", freq / 1000);
            if (d == nX) str += "KHz";
            int w = fm.stringWidth(str);
            g.drawString(str, nx(x)-w/2, ny(0)+h+2);
        }
        for (int n = 0; n <= nY; n++) {
            double y = (double)n / nY;
            g.setColor(Color.lightGray);
            g.drawLine(nx(0), ny(y), nx(1.0), ny(y));
            String str = String.format("%.0f", vMin + (vMax-vMin)*y);
            int w = fm.stringWidth(str);
            g.setColor(Color.WHITE);
            g.drawString(str, nx(0)-w-5, ny(y)+h/2);
        }
    }

    public void paintSpectrum(Graphics g) {
        width  = getWidth() - margin.left - margin.right;
        height = getHeight() - margin.top - margin.bottom;
        g.drawRect(margin.left, margin.top, width-1, height-1); // 座標軸描画
        if (d1 == null) {
            return;
        }
        if (hispeedSpectrum == true) {
            hWaveBuf = Arrays.copyOf(d1, d1.length);
            if (hSpetrumBuf == null) {
                return;
            }
            spectrumBuf = Arrays.copyOf(hSpetrumBuf, hSpetrumBuf.length);
        }
        else {
            spectrumBuf = DFT(d1);
        }

        if (spectrumBuf == null) {
            return;
        }
        tick(g, 10, 5);
        g.setColor(Color.red);
        for (int n = 0; n < spectrumBuf.length; n++) {
            double x = (double)n / spectrumBuf.length;
            g.drawLine(nx(x), ny(0), nx(x), yVal(spectrumBuf[n]));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        hispeedSpectrumRunnable = false;
    }
}
