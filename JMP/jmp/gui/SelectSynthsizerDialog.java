package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import function.Utility;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;
import jmp.player.MidiPlayer;
import jmsynth.JMSynthEngine;
import jmsynth.midi.MidiInterface;

public class SelectSynthsizerDialog extends JMPDialog {

    private boolean startupFlag = false;
    private boolean isOkActionClose = false;

    public static final int MAX_ROW_COUNT = 15;

    private SelectSynthsizerDialogListener commitListener = null;
    private MidiDevice.Info[] infosOfRecv = null;
    private MidiDevice.Info[] infosOfTrans = null;
    private static final String NameNoSepareter = "-";

    private final JPanel contentPanel = new JPanel();
    private JLabel lblMidiDevices = new JLabel("MIDI OUT Device");
    private JLabel lblDebugmode;
    JComboBox<String> comboRecvMode;
    private JLabel lblVendorOfRecv;
    private JLabel lblVersionOfRecv;
    private JLabel lblDescriptionOfRecv;
    private JLabel lblPortOfRecv;
    private JTabbedPane tabbedPane;
    private JPanel panel;
    private JPanel panel_1;
    private JLabel labelMidiOutDevice;
    private JComboBox<String> comboTransMode;
    private JLabel lblVendorOfTrans;
    private JLabel lblVersionOfTrans;
    private JLabel lblDescriptionOfTrans;
    private JLabel lblPortOfTrans;

    public static final String DEFAULT_ITEM_NAME = "Using Default";
    public static final String JMSYNTH_ITEM_NAME = "Built-in soft synthesizer";
    public static final int NUMBER_OF_CUSTOM_SYNTH = 2;

    private JLabel labelDevelop;
    private JCheckBox chckbxStartupShowDialog;

