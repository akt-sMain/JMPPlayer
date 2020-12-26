package jmsynth.app.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.midi.MidiInterface;

public class WaveViewerFrame extends JFrame implements ActionListener{

    private JPanel contentPane;
    protected MultiWaveViewerPanel panel;
    private JCheckBox chckbxWave1;
    private JCheckBox chckbxWave2;
    private JCheckBox chckbxWave3;
    private JCheckBox chckbxWave4;
    private JCheckBox chckbxWave5;
    private JCheckBox chckbxWave6;
    private JCheckBox chckbxWave7;
    private JCheckBox chckbxWave8;
    private JCheckBox chckbxWave9;
    private JCheckBox chckbxWave10;
    private JCheckBox chckbxWave11;
    private JCheckBox chckbxWave12;
    private JCheckBox chckbxWave13;
    private JCheckBox chckbxWave14;
    private JCheckBox chckbxWave15;
    private JCheckBox chckbxWave16;
    private JButton btnAllOff;
    private JCheckBox chckbxTraceshift;
    private JButton btnSetup;

    private ChannelSetupDialog setupDialog = null;

    private MidiInterface midiInterface = null;
    private JMSoftSynthesizer softSynth = null;
    private JCheckBox chckbxAutoOscChange;

    /**
     * Create the frame.
     * @wbp.parser.constructor
     */
    public WaveViewerFrame(JMSoftSynthesizer synth) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 853, 649);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        softSynth = synth;

        panel = new MultiWaveViewerPanel(synth);
        panel.setBounds(97, 58, 728, 542);
        contentPane.add(panel);

        chckbxWave1 = new JCheckBox("wave1");
        chckbxWave1.setBounds(16, 85, 73, 21);
        contentPane.add(chckbxWave1);

        chckbxWave2 = new JCheckBox("wave2");
        chckbxWave2.setBounds(16, 117, 73, 21);
        contentPane.add(chckbxWave2);

        chckbxWave3 = new JCheckBox("wave3");
        chckbxWave3.setBounds(16, 149, 73, 21);
        contentPane.add(chckbxWave3);

        chckbxWave4 = new JCheckBox("wave4");
        chckbxWave4.setBounds(16, 181, 73, 21);
        contentPane.add(chckbxWave4);

        chckbxWave5 = new JCheckBox("wave5");
        chckbxWave5.setBounds(16, 213, 73, 21);
        contentPane.add(chckbxWave5);

        chckbxWave6 = new JCheckBox("wave6");
        chckbxWave6.setBounds(16, 245, 73, 21);
        contentPane.add(chckbxWave6);

        chckbxWave7 = new JCheckBox("wave7");
        chckbxWave7.setBounds(16, 277, 73, 21);
        contentPane.add(chckbxWave7);

        chckbxWave8 = new JCheckBox("wave8");
        chckbxWave8.setBounds(16, 309, 73, 21);
        contentPane.add(chckbxWave8);

        chckbxWave9 = new JCheckBox("wave9");
        chckbxWave9.setBounds(16, 341, 73, 21);
        contentPane.add(chckbxWave9);

        chckbxWave10 = new JCheckBox("wave10");
        chckbxWave10.setBounds(16, 373, 73, 21);
        contentPane.add(chckbxWave10);

        chckbxWave11 = new JCheckBox("wave11");
        chckbxWave11.setBounds(16, 405, 73, 21);
        contentPane.add(chckbxWave11);

        chckbxWave12 = new JCheckBox("wave12");
        chckbxWave12.setBounds(16, 437, 73, 21);
        contentPane.add(chckbxWave12);

        chckbxWave13 = new JCheckBox("wave13");
        chckbxWave13.setBounds(16, 469, 73, 21);
        contentPane.add(chckbxWave13);

        chckbxWave14 = new JCheckBox("wave14");
        chckbxWave14.setBounds(16, 501, 73, 21);
        contentPane.add(chckbxWave14);

        chckbxWave15 = new JCheckBox("wave15");
        chckbxWave15.setBounds(16, 533, 73, 21);
        contentPane.add(chckbxWave15);

        chckbxWave16 = new JCheckBox("wave16");
        chckbxWave16.setBounds(16, 565, 73, 21);
        contentPane.add(chckbxWave16);

        btnAllOff = new JButton("All Off");
        btnAllOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean bb = false;
                chckbxWave1.setSelected(bb);
                chckbxWave2.setSelected(bb);
                chckbxWave3.setSelected(bb);
                chckbxWave4.setSelected(bb);
                chckbxWave5.setSelected(bb);
                chckbxWave6.setSelected(bb);
                chckbxWave7.setSelected(bb);
                chckbxWave8.setSelected(bb);
                chckbxWave9.setSelected(bb);
                chckbxWave10.setSelected(bb);
                chckbxWave11.setSelected(bb);
                chckbxWave12.setSelected(bb);
                chckbxWave13.setSelected(bb);
                chckbxWave14.setSelected(bb);
                chckbxWave15.setSelected(bb);
                chckbxWave16.setSelected(bb);
                updateLabel();
            }
        });
        btnAllOff.setBounds(12, 58, 77, 21);
        contentPane.add(btnAllOff);

        chckbxTraceshift = new JCheckBox("detail");
        chckbxTraceshift.addActionListener(this);
        chckbxTraceshift.setBounds(97, 26, 103, 21);
        contentPane.add(chckbxTraceshift);

        JButton btnAllOn = new JButton("All ON");
        btnAllOn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean bb = true;
                chckbxWave1.setSelected(bb);
                chckbxWave2.setSelected(bb);
                chckbxWave3.setSelected(bb);
                chckbxWave4.setSelected(bb);
                chckbxWave5.setSelected(bb);
                chckbxWave6.setSelected(bb);
                chckbxWave7.setSelected(bb);
                chckbxWave8.setSelected(bb);
                chckbxWave9.setSelected(bb);
                chckbxWave10.setSelected(bb);
                chckbxWave11.setSelected(bb);
                chckbxWave12.setSelected(bb);
                chckbxWave13.setSelected(bb);
                chckbxWave14.setSelected(bb);
                chckbxWave15.setSelected(bb);
                chckbxWave16.setSelected(bb);
                updateLabel();
            }
        });
        btnAllOn.setBounds(12, 26, 77, 21);
        contentPane.add(btnAllOn);

        chckbxWave1.addActionListener(this);
        chckbxWave2.addActionListener(this);
        chckbxWave3.addActionListener(this);
        chckbxWave4.addActionListener(this);
        chckbxWave5.addActionListener(this);
        chckbxWave6.addActionListener(this);
        chckbxWave7.addActionListener(this);
        chckbxWave8.addActionListener(this);
        chckbxWave9.addActionListener(this);
        chckbxWave10.addActionListener(this);
        chckbxWave11.addActionListener(this);
        chckbxWave12.addActionListener(this);
        chckbxWave13.addActionListener(this);
        chckbxWave14.addActionListener(this);
        chckbxWave15.addActionListener(this);
        chckbxWave16.addActionListener(this);

        boolean bb = true;
        chckbxWave1.setSelected(bb);
        chckbxWave2.setSelected(bb);
        chckbxWave3.setSelected(bb);
        chckbxWave4.setSelected(bb);
        chckbxWave5.setSelected(bb);
        chckbxWave6.setSelected(bb);
        chckbxWave7.setSelected(bb);
        chckbxWave8.setSelected(bb);
        chckbxWave9.setSelected(bb);
        chckbxWave10.setSelected(bb);
        chckbxWave11.setSelected(bb);
        chckbxWave12.setSelected(bb);
        chckbxWave13.setSelected(bb);
        chckbxWave14.setSelected(bb);
        chckbxWave15.setSelected(bb);
        chckbxWave16.setSelected(bb);
        chckbxTraceshift.setSelected(bb);

        btnSetup = new JButton("Setup");
        btnSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupDialog.setVisible(true);
            }
        });
        btnSetup.setBounds(734, 26, 91, 21);
        contentPane.add(btnSetup);

        chckbxAutoOscChange = new JCheckBox("Auto tone change");
        chckbxAutoOscChange.setVisible(false);
        chckbxAutoOscChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (midiInterface != null) {
                    midiInterface.setAutoSelectOscillator(chckbxAutoOscChange.isSelected());
                }
            }
        });
        chckbxAutoOscChange.setBounds(204, 26, 141, 21);
        contentPane.add(chckbxAutoOscChange);

        setupDialog = new ChannelSetupDialog(synth);
        updateLabel();
    }

    public WaveViewerFrame(MidiInterface iface) {
        this((JMSoftSynthesizer)iface.getSynthController());
        midiInterface = iface;
        chckbxAutoOscChange.setVisible(true);
        updateLabel();
    }

    private void updateLabel() {
        panel.traceShift = chckbxTraceshift.isSelected();
        panel.visibleWave[0] = chckbxWave1.isSelected();
        panel.visibleWave[1] = chckbxWave2.isSelected();
        panel.visibleWave[2] = chckbxWave3.isSelected();
        panel.visibleWave[3] = chckbxWave4.isSelected();
        panel.visibleWave[4] = chckbxWave5.isSelected();
        panel.visibleWave[5] = chckbxWave6.isSelected();
        panel.visibleWave[6] = chckbxWave7.isSelected();
        panel.visibleWave[7] = chckbxWave8.isSelected();
        panel.visibleWave[8] = chckbxWave9.isSelected();
        panel.visibleWave[9] = chckbxWave10.isSelected();
        panel.visibleWave[10] = chckbxWave11.isSelected();
        panel.visibleWave[11] = chckbxWave12.isSelected();
        panel.visibleWave[12] = chckbxWave13.isSelected();
        panel.visibleWave[13] = chckbxWave14.isSelected();
        panel.visibleWave[14] = chckbxWave15.isSelected();
        panel.visibleWave[15] = chckbxWave16.isSelected();
        if (midiInterface != null) {
            chckbxAutoOscChange.setSelected(midiInterface.isAutoSelectOscillator());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateLabel();
    }

    public void setWaveColorTable(Color[] waveColorTable) {
        panel.setWaveColorTable(waveColorTable);
    }
}
