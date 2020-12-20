package jmsynth.app.component;

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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.enverope.Envelope;
import jmsynth.oscillator.IOscillator.WaveType;

public class ChannelSetupDialog extends JDialog {

    private JMSoftSynthesizer synth = null;
    private final JPanel contentPanel = new JPanel();
    private JSlider sliderA;
    private JSlider sliderD;
    private JSlider sliderS;
    private JComboBox comboBoxChannel;
    private JComboBox comboBoxWaveType;
    private JSlider sliderR;
    private JButton btnTest;

    //"SAW", "TRIANGLE", "SQUARE", "PULSE", "SINE", "NOIS"
    private static final String WAVE_STR_SAW = "SAW";
    private static final String WAVE_STR_TRIANGLE = "TRIANGLE";
    private static final String WAVE_STR_SQUARE = "SQUARE";
    private static final String WAVE_STR_PULSE = "PULSE";
    private static final String WAVE_STR_SINE = "SINE";
    private static final String WAVE_STR_NOIS = "NOIS";

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
            wave = getWaveStr(synth.getWaveType(ch));
            a = env.getAttackTime();
            d = env.getDecayTime();
            s = env.getSustainLevel();
            r = env.getReleaseTime();
            paintCurve(g, a, d, s, r, ma, md, mr, Color.GRAY);
            paintInfo(g, w - 90, 90, 10, (int)((double)ma * a), (int)((double)md * d), s, (int)((double)mr * r), wave, Color.GRAY);

            wave = comboBoxWaveType.getSelectedItem().toString();
            a = getAttackSli();
            d = getDecaySli();
            s = getSustainSli();
            r = getReleaseSli();
            paintCurve(g, a, d, s, r, ma, md, mr, Color.GREEN);
            paintInfo(g, w - 90 + 1, 16, 10, (int)((double)ma * a), (int)((double)md * d), s, (int)((double)mr * r), wave, Color.DARK_GRAY);
            paintInfo(g, w - 90, 15, 10, (int)((double)ma * a), (int)((double)md * d), s, (int)((double)mr * r), wave, Color.GREEN);
        }

        private void paintInfo(Graphics g, int x, int y, int fontSize, int aInt, int dInt, double s, int rInt, String wave, Color strColor) {
            int fx, fy;
            fx = x;
            fy = y;
            final int fspan = 10;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(strColor);
            g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
            g2d.drawString("a = " + aInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("d = " + dInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("s = " + s + "", fx, fy);
            fy += fspan;
            g2d.drawString("r = " + rInt + " ms", fx, fy);
            fy += fspan;
            g2d.drawString("osc = " + wave, fx, fy);
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

            g2d.drawPolyline(xPoint, yPoint, 5);
        }
    }

    /**
     * Create the dialog.
     */
    public ChannelSetupDialog(JMSoftSynthesizer synth) {
        this.synth = synth;
        setBounds(100, 100, 450, 429);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        comboBoxWaveType = new JComboBox<String>();
        comboBoxWaveType.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                repaint();
            }
        });
        comboBoxWaveType.setModel(new DefaultComboBoxModel(new String[] { WAVE_STR_SAW, WAVE_STR_TRIANGLE, WAVE_STR_SQUARE, WAVE_STR_PULSE, WAVE_STR_SINE, WAVE_STR_NOIS }));
        comboBoxWaveType.setBounds(12, 10, 106, 21);
        contentPanel.add(comboBoxWaveType);

        comboBoxChannel = new JComboBox<String>();
        comboBoxChannel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                reset();
                repaint();
            }
        });
        comboBoxChannel
                .setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
        comboBoxChannel.setBounds(130, 10, 106, 21);
        contentPanel.add(comboBoxChannel);

        EnvelopeViewPanel panelEnvelopeView = new EnvelopeViewPanel();
        panelEnvelopeView.setBounds(12, 41, 410, 149);
        contentPanel.add(panelEnvelopeView);

        sliderA = new JSlider();
        sliderA.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });
        sliderA.setBounds(12, 200, 410, 26);
        contentPanel.add(sliderA);

        sliderD = new JSlider();
        sliderD.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });
        sliderD.setBounds(12, 236, 410, 26);
        contentPanel.add(sliderD);

        sliderS = new JSlider();
        sliderS.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });
        sliderS.setBounds(12, 272, 410, 26);
        contentPanel.add(sliderS);

        sliderR = new JSlider();
        sliderR.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });
        sliderR.setBounds(12, 308, 410, 26);
        contentPanel.add(sliderR);

        btnTest = new JButton("Test");
        btnTest.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int ch = getChannel();
                synth.noteOn(ch, 68, 100);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int ch = getChannel();
                synth.noteOff(ch, 68);
            }
        });
        btnTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        btnTest.setBounds(331, 10, 91, 21);
        contentPanel.add(btnTest);
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
            buttonPane.add(btnReset);
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
    }

    @Override
    public void setVisible(boolean b) {
        if (isVisible() == false && b == true) {
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
        if (e == null) {
            return;
        }

        /*
         * SAW TRIANGLE SQUARE PULSE SINE NOIS
         */
        String sWave = comboBoxWaveType.getSelectedItem().toString();
        if (sWave.equalsIgnoreCase(WAVE_STR_SAW) == true) {
            synth.setOscillator(ch, WaveType.SAW);
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_TRIANGLE) == true) {
            synth.setOscillator(ch, WaveType.TRIANGLE);
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_SQUARE) == true) {
            synth.setOscillator(ch, WaveType.SQUARE);
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_PULSE) == true) {
            synth.setOscillator(ch, WaveType.PULSE);
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_SINE) == true) {
            synth.setOscillator(ch, WaveType.SINE);
        }
        else {
            synth.setOscillator(ch, WaveType.NOISE);
        }

        e.setAttackTime(getAttackSli());
        e.setDecayTime(getDecaySli());
        e.setSustainLevel(getSustainSli());
        e.setReleaseTime(getReleaseSli());
    }

    public void reset() {
        int ch = getChannel();
        Envelope e = synth.getEnvelope(ch);
        if (e == null) {
            return;
        }

        /*
         * SAW TRIANGLE SQUARE PULSE SINE NOIS
         */
        String sWave = getWaveStr(synth.getWaveType(ch));
        comboBoxWaveType.setSelectedItem(sWave);

        setAttackSli();
        setDecaySli();
        setSustainSli();
        setReleaseSli();
    }

    private String getWaveStr(WaveType type) {
        String sWave = "NOIS";
        switch (type) {
            case NOISE:
                sWave = WAVE_STR_NOIS;
                break;
            case PULSE:
                sWave = WAVE_STR_PULSE;
                break;
            case SAW:
                sWave = WAVE_STR_SAW;
                break;
            case SINE:
                sWave = WAVE_STR_SINE;
                break;
            case SQUARE:
                sWave = WAVE_STR_SQUARE;
                break;
            case TRIANGLE:
                sWave = WAVE_STR_TRIANGLE;
                break;
            default:
                break;
        }
        return sWave;
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
}
