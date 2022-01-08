package jmsynth.app.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthFile;
import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.OscillatorSet.WaveType;

public class ChannelSetupDialog extends JDialog {

    private static final int TEST_NOTE_NUMBER = 60;

    private JMSoftSynthesizer synth = null;
    private final JPanel contentPanel = new JPanel();
    private JSlider sliderA;
    private JSlider sliderD;
    private JSlider sliderS;
    private JComboBox comboBoxChannel;
    private JComboBox comboBoxWaveType;
    private JSlider sliderR;
    private JButton btnTest;

    private static final String[] WAVE_STR_ITEMS = JMSynthFile.WAVE_STR_ITEMS;

    private static WaveType toWaveType(String sWave) {
        return JMSynthFile.toWaveType(sWave);
    }

    private static String getWaveStr(WaveType type) {
        return JMSynthFile.toWaveStr(type);
    }

    private static int toYCord(WaveType type, double f, int overallLeval, boolean isReverse) {
        return JMSynthFile.toYCord(type, f, overallLeval, isReverse);
    }

    private JSlider sliderMod;
    private JCheckBox chckbxRealTime;

    private class EnvelopeViewPanel extends JPanel {

        public EnvelopeViewPanel() {
            super();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            int w = this.getWidth();
            int h = this.getHeight();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, w, h);

            int ch = getChannel();

            long ma, md, mr;
            String wave = "";
            boolean isWaveReverse = false;
            double a, d, s, r;
            int modDepth;
            Envelope env = synth.getEnvelope(ch);
            if (env == null) {
                return;
            }

            Modulator mod = synth.getModulator(ch);
            if (mod == null) {
                return;
            }

            if (chckbxRealTime.isSelected() == false) {
                wave = getWaveStr(synth.getWaveType(ch));
                isWaveReverse = synth.isWaveReverse(ch);
                a = env.getAttackTime();
                d = env.getDecayTime();
                s = env.getSustainLevel();
                r = env.getReleaseTime();
                ma = env.getMaxAttackMills();
                md = env.getMaxDecayMills();
                mr = env.getMaxReleaseMills();
                modDepth = mod.getDepth();
                paintWave(g, synth.getWaveType(ch), isWaveReverse, Color.GRAY);
                paintCurve(g, a, d, s, r, ma, md, mr, Color.GRAY);
                paintInfo(g, w - 110, 90, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), ma, md, mr, modDepth, wave,
                        Color.GRAY);
            }

