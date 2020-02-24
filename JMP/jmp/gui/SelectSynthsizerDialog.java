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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import function.Utility;
import jmp.ConfigDatabase;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.gui.ui.JMPDialog;
import jmp.player.MidiPlayer;

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
    private JLabel lblSettingUp;
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
    private JLabel labelDevelop;

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
        setBounds(100, 100, 460, 271);
        getContentPane().setLayout(new BorderLayout());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

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

            lblSettingUp = new JLabel("Setting up...");
            lblSettingUp.setForeground(Color.PINK);
            lblSettingUp.setBounds(12, 215, 138, 13);
            lblSettingUp.setVisible(false);
            contentPanel.add(lblSettingUp);

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
            lblDescriptionOfRecv.setBounds(12, 108, 410, 13);
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
            lblDescriptionOfTrans.setBounds(12, 108, 410, 13);
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
            comboRecvMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        String listName = comboRecvMode.getSelectedItem().toString().trim();
                        // String devName = getOrgDeviceName(listName);
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "Description : ";
                        String port = "";

                        MidiDevice dev = MidiSystem.getMidiDevice(infosOfRecv[devIndex]);
                        vendor += dev.getDeviceInfo().getVendor();
                        version += dev.getDeviceInfo().getVersion();
                        description += dev.getDeviceInfo().getDescription();

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
                        // String devName = getOrgDeviceName(listName);
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "Description : ";
                        String port = "";

                        MidiDevice dev = MidiSystem.getMidiDevice(infosOfTrans[devIndex]);
                        vendor += dev.getDeviceInfo().getVendor();
                        version += dev.getDeviceInfo().getVersion();
                        description += dev.getDeviceInfo().getDescription();

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

    public void start() {
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
            String saveInfoOfRecv = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIOUT);
            if (saveInfoOfRecv.isEmpty() == false) {
                comboRecvMode.setSelectedIndex(0);
                for (int i = 0; i < infosOfRecv.length; i++) {
                    if (saveInfoOfRecv.equals(infosOfRecv[i].getName()) == true) {
                        comboRecvMode.setSelectedIndex(i + 1);
                        break;
                    }
                }
            }
            else {
                comboRecvMode.setSelectedIndex(0);
            }
        }

        // 設定値を復元(MIDIIN)
        {
            String saveInfoOfTrans = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIIN);
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

        String pastMidiOutName = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIOUT);
        String pastMidiInName = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIIN);

        try {

            String listName = comboRecvMode.getSelectedItem().toString().trim();
            Receiver inReciever = null;
            int inDevIndex = getOrgDeviceIndex(listName);
            if (inDevIndex != -1) {
                MidiDevice inDev = MidiSystem.getMidiDevice(infosOfRecv[inDevIndex]);
                if (inDev.isOpen() == false) {
                    inDev.open();
                }
                inReciever = inDev.getReceiver();
                JMPCore.getDataManager().setConfigParam(ConfigDatabase.CFG_KEY_MIDIOUT,
                        inDev.getDeviceInfo().getName());
            }
            else {
                /* デフォルト使用 */
                inReciever = MidiSystem.getReceiver();
                JMPCore.getDataManager().setConfigParam(ConfigDatabase.CFG_KEY_MIDIOUT, "");
            }
            if ((startupFlag == false) || (pastMidiOutName
                    .equals(JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIOUT)) == false)) {
                commitListener.commitMidiOut(inReciever);
            }

            listName = comboTransMode.getSelectedItem().toString().trim();
            // String devName = getOrgDeviceName(listName);
            int outDevIndex = getOrgDeviceIndex(listName);
            if (listName.equals("") == false && outDevIndex != -1) {
                MidiDevice outDev = MidiSystem.getMidiDevice(infosOfTrans[outDevIndex]);
                if (outDev.isOpen() == false) {
                    outDev.open();
                }
                JMPCore.getDataManager().setConfigParam(ConfigDatabase.CFG_KEY_MIDIIN,
                        outDev.getDeviceInfo().getName());

                if ((startupFlag == false) || (pastMidiInName
                        .equals(JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDIIN)) == false)) {
                    commitListener.commitMidiIn(outDev.getTransmitter());
                }
            }
            else {
                JMPCore.getDataManager().setConfigParam(ConfigDatabase.CFG_KEY_MIDIIN, "");
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
}
