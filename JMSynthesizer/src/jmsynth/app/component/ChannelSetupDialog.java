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
    private static int toYCord(WaveType type, double f, int overallLeval) {
        return JMSynthFile.toYCord(type, f, overallLeval);
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

            Envelope env = synth.getEnvelope(ch);
            if (env == null) {
                return;
            }

            long ma, md, mr;
            ma = env.getMaxAttackMills();
            md = env.getMaxDecayMills();
            mr = env.getMaxReleaseMills();

            String wave = "";
            double a, d, s, r;
            if (chckbxRealTime.isSelected() == false) {
                wave = getWaveStr(synth.getWaveType(ch));
                a = env.getAttackTime();
                d = env.getDecayTime();
                s = env.getSustainLevel();
                r = env.getReleaseTime();
                paintWave(g, synth.getWaveType(ch), Color.GRAY);
                paintCurve(g, a, d, s, r, ma, md, mr, Color.GRAY);
                paintInfo(g, w - 90, 90, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), wave, Color.GRAY);
            }

            wave = comboBoxWaveType.getSelectedItem().toString();
            a = getAttackSli();
            d = getDecaySli();
            s = getSustainSli();
            r = getReleaseSli();
            paintWave(g, toWaveType(wave), Color.YELLOW);
            paintCurve(g, a, d, s, r, ma, md, mr, Color.GREEN);
            paintInfo(g, w - 90 + 1, 16, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), wave, Color.DARK_GRAY);
            paintInfo(g, w - 90, 15, 10, (int) ((double) ma * a), (int) ((double) md * d), s, (int) ((double) mr * r), wave, Color.GREEN);
        }

        private void paintInfo(Graphics g, int x, int y, int fontSize, int aInt, int dInt, double s, int rInt, String wave, Color strColor) {
            int fx, fy;
            fx = x;
            fy = y;
            final int fspan = 10;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(strColor);
            g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
            g2d.drawString("A: " + aInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("D: " + dInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("S: " + s + "", fx, fy);
            fy += fspan;
            g2d.drawString("R: " + rInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("MOD: " + getModSli() + " ", fx, fy);
//            fy += fspan;
//            g2d.drawString("osc = " + wave, fx, fy);
        }

        private void paintWave(Graphics g, WaveType type, Color lineColor) {
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
            Arrays.fill(data, (byte)0x00);
            int[] xPoint = new int[length];
            int[] yPoint = new int[length];

            int cy = (under - top) / 2;

            for (int i=0; i<length; i++) {
                if ((i < mergin) || (i >= length - mergin)) {
                    xPoint[i] = i;
                    yPoint[i] = cy;
                }
                else {
                    xPoint[i] = i;

                    double f = (double)(i - mergin) / (double)(length - (mergin * 2));
                    int mwy = toYCord(type, f, overallLeval);
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

            //g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawPolyline(xPoint, yPoint, 5);
            //g2d.setStroke(new BasicStroke());
        }
    }

    /**
     * Create the dialog.
     */
    public ChannelSetupDialog(JMSoftSynthesizer synth) {
        this.synth = synth;
        setTitle("Channel setting");
        setBounds(100, 100, 450, 449);
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
        comboBoxWaveType.setModel(
                new DefaultComboBoxModel(WAVE_STR_ITEMS));
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

        sliderA = new JSlider();
        sliderA.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderA.setBounds(49, 200, 373, 26);
        contentPanel.add(sliderA);

        sliderD = new JSlider();
        sliderD.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderD.setBounds(49, 236, 373, 26);
        contentPanel.add(sliderD);

        sliderS = new JSlider();
        sliderS.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderS.setBounds(49, 272, 373, 26);
        contentPanel.add(sliderS);

        sliderR = new JSlider();
        sliderR.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderR.setBounds(49, 308, 373, 26);
        contentPanel.add(sliderR);

        JLabel lblNewLabel = new JLabel("A");
        lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(12, 200, 25, 26);
        contentPanel.add(lblNewLabel);

        JLabel lblD = new JLabel("D");
        lblD.setHorizontalAlignment(SwingConstants.CENTER);
        lblD.setFont(new Font("Dialog", Font.BOLD, 12));
        lblD.setBounds(12, 236, 25, 26);
        contentPanel.add(lblD);

        JLabel lblS = new JLabel("S");
        lblS.setHorizontalAlignment(SwingConstants.CENTER);
        lblS.setFont(new Font("Dialog", Font.BOLD, 12));
        lblS.setBounds(12, 272, 25, 26);
        contentPanel.add(lblS);

        JLabel lblR = new JLabel("R");
        lblR.setHorizontalAlignment(SwingConstants.CENTER);
        lblR.setFont(new Font("Dialog", Font.BOLD, 12));
        lblR.setBounds(12, 308, 25, 26);
        contentPanel.add(lblR);

        sliderMod = new JSlider();
        sliderMod.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue();
                repaint();
            }
        });
        sliderMod.setBounds(49, 344, 373, 26);
        contentPanel.add(sliderMod);

        JLabel lblMod = new JLabel("MOD");
        lblMod.setHorizontalAlignment(SwingConstants.CENTER);
        lblMod.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMod.setBounds(3, 344, 44, 26);
        contentPanel.add(lblMod);
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
        sliderMod.setVisible(true);
        lblMod.setVisible(true);
    }

    private final static int TEST_MIDINUM = 60;
    private int cacheTestChannel = 0;
    private boolean testOn = false;
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

        if (e != null) {
            e.setAttackTime(getAttackSli());
            e.setDecayTime(getDecaySli());
            e.setSustainLevel(getSustainSli());
            e.setReleaseTime(getReleaseSli());
        }
        if (mod != null) {
            mod.setDepth(getModSli());
        }
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

        if (e != null) {
            setAttackSli();
            setDecaySli();
            setSustainSli();
            setReleaseSli();
            setModSli();
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
