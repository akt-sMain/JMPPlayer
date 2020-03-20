package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import function.Utility;
import jlib.midi.CommandDefine;
import jlib.midi.ControlChangeDefine;
import jlib.midi.IMidiEventListener;
import jmp.core.JMPCore;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;

public class MidiChannelMonitor extends JMPDialog implements IMidiEventListener {

    private DefaultTableModel model = null;
    private final JPanel contentPanel = new JPanel();
    private JScrollPane scrollPane;
    private JComboBox<String> comboBox;
    private JTable table;

    public MidiChannelMonitor() {
        setTitle("MIDIメッセージモニタ");
        setBounds(100, 100, 631, 465);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                table = new JTable();
                model = new DefaultTableModel(new Object[][] {},
                        new String[] { "Time", "Byte", "Channel", "Status", "Data1", "Data2" });
                table.setModel(model);

                table.getColumn("Time").setPreferredWidth(30);
                table.getColumn("Byte").setPreferredWidth(30);
                table.getColumn("Channel").setPreferredWidth(5);
                //table.getColumn("Data1").setPreferredWidth(5);
                //table.getColumn("Data2").setPreferredWidth(5);
                scrollPane.setViewportView(table);
            }
        }
        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_MIDI_MONITOR, this);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new GridLayout(0, 6, 5, 0));
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel);
                panel.setLayout(new BorderLayout(0, 0));
                {
                    JButton btnClear = new JButton("Clear");
                    btnClear.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            model.setRowCount(0);
                        }
                    });
                    panel.add(btnClear);
                }
            }
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel);
            }
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel);
            }
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel);
            }
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel);
            }
            {
                comboBox = new JComboBox<String>();
                buttonPane.add(comboBox);
                comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "All", "1", "2", "3", "4", "5", "6",
                        "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
            }
        }
    }

    public void addMidiMessage(MidiMessage mes) {
        try {
            int ch = mes.getStatus() & 0x0F;
            String combo = comboBox.getSelectedItem().toString();
            if (combo.equalsIgnoreCase("All") == false) {
                if ((Utility.tryParseInt(combo, -1) - 1) != ch) {
                    return;
                }
            }

            String[] rowData = getMessageInfoStr(mes);

            try {
                model.insertRow(0, rowData);
                table.setRowSelectionInterval(0, 0);
//
//                if (model.getRowCount() > 1000) {
//                    model.setRowCount(1000);
//                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private String[] getMessageInfoStr(MidiMessage mes) {

        SoundManager sm = JMPCore.getSoundManager();

        byte[] data = mes.getMessage();
        String byteStr = getByteMessageStr(mes);
        String time = "" + sm.getSequencer().getTickPosition() + "[" + sm.getPositionTimeString() + "]";
        int ch = data.length >= 1 ? (data[0] & 0x0f) : 0;
        int status = data.length >= 1 ? Byte.toUnsignedInt(data[0]) : 0;
        int command = data.length >= 1 ? (data[0] & 0xf0) : 0;
        int data1 = data.length >= 2 ? (data[1] & 0xff) : 0;
        int data2 = data.length >= 3 ? (data[2] & 0xff) : 0;

        String[] rowData = new String[model.getColumnCount()];

        String sCommand = "--";
        boolean notChannelMsg = false;
        boolean isControlChange = false;

        rowData[0] = time;
        rowData[1] = byteStr;
        if (command < 0xf0) {
            sCommand = CommandDefine.getCommandMessage(command);
            if (command == CommandDefine.CONTROL_CHANGE) {
                isControlChange = true;
            }
            notChannelMsg = false;
        }
        else {
            sCommand = CommandDefine.getCommandMessage(status);
            notChannelMsg = true;
        }

        if (notChannelMsg == true) {
            rowData[2] = "--";
            rowData[3] = sCommand;
            rowData[4] = "--";
            rowData[5] = "--";
        }
        else {
            String sData1 = "--";
            String sData2 = "--";
            if (isControlChange == true) {
                sData1 = ControlChangeDefine.getControlChangeMessage(data1);
                sData2 = String.valueOf(data2);
            }
            else {
                sData1 = String.valueOf(data1);
                sData2 = String.valueOf(data2);
            }

            // チャンネルメッセージ
            rowData[2] = String.valueOf(ch + 1);
            rowData[3] = sCommand;
            rowData[4] = sData1;
            rowData[5] = sData2;
        }
        return rowData;
    }

    private String getByteMessageStr(MidiMessage mes) {
        String byteData = "";
        // for (int i = mes.getLength()-1; i >= 0; i--) {
        for (int i = 0; i < mes.getLength(); i++) {
            int iData = Byte.toUnsignedInt(mes.getMessage()[i]);
            String hex = convertIntegerToHexString(iData);
            byteData += hex;
        }
        return byteData;
    }

    private String convertIntegerToHexString(int iData) {
        String hex = Integer.toHexString(iData);
        hex = (hex.length() < 2) ? "0" + hex : hex;
        return hex;
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        if (isVisible() == true) {
            addMidiMessage(message);
        }
    }
}