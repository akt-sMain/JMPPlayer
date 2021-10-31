package jmsynth.app.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthFile;
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
    private JButton btnSetup;

    private ChannelSetupDialog setupDialog = null;

    private MidiInterface midiInterface = null;
    private JMSoftSynthesizer softSynth = null;
    private JCheckBox chckbxAutoOscChange;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton rdbtnModeDetail;
    private JRadioButton rdbtnModeSpectrum;
    private JCheckBox chckbxAutoWaveVisible;
    private JRadioButton rdbtnModeMerge;

    private ProgramChangeTableEditDialog tableEditer;

    /**
     * Create the frame.
     * @wbp.parser.constructor
     */
    public WaveViewerFrame(JMSoftSynthesizer synth) {
        setTitle("Synthesizer setting");
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 853, 667);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        softSynth = synth;

        panel = new MultiWaveViewerPanel(synth);
        panel.setBounds(97, 58, 728, 542);
        contentPane.add(panel);

        chckbxWave1 = new JCheckBox("wave1");
        chckbxWave1.setBounds(12, 99, 73, 21);
        contentPane.add(chckbxWave1);

        chckbxWave2 = new JCheckBox("wave2");
        chckbxWave2.setBounds(12, 131, 73, 21);
        contentPane.add(chckbxWave2);

        chckbxWave3 = new JCheckBox("wave3");
        chckbxWave3.setBounds(12, 163, 73, 21);
        contentPane.add(chckbxWave3);

        chckbxWave4 = new JCheckBox("wave4");
        chckbxWave4.setBounds(12, 195, 73, 21);
        contentPane.add(chckbxWave4);

        chckbxWave5 = new JCheckBox("wave5");
        chckbxWave5.setBounds(12, 227, 73, 21);
        contentPane.add(chckbxWave5);

        chckbxWave6 = new JCheckBox("wave6");
        chckbxWave6.setBounds(12, 259, 73, 21);
        contentPane.add(chckbxWave6);

        chckbxWave7 = new JCheckBox("wave7");
        chckbxWave7.setBounds(12, 291, 73, 21);
        contentPane.add(chckbxWave7);

        chckbxWave8 = new JCheckBox("wave8");
        chckbxWave8.setBounds(12, 323, 73, 21);
        contentPane.add(chckbxWave8);

        chckbxWave9 = new JCheckBox("wave9");
        chckbxWave9.setBounds(12, 355, 73, 21);
        contentPane.add(chckbxWave9);

        chckbxWave10 = new JCheckBox("wave10");
        chckbxWave10.setBounds(12, 387, 73, 21);
        contentPane.add(chckbxWave10);

        chckbxWave11 = new JCheckBox("wave11");
        chckbxWave11.setBounds(12, 419, 73, 21);
        contentPane.add(chckbxWave11);

        chckbxWave12 = new JCheckBox("wave12");
        chckbxWave12.setBounds(12, 451, 73, 21);
        contentPane.add(chckbxWave12);

        chckbxWave13 = new JCheckBox("wave13");
        chckbxWave13.setBounds(12, 483, 73, 21);
        contentPane.add(chckbxWave13);

        chckbxWave14 = new JCheckBox("wave14");
        chckbxWave14.setBounds(12, 515, 73, 21);
        contentPane.add(chckbxWave14);

        chckbxWave15 = new JCheckBox("wave15");
        chckbxWave15.setBounds(12, 547, 73, 21);
        contentPane.add(chckbxWave15);

        chckbxWave16 = new JCheckBox("wave16");
        chckbxWave16.setBounds(12, 579, 73, 21);
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
        btnAllOff.setBounds(8, 42, 77, 21);
        contentPane.add(btnAllOff);

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
        btnAllOn.setBounds(8, 10, 77, 21);
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

        btnSetup = new JButton("Setup");
        btnSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupDialog.setLocation(WaveViewerFrame.this.getX() + 20, WaveViewerFrame.this.getY() + 20);
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
        chckbxAutoOscChange.setBounds(463, 26, 141, 21);
        contentPane.add(chckbxAutoOscChange);

        rdbtnModeDetail = new JRadioButton("Detail");
        rdbtnModeDetail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        buttonGroup.add(rdbtnModeDetail);
        rdbtnModeDetail.setSelected(true);
        rdbtnModeDetail.setBounds(97, 26, 113, 21);
        contentPane.add(rdbtnModeDetail);

        rdbtnModeSpectrum = new JRadioButton("Spectrum");
        rdbtnModeSpectrum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        buttonGroup.add(rdbtnModeSpectrum);
        rdbtnModeSpectrum.setBounds(348, 26, 91, 21);
        contentPane.add(rdbtnModeSpectrum);

        rdbtnModeMerge = new JRadioButton("Merge");
        rdbtnModeMerge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        buttonGroup.add(rdbtnModeMerge);
        rdbtnModeMerge.setBounds(214, 26, 113, 21);
        contentPane.add(rdbtnModeMerge);

        chckbxAutoWaveVisible = new JCheckBox("Auto");
        chckbxAutoWaveVisible.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.visibleAuto = chckbxAutoWaveVisible.isSelected();
                boolean cbEnable = true;
                if (chckbxAutoWaveVisible.isSelected() == true) {
                    cbEnable = false;
                }
                else {
                    cbEnable = true;
                }
                chckbxWave1.setEnabled(cbEnable);
                chckbxWave2.setEnabled(cbEnable);
                chckbxWave3.setEnabled(cbEnable);
                chckbxWave4.setEnabled(cbEnable);
                chckbxWave5.setEnabled(cbEnable);
                chckbxWave6.setEnabled(cbEnable);
                chckbxWave7.setEnabled(cbEnable);
                chckbxWave8.setEnabled(cbEnable);
                chckbxWave9.setEnabled(cbEnable);
                chckbxWave10.setEnabled(cbEnable);
                chckbxWave11.setEnabled(cbEnable);
                chckbxWave12.setEnabled(cbEnable);
                chckbxWave13.setEnabled(cbEnable);
                chckbxWave14.setEnabled(cbEnable);
                chckbxWave15.setEnabled(cbEnable);
                chckbxWave16.setEnabled(cbEnable);
                updateMenu();
            }
        });
        chckbxAutoWaveVisible.setBounds(12, 69, 77, 21);
        contentPane.add(chckbxAutoWaveVisible);

        setupDialog = new ChannelSetupDialog(synth);


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
        chckbxAutoWaveVisible.setSelected(true);

        JButton btnSaveButton = new JButton("Save");
        btnSaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                JFileChooser filechooser = new JFileChooser();
                File f = new File(new File("config." + JMSynthFile.EXTENSION_CONFIG).getAbsolutePath());
                filechooser.setSelectedFile(f);

                int selected = filechooser.showSaveDialog(WaveViewerFrame.this);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    ret = filechooser.getSelectedFile();
                }
                if (ret != null) {
                    JMSynthFile.saveSynthConfig(ret, softSynth);
                }
            }
        });
        btnSaveButton.setBounds(734, 610, 91, 21);
        contentPane.add(btnSaveButton);

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                // 確認ダイアログ
                JFileChooser filechooser = new JFileChooser();
                int selected = filechooser.showOpenDialog(WaveViewerFrame.this);
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        ret = filechooser.getSelectedFile();
                        break;
                    default:
                        break;
                }
                if (ret != null) {
                    JMSynthFile.loadSynthConfig(ret, softSynth);
                }
            }
        });
        btnLoad.setBounds(635, 610, 91, 21);
        contentPane.add(btnLoad);

        JButton btnTable = new JButton("Table");
        btnTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableEditer.openDlg(midiInterface.getProgramChangeTable(), softSynth);
            }
        });
        btnTable.setBounds(631, 26, 91, 21);
        contentPane.add(btnTable);

        JButton btnResetAllChannel = new JButton("Reset all channel");
        btnResetAllChannel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                softSynth.initializeAllChannel();

                if (setupDialog.isVisible() == true) {
                    setupDialog.reset();
                    setupDialog.repaint();
                }
            }
        });
        btnResetAllChannel.setBounds(97, 610, 141, 21);
        contentPane.add(btnResetAllChannel);

        rdbtnModeDetail.setVisible(true);
        rdbtnModeSpectrum.setVisible(false);
        rdbtnModeMerge.setVisible(true);

        tableEditer = new ProgramChangeTableEditDialog();

        updateLabel();
    }

    public WaveViewerFrame(MidiInterface iface) {
        this((JMSoftSynthesizer)iface.getSynthController());
        midiInterface = iface;
        chckbxAutoOscChange.setVisible(true);
        updateLabel();
    }

    private void updateMenu() {
        chckbxWave1.setSelected(panel.visibleWave[0]);
        chckbxWave2.setSelected(panel.visibleWave[1]);
        chckbxWave3.setSelected(panel.visibleWave[2]);
        chckbxWave4.setSelected(panel.visibleWave[3]);
        chckbxWave5.setSelected(panel.visibleWave[4]);
        chckbxWave6.setSelected(panel.visibleWave[5]);
        chckbxWave7.setSelected(panel.visibleWave[6]);
        chckbxWave8.setSelected(panel.visibleWave[7]);
        chckbxWave9.setSelected(panel.visibleWave[8]);
        chckbxWave10.setSelected(panel.visibleWave[9]);
        chckbxWave11.setSelected(panel.visibleWave[10]);
        chckbxWave12.setSelected(panel.visibleWave[11]);
        chckbxWave13.setSelected(panel.visibleWave[12]);
        chckbxWave14.setSelected(panel.visibleWave[13]);
        chckbxWave15.setSelected(panel.visibleWave[14]);
        chckbxWave16.setSelected(panel.visibleWave[15]);
    }
    private void updateLabel() {
        if (rdbtnModeDetail.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_DETAIL;
        }
        else if (rdbtnModeMerge.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_MERGE;
        }
        else {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_SPECT;
        }

        panel.visibleAuto = chckbxAutoWaveVisible.isSelected();
        boolean cbEnable = true;
        if (chckbxAutoWaveVisible.isSelected() == true) {
            cbEnable = false;
        }
        else {
            cbEnable = true;
        }
        chckbxWave1.setEnabled(cbEnable);
        chckbxWave2.setEnabled(cbEnable);
        chckbxWave3.setEnabled(cbEnable);
        chckbxWave4.setEnabled(cbEnable);
        chckbxWave5.setEnabled(cbEnable);
        chckbxWave6.setEnabled(cbEnable);
        chckbxWave7.setEnabled(cbEnable);
        chckbxWave8.setEnabled(cbEnable);
        chckbxWave9.setEnabled(cbEnable);
        chckbxWave10.setEnabled(cbEnable);
        chckbxWave11.setEnabled(cbEnable);
        chckbxWave12.setEnabled(cbEnable);
        chckbxWave13.setEnabled(cbEnable);
        chckbxWave14.setEnabled(cbEnable);
        chckbxWave15.setEnabled(cbEnable);
        chckbxWave16.setEnabled(cbEnable);

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

    @Override
    public void setVisible(boolean b) {
        if (b == false) {
            setupDialog.setVisible(false);
            tableEditer.setVisible(false);
        }
        super.setVisible(b);
    }

    public void repaintWavePane() {
        if (isVisible() == true) {
            panel.repaintWavePane();
        }
    }

    public void setAutoRepaintTask(boolean autoRepaint) {
        panel.autoRepaintTask = autoRepaint;
    }
}