            wave = comboBoxWaveType.getSelectedItem().toString();
            isWaveReverse = chckbxWaveReverse.isSelected();
            a = getAttackSli();
            d = getDecaySli();
            s = getSustainSli();
            r = getReleaseSli();
            ma = getMaxAttackSli();
            md = getMaxDecaySli();
            mr = getMaxReleaseSli();
            modDepth = getModSli();
            paintWave(g, toWaveType(wave), isWaveReverse, Color.YELLOW);
            paintCurve(g, a, d, s, r, ma, md, mr, Color.MAGENTA);
            paintInfo(g, w - 110 + 1, 16, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), ma, md, mr, modDepth, wave,
                    Color.DARK_GRAY);
            paintInfo(g, w - 110, 15, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), ma, md, mr, modDepth, wave,
                    Color.GREEN);
        }

        private void paintInfo(Graphics g, int x, int y, int fontSize, int aInt, int dInt, double s, int rInt, long maInt, long mdInt, long mrInt, int mod,
                String wave, Color strColor) {
            int fx, fy;
            fx = x;
            fy = y;
            final int fspan = 10;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(strColor);
            g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
            g2d.drawString("A: " + aInt + " ms / " + maInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("D: " + dInt + " ms / " + mdInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("S: " + s + "", fx, fy);
            fy += fspan;
            g2d.drawString("R: " + rInt + " ms / " + mrInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("MOD: " + mod + " ", fx, fy);
            // fy += fspan;
            // g2d.drawString("osc = " + wave, fx, fy);
        }

        private void paintWave(Graphics g, WaveType type, boolean isReverse, Color lineColor) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(lineColor);

            int w = this.getWidth();
            int h = this.getHeight();
            int mergin = 120;
            int overallLeval = 55;
            int left = mergin;
            int right = w - (mergin);
            int top = 2;
            int under = h - 4;

            int length = right - left + (mergin * 2);
            byte[] data = new byte[100];
            Arrays.fill(data, (byte) 0x00);
            int[] xPoint = new int[length];
            int[] yPoint = new int[length];

            int cy = (under - top) / 2;

            for (int i = 0; i < length; i++) {
                if ((i < mergin) || (i >= length - mergin)) {
                    xPoint[i] = i;
                    yPoint[i] = cy;
                }
                else {
                    xPoint[i] = i;

                    double f = (double) (i - mergin) / (double) (length - (mergin * 2));
                    int mwy = toYCord(type, f, overallLeval, isReverse);
                    if (mwy != -1) {
                        yPoint[i] = mwy + cy;
                    }
                    else {
                        yPoint[i] = ((under - top) / 2);
                    }
                }
            }
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawPolyline(xPoint, yPoint, length);
            g2d.setStroke(new BasicStroke());
        }

        private void paintCurve(Graphics g, double at, double dt, double sl, double rt, long ma, long md, long mr, Color lineColor) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(lineColor);

            int w = this.getWidth();
            int h = this.getHeight();

            int maxWidth_A = (int) ((double) w / 4);
            int maxWidth_D = (int) ((double) w / 4);
            int maxWidth_R = (int) ((double) w / 4);

            int left = 2;
            int right = w - 3;
            int top = 2;
            int under = h - 4;

            int a = (int) ((double) maxWidth_A * at);
            int d = (int) ((double) maxWidth_D * dt);
            int s = under - (int) ((double) (under - top) * sl);
            int r = (int) ((double) maxWidth_R * rt);

            int index = 0;
            int[] xPoint = new int[5];
            int[] yPoint = new int[5];
            xPoint[index] = left;
            yPoint[index] = under;
            index++;

            // attack
            xPoint[index] = xPoint[index - 1] + a;
            yPoint[index] = ((a <= 0) && (d <= 0)) ? s : top;
            index++;

            // decay
            xPoint[index] = xPoint[index - 1] + d;
            yPoint[index] = s;
            index++;

            // sustain
            if (r <= 0) {
                xPoint[index] = right;
            }
            else {
                xPoint[index] = xPoint[index - 1] + ((right - 1) - (a + d)) - r;
            }
            yPoint[index] = s;
            index++;

            // release
            xPoint[index] = right;
            yPoint[index] = under;
            index++;

            // g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawPolyline(xPoint, yPoint, 5);
            // g2d.setStroke(new BasicStroke());
        }
    }

    /**
     * Create the dialog.
     */
    public ChannelSetupDialog(JMSoftSynthesizer synth) {
        this.synth = synth;
        setTitle("Channel setting");
        setBounds(100, 100, 485, 518);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        comboBoxWaveType = new JComboBox<String>();
        comboBoxWaveType.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateValue();
                repaint();
            }
        });
        comboBoxWaveType.setModel(new DefaultComboBoxModel(WAVE_STR_ITEMS));
        comboBoxWaveType.setBounds(12, 10, 136, 21);
        contentPanel.add(comboBoxWaveType);

        comboBoxChannel = new JComboBox<String>();
        comboBoxChannel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (testOn == true) {
                    testSoundAction();
                }

                boolean bp = chckbxRealTime.isSelected();
                chckbxRealTime.setSelected(false);
                reset();
                chckbxRealTime.setSelected(bp);
                repaint();
            }
        });
        comboBoxChannel
                .setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
        comboBoxChannel.setBounds(160, 10, 106, 21);
        contentPanel.add(comboBoxChannel);

        EnvelopeViewPanel panelEnvelopeView = new EnvelopeViewPanel();
        panelEnvelopeView.setBounds(12, 41, 410, 149);
        contentPanel.add(panelEnvelopeView);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(12, 203, 445, 235);
        contentPanel.add(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Envelope", null, panel, null);
        panel.setLayout(null);

        sliderR = new JSlider();
        sliderR.setBounds(49, 90, 373, 26);
        panel.add(sliderR);

        JLabel lblR = new JLabel("R");
        lblR.setBounds(12, 90, 25, 26);
        panel.add(lblR);
        lblR.setHorizontalAlignment(SwingConstants.CENTER);
        lblR.setFont(new Font("Dialog", Font.BOLD, 12));

        sliderS = new JSlider();
        sliderS.setBounds(49, 61, 373, 26);
        panel.add(sliderS);

        JLabel lblS = new JLabel("S");
        lblS.setBounds(12, 61, 25, 26);
        panel.add(lblS);
        lblS.setHorizontalAlignment(SwingConstants.CENTER);
        lblS.setFont(new Font("Dialog", Font.BOLD, 12));

        sliderD = new JSlider();
        sliderD.setBounds(49, 32, 373, 26);
        panel.add(sliderD);

        JLabel lblD = new JLabel("D");
        lblD.setBounds(12, 32, 25, 26);
        panel.add(lblD);
        lblD.setHorizontalAlignment(SwingConstants.CENTER);
        lblD.setFont(new Font("Dialog", Font.BOLD, 12));

        sliderA = new JSlider();
        sliderA.setBounds(49, 3, 373, 26);
        panel.add(sliderA);

        JLabel lblNewLabel = new JLabel("A");
        lblNewLabel.setBounds(12, 3, 25, 26);
        panel.add(lblNewLabel);
        lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sliderMA = new JSlider();
        sliderMA.setMinimum(Envelope.MIN_MILLS);
        sliderMA.setMaximum(Envelope.MAX_MILLS);
        sliderMA.setBounds(49, 119, 373, 26);
        panel.add(sliderMA);

        JLabel lblMa = new JLabel("MA");
        lblMa.setHorizontalAlignment(SwingConstants.CENTER);
        lblMa.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMa.setBounds(12, 119, 25, 26);
        panel.add(lblMa);

        sliderMD = new JSlider();
        sliderMD.setMinimum(Envelope.MIN_MILLS);
        sliderMD.setMaximum(Envelope.MAX_MILLS);
        sliderMD.setBounds(49, 148, 373, 26);
        panel.add(sliderMD);

        JLabel lblMd = new JLabel("MD");
        lblMd.setHorizontalAlignment(SwingConstants.CENTER);
        lblMd.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMd.setBounds(12, 148, 25, 26);
        panel.add(lblMd);

        sliderMR = new JSlider();
        sliderMR.setMinimum(Envelope.MIN_MILLS);
        sliderMR.setMaximum(Envelope.MAX_MILLS);
        sliderMR.setBounds(49, 177, 373, 26);
        panel.add(sliderMR);

        JLabel lblMr = new JLabel("MR");
        lblMr.setHorizontalAlignment(SwingConstants.CENTER);
        lblMr.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMr.setBounds(12, 177, 25, 26);
        panel.add(lblMr);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Modulator", null, panel_1, null);
        panel_1.setLayout(null);

        sliderMod = new JSlider();
        sliderMod.setBounds(58, 10, 373, 26);
        panel_1.add(sliderMod);

        JLabel lblMod = new JLabel("MOD");
        lblMod.setBounds(12, 10, 44, 26);
        panel_1.add(lblMod);
        lblMod.setHorizontalAlignment(SwingConstants.CENTER);
        lblMod.setFont(new Font("Dialog", Font.BOLD, 12));

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Other", null, panel_2, null);
        panel_2.setLayout(null);

        chckbxWaveReverse = new JCheckBox("Wave reverse");
        chckbxWaveReverse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateValue();
                repaint();
            }
        });
        chckbxWaveReverse.setBounds(8, 6, 103, 21);
        panel_2.add(chckbxWaveReverse);

        checkBoxFESSim = new JCheckBox("FES Simulate");
        checkBoxFESSim.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateValue();
                repaint();
            }
        });
        checkBoxFESSim.setBounds(8, 40, 103, 21);
        panel_2.add(checkBoxFESSim);
        lblMod.setVisible(true);
        sliderMod.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderMod.setVisible(true);
        sliderA.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderD.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderS.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderR.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderMA.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderMD.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderMR.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);

            JButton btnReset = new JButton("Reset");
            btnReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reset();
                    repaint();
                }
            });

            chckbxRealTime = new JCheckBox("Real time");
            chckbxRealTime.setSelected(false);
            chckbxRealTime.setVisible(false);
            buttonPane.add(chckbxRealTime);

            btnTest = new JButton("Test");
            buttonPane.add(btnTest);
            btnTest.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    testSoundAction();
                }
            });
            btnTest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });
            {
                JButton okButton = new JButton("Sync");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        sync();
                        repaint();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            buttonPane.add(btnReset);
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }

        /* ※MODはノイズが起きるため現状非表示 */
    }

    private final static int TEST_MIDINUM = 60;
    private int cacheTestChannel = 0;
    private boolean testOn = false;
    private JSlider sliderMA;
    private JSlider sliderMD;
    private JSlider sliderMR;
    private JCheckBox chckbxWaveReverse;
    private JCheckBox checkBoxFESSim;

    private void testSoundAction() {
        if (testOn == false) {
            testSoundOn();
        }
        else {
            testSoundOff();
        }
        testOn = !testOn;
    }

    private void testSoundOn() {
        cacheTestChannel = getChannel();
        synth.noteOn(cacheTestChannel, TEST_MIDINUM, 100);
    }

    private void testSoundOff() {
        synth.noteOff(cacheTestChannel, TEST_MIDINUM);
    }

    @Override
    public void setVisible(boolean b) {
        if (isVisible() == false && b == true) {
            chckbxRealTime.setSelected(false);
            reset();
        }
        super.setVisible(b);
    }

    public int getChannel() {
        String sCh = comboBoxChannel.getSelectedItem().toString();
        int ch = 0;
        try {
            ch = Integer.parseInt(sCh) - 1;
        }
        catch (Exception e) {
            ch = 0;
        }
        return ch;
    }

    public void sync() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        Modulator mod = synth.getModulator(ch);

        /*
         * SAW TRIANGLE SQUARE PULSE SINE NOIS
         */
        String sWave = comboBoxWaveType.getSelectedItem().toString();
        synth.setOscillator(ch, toWaveType(sWave));

        boolean waveReverse = chckbxWaveReverse.isSelected();
        synth.setWaveReverse(ch, waveReverse);

        boolean fesSim = checkBoxFESSim.isSelected();
        synth.setValidFesSimulate(ch, fesSim);

        if (e != null) {
            e.setAttackTime(getAttackSli());
            e.setDecayTime(getDecaySli());
            e.setSustainLevel(getSustainSli());
            e.setReleaseTime(getReleaseSli());
            e.setMaxAttackMills(getMaxAttackSli());
            e.setMaxDecayMills(getMaxDecaySli());
            e.setMaxReleaseMills(getMaxReleaseSli());
        }
        if (mod != null) {
            mod.setDepth(getModSli());
        }
        reset();
    }

    public void reset() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        Modulator mod = synth.getModulator(ch);

        /*
         * SAW TRIANGLE SQUARE PULSE SINE NOIS
         */
        String sWave = getWaveStr(synth.getWaveType(ch));
        comboBoxWaveType.setSelectedItem(sWave);

        chckbxWaveReverse.setSelected(synth.isWaveReverse(ch));

        checkBoxFESSim.setSelected(synth.isValidFesSimulate(ch));

        if (e != null) {
            setAttackSli();
            setDecaySli();
            setSustainSli();
            setReleaseSli();
            setModSli();
            setMaxAttackSli();
            setMaxDecaySli();
            setMaxReleaseSli();
        }
        if (mod != null) {
            setModSli();
        }
    }

    private void setSliderInt(JSlider sl, int val) {
        int max = sl.getMaximum();
        if (max < val) {
            val = max;
        }
        sl.setValue(val);
        return;
    }

    private int getSliderInt(JSlider sl) {
        int val = sl.getValue();
        return val;
    }

    private void setSliderLong(JSlider sl, long val) {
        int max = sl.getMaximum();
        if (max < val) {
            val = max;
        }
        sl.setValue((int) val);
        return;
    }

    private long getSliderLong(JSlider sl) {
        long val = sl.getValue();
        return val;
    }

    private void setSliderDouble(JSlider sl, double val) {
        int max = sl.getMaximum();
        int iVal = (int) (val * 100.0);
        if (max < iVal) {
            iVal = max;
        }
        sl.setValue(iVal);
        return;
    }

    private double getSliderDouble(JSlider sl) {
        int max = sl.getMaximum();
        int val = sl.getValue();
        return (double) val / (double) max;
    }

    public void setAttackSli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderDouble(sliderA, e.getAttackTime());
        return;
    }

    public double getAttackSli() {
        return getSliderDouble(sliderA);
    }

    public void setDecaySli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderDouble(sliderD, e.getDecayTime());
        return;
    }

    public double getDecaySli() {
        return getSliderDouble(sliderD);
    }

    public void setSustainSli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderDouble(sliderS, e.getSustainLevel());
        return;
    }

    public double getSustainSli() {
        return getSliderDouble(sliderS);
    }

    public void setReleaseSli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderDouble(sliderR, e.getReleaseTime());
        return;
    }

    public double getReleaseSli() {
        return getSliderDouble(sliderR);
    }

    public void setMaxAttackSli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderLong(sliderMA, e.getMaxAttackMills());
        return;
    }

    public long getMaxAttackSli() {
        return getSliderLong(sliderMA);
    }

    public void setMaxDecaySli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderLong(sliderMD, e.getMaxDecayMills());
        return;
    }

    public long getMaxDecaySli() {
        return getSliderLong(sliderMD);
    }

    public void setMaxReleaseSli() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }
        setSliderLong(sliderMR, e.getMaxReleaseMills());
        return;
    }

    public long getMaxReleaseSli() {
        return getSliderLong(sliderMR);
    }

    public void setModSli() {
        int ch = getChannel();
        Modulator m = synth.getModulator(ch);
        if (m == null) {
            return;
        }
        setSliderInt(sliderMod, m.getDepth());
        return;
    }

    public int getModSli() {
        return getSliderInt(sliderMod);
    }

    private void updateValue() {
        if (chckbxRealTime.isSelected() == true) {
            sync();
        }
    }
}