    /**
     * コンストラクタ
     */
    public SelectSynthsizerDialog(boolean isVisibleMidiOut, boolean isVisibleMidiIn) {
        /*
         * Window Builder
         */
        super();
        setAlwaysOnTop(true);
        setFont(new Font("Dialog", Font.BOLD, 12));
        setResizable(false);
        setTitle("シンセサイザー選択");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 465, 275);
        getContentPane().setLayout(new BorderLayout());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_MIDI_SETUP, this);

        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(null);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        lblDebugmode = new JLabel("DebugMode");
        lblDebugmode.setBounds(12, 86, 410, 13);
        {
            JButton okButton = new JButton("OK");
            okButton.setBounds(359, 211, 84, 21);
            contentPanel.add(okButton);
            okButton.setBackground(Color.WHITE);
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);

            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setBackground(Color.DARK_GRAY);
            tabbedPane.setBounds(12, 10, 431, 195);
            contentPanel.add(tabbedPane);

            panel = new JPanel();
            panel.setBackground(Color.WHITE);
            tabbedPane.addTab("MIDI OUT", null, panel, null);
            tabbedPane.setBackgroundAt(0, Color.LIGHT_GRAY);
            panel.setLayout(null);

            comboRecvMode = new JComboBox<String>();
            comboRecvMode.setBounds(12, 33, 402, 19);
            comboRecvMode.setMaximumRowCount(MAX_ROW_COUNT);
            panel.add(comboRecvMode);

            lblVendorOfRecv = new JLabel("Vendor : ");
            lblVendorOfRecv.setBounds(12, 62, 410, 13);
            panel.add(lblVendorOfRecv);
            lblVendorOfRecv.setForeground(Color.DARK_GRAY);

            lblVersionOfRecv = new JLabel("Version : ");
            lblVersionOfRecv.setBounds(12, 85, 410, 13);
            panel.add(lblVersionOfRecv);
            lblVersionOfRecv.setForeground(Color.DARK_GRAY);

            lblDescriptionOfRecv = new JLabel("Description : ");
            lblDescriptionOfRecv.setVerticalAlignment(SwingConstants.TOP);
            lblDescriptionOfRecv.setBounds(22, 115, 400, 43);
            panel.add(lblDescriptionOfRecv);
            lblDescriptionOfRecv.setForeground(Color.DARK_GRAY);

            lblPortOfRecv = new JLabel("");
            lblPortOfRecv.setBounds(12, 131, 249, 13);
            panel.add(lblPortOfRecv);
            lblPortOfRecv.setForeground(Color.LIGHT_GRAY);
            lblMidiDevices.setBounds(12, 10, 210, 13);
            panel.add(lblMidiDevices);
            lblMidiDevices.setForeground(Color.BLACK);

            panel_1 = new JPanel();
            panel_1.setBackground(Color.WHITE);
            tabbedPane.addTab("MIDI IN", null, panel_1, null);
            panel_1.setLayout(null);

            labelMidiOutDevice = new JLabel("MIDI IN Device");
            labelMidiOutDevice.setForeground(Color.BLACK);
            labelMidiOutDevice.setBounds(12, 10, 210, 13);
            panel_1.add(labelMidiOutDevice);

            comboTransMode = new JComboBox<String>();
            comboTransMode.setBounds(12, 33, 402, 19);
            comboTransMode.setMaximumRowCount(MAX_ROW_COUNT);
            panel_1.add(comboTransMode);

            lblVendorOfTrans = new JLabel("Vendor : ");
            lblVendorOfTrans.setForeground(Color.DARK_GRAY);
            lblVendorOfTrans.setBounds(12, 62, 410, 13);
            panel_1.add(lblVendorOfTrans);

            lblVersionOfTrans = new JLabel("Version : ");
            lblVersionOfTrans.setForeground(Color.DARK_GRAY);
            lblVersionOfTrans.setBounds(12, 85, 410, 13);
            panel_1.add(lblVersionOfTrans);

            lblDescriptionOfTrans = new JLabel("Description : ");
            lblDescriptionOfTrans.setForeground(Color.DARK_GRAY);
            lblDescriptionOfTrans.setVerticalAlignment(SwingConstants.TOP);
            lblDescriptionOfTrans.setBounds(22, 115, 400, 43);
            panel_1.add(lblDescriptionOfTrans);

            lblPortOfTrans = new JLabel("");
            lblPortOfTrans.setForeground(Color.LIGHT_GRAY);
            lblPortOfTrans.setBounds(12, 131, 249, 13);
            panel_1.add(lblPortOfTrans);

            labelDevelop = new JLabel("開発者モード");
            labelDevelop.setForeground(Color.YELLOW);
            labelDevelop.setHorizontalAlignment(SwingConstants.RIGHT);
            labelDevelop.setBounds(339, 10, 104, 13);
            contentPanel.add(labelDevelop);

            chckbxStartupShowDialog = new JCheckBox("起動時に毎回表示するか");
            chckbxStartupShowDialog.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JMPCore.getDataManager().setShowStartupDeviceSetup(chckbxStartupShowDialog.isSelected());
                }
            });
            chckbxStartupShowDialog.setBackground(Color.DARK_GRAY);
            chckbxStartupShowDialog.setForeground(Color.WHITE);
            chckbxStartupShowDialog.setBounds(12, 211, 310, 21);
            contentPanel.add(chckbxStartupShowDialog);
            comboRecvMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        String listName = comboRecvMode.getSelectedItem().toString().trim();
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "";
                        String port = "";

                        if (listName.equals(DEFAULT_ITEM_NAME) == true) {
                            vendor += "";
                            version += "";
                            description += "Automatically select an available synthesizer.";
                        }
                        else if (listName.equals(JMSYNTH_ITEM_NAME) == true) {
                            vendor += "Akt";
                            version += jmsynth.Version.NO;
                            description += "Self-made built-in 8bit tune synthesizer.";
                        }
                        else {
                            MidiDevice dev = MidiSystem.getMidiDevice(infosOfRecv[devIndex]);
                            vendor += dev.getDeviceInfo().getVendor();
                            version += dev.getDeviceInfo().getVersion();
                            description += dev.getDeviceInfo().getDescription();
                        }

                        lblVendorOfRecv.setText(vendor);
                        lblVersionOfRecv.setText(version);
                        lblDescriptionOfRecv.setText(description);
                        lblPortOfRecv.setText(port);
                    }
                    catch (Exception e1) {
                    }
                    finally {
                    }
                }
            });
            comboTransMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        String listName = comboTransMode.getSelectedItem().toString().trim();
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "";
                        String port = "";

                        if (listName.equals("") == false) {
                            MidiDevice dev = MidiSystem.getMidiDevice(infosOfTrans[devIndex]);
                            vendor += dev.getDeviceInfo().getVendor();
                            version += dev.getDeviceInfo().getVersion();
                            description += dev.getDeviceInfo().getDescription();
                        }

                        lblVendorOfTrans.setText(vendor);
                        lblVersionOfTrans.setText(version);
                        lblDescriptionOfTrans.setText(description);
                        lblPortOfTrans.setText(port);
                    }
                    catch (Exception e1) {
                    }
                    finally {
                    }
                }
            });
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    commit();
                }
            });
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(Color.DARK_GRAY);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(null);
        }

        {
            if (isVisibleMidiOut == false) {
                tabbedPane.removeTabAt(0);
            }
            if (isVisibleMidiIn == false && isVisibleMidiOut == true) {
                tabbedPane.removeTabAt(1);
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            if (JMPCore.isFinishedInitialize() == true) {
                if (SoundManager.SMidiPlayer.isRunnable() == true) {
                    SoundManager.SMidiPlayer.stop();
                }
                if (JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
                    // スタンドアロン・ライブラリは非表示
                    chckbxStartupShowDialog.setVisible(false);
                }
                else {
                    chckbxStartupShowDialog.setVisible(true);
                }
                chckbxStartupShowDialog.setSelected(JMPCore.getDataManager().isShowStartupDeviceSetup());
            }
            updateLanguage();
        }
        super.setVisible(b);
    }

    @Override
    public void showWindow() {
        start();
    }

    @Override
    public void hideWindow() {
        close();
    }

    public void start() {
        if (JMPCore.isFinishedInitialize() == true) {
            if (SoundManager.SMidiPlayer.isRunnable() == true) {
                SoundManager.SMidiPlayer.stop();
            }
        }

        isOkActionClose = false;

        if (JMPFlags.DebugMode == true) {
            labelDevelop.setText("開発者モード");
        }
        else {
            labelDevelop.setText("");
        }

        // レシーバー
        comboRecvMode.removeAllItems();
        infosOfRecv = MidiPlayer.getMidiDeviceInfo(false, true);
        comboRecvMode.addItem(DEFAULT_ITEM_NAME);
        comboRecvMode.addItem(JMSYNTH_ITEM_NAME);
        for (int i = 0; i < infosOfRecv.length; i++) {
            String line = createItemName(i, infosOfRecv[i].getName());
            comboRecvMode.addItem(line);
        }

        // トランスミッター
        comboTransMode.removeAllItems();
        infosOfTrans = MidiPlayer.getMidiDeviceInfo(true, false);
        comboTransMode.addItem("");
        for (int i = 0; i < infosOfTrans.length; i++) {
            String line = createItemName(i, infosOfTrans[i].getName());
            comboTransMode.addItem(line);
        }

        // 設定値を復元(MIDIOUT)
        {
            String saveInfoOfRecv = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT);
            if (saveInfoOfRecv.isEmpty() == false) {
                comboRecvMode.setSelectedIndex(0);
                if (saveInfoOfRecv.equals(JMSYNTH_ITEM_NAME) == true) {
                    comboRecvMode.setSelectedIndex(1);
                }
                else {
                    for (int i = 0; i < infosOfRecv.length; i++) {
                        if (saveInfoOfRecv.equals(infosOfRecv[i].getName()) == true) {
                            comboRecvMode.setSelectedIndex(i + NUMBER_OF_CUSTOM_SYNTH);
                            break;
                        }
                    }
                }
            }
            else {
                comboRecvMode.setSelectedIndex(0);
            }
        }

        // 設定値を復元(MIDIIN)
        {
            String saveInfoOfTrans = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN);
            if (saveInfoOfTrans.isEmpty() == false) {
                comboTransMode.setSelectedIndex(0);
                for (int i = 0; i < infosOfTrans.length; i++) {
                    if (saveInfoOfTrans.equals(infosOfTrans[i].getName()) == true) {
                        comboTransMode.setSelectedIndex(i + 1);
                        break;
                    }
                }
            }
            else {
                comboTransMode.setSelectedIndex(0);
            }
        }

        setVisible(true);
    }

    protected void commit() {
        isOkActionClose = true;

        JMPCore.getWindowManager().closeBuiltinSynthFrame();

        String pastMidiOutName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT);
        String pastMidiInName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN);

        try {

            final int COMMIT_BUILT_IN = 0;
            final int COMMIT_MIDI = 1;
            int commitType = COMMIT_MIDI;

            String listName = comboRecvMode.getSelectedItem().toString().trim();
            Receiver outReciever = null;
            int outDevIndex = getOrgDeviceIndex(listName);
            if (outDevIndex != -1) {
                MidiDevice outDev = MidiSystem.getMidiDevice(infosOfRecv[outDevIndex]);
                if (outDev.isOpen() == false) {
                    outDev.open();
                }
                outReciever = outDev.getReceiver();
                JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, outDev.getDeviceInfo().getName());
            }
            else {
                /* 独自のシンセを選択 */
                if (listName.equals(JMSYNTH_ITEM_NAME) == true) {
                    // リソース準備は後で行う
                    commitType = COMMIT_BUILT_IN;

                    JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, JMSYNTH_ITEM_NAME);
                }
                else {
                    // デフォルト
                    int defIndex = -1;
                    for (int i = 0; i < infosOfRecv.length; i++) {
                        if (infosOfRecv[i].getName().contains("Gervill") == true) {
                            defIndex = i;
                            break;
                        }
                    }

                    /* デフォルト使用 */
                    if (defIndex != -1) {
                        // "Gervill"を優先的に使用
                        MidiDevice outDev = MidiSystem.getMidiDevice(infosOfRecv[defIndex]);
                        if (outDev.isOpen() == false) {
                            outDev.open();
                        }
                        outReciever = outDev.getReceiver();
                    }
                    else {
                        // SoundAPIの自動選択に従う
                        try {
                            outReciever = MidiSystem.getReceiver();
                        }
                        catch (Exception e3) {
                            // ない場合は内蔵シンセを採用する
                            commitType = COMMIT_BUILT_IN;
                        }
                    }
                    JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, "");
                }
            }

            // 内蔵シンセのリソース準備
            if (commitType == COMMIT_BUILT_IN || outReciever == null) {
                MidiInterface miface = JMSynthEngine.getMidiInterface();
                outReciever = miface;

                // Window登録
                BuiltinSynthSetupDialog wvf = new BuiltinSynthSetupDialog(miface);
                Color[] ct = new Color[16];
                for (int i = 0; i < 16; i++) {
                    String key = String.format(SystemManager.COMMON_REGKEY_CH_COLOR_FORMAT, i + 1);
                    ct[i] = JMPCore.getSystemManager().getUtilityToolkit().convertCodeToHtmlColor(JMPCore.getSystemManager().getCommonRegisterValue(key));
                }
                wvf.setWaveColorTable(ct);
                JMPCore.getWindowManager().setBuiltinSynthFrame(wvf);
            }

            if ((startupFlag == false) || (pastMidiOutName.equals(JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT)) == false)) {
                commitListener.commitMidiOut(outReciever);
            }

            listName = comboTransMode.getSelectedItem().toString().trim();
            // String devName = getOrgDeviceName(listName);
            Transmitter inTransmitter = null;
            int inDevIndex = getOrgDeviceIndex(listName);
            if (listName.equals("") == false && inDevIndex != -1) {
                MidiDevice inDev = MidiSystem.getMidiDevice(infosOfTrans[inDevIndex]);
                if (inDev.isOpen() == false) {
                    inDev.open();
                }
                inTransmitter = inDev.getTransmitter();
                JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIIN, inDev.getDeviceInfo().getName());

                if ((startupFlag == false) || (pastMidiInName.equals(JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN)) == false)) {
                    commitListener.commitMidiIn(inTransmitter);
                }
            }
            else {
                JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIIN, "");
            }

        }
        catch (MidiUnavailableException e1) {
            // ErrorFunc.logger(e1, true);
        }
        catch (Exception e2) {
            System.out.println("Not Open Midi OUT Device.");
        }
        finally {
            if (startupFlag == false) {
                startupFlag = true;
            }
            close();
        }
    }

    /**
     * 閉じる
     */
    public void close() {
        this.setVisible(false);
    }

    private String createItemName(int no, String name) {
        String ret = String.format("%s%s %s", no, NameNoSepareter, name);
        return ret;
    }

    protected int getOrgDeviceIndex(String name) {
        String devIndex = name;
        String[] sName = devIndex.split(NameNoSepareter);
        devIndex = sName[0].trim();
        return Utility.tryParseInt(devIndex, -1);
    }

    protected String getOrgDeviceName(String name) {
        String devName = name;
        String[] sName = devName.split(NameNoSepareter);
        devName = sName[sName.length - 1].trim();
        return devName;
    }

    public boolean isOkActionClose() {
        return isOkActionClose;
    }

    public void setCommitListener(SelectSynthsizerDialogListener commitListener) {
        this.commitListener = commitListener;
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();
        setTitle(lm.getLanguageStr(LangID.MIDI_device_settings));
        chckbxStartupShowDialog.setText(lm.getLanguageStr(LangID.Whether_to_display_every_time_at_startup));
    }
}
