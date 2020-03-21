package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.midi.MidiMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import function.Utility;
import jlib.midi.DefineCommand;
import jlib.midi.IMidiEventListener;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;

public class MidiMessageMonitor extends JMPDialog implements IMidiEventListener {

    private final static int MAX_ROW = 20000;
    private final static String COUNTER_FORMAT = " Counter : %d";

    private JLabel lblCounter;
    private DefaultTableModel model = null;
    private final JPanel contentPanel = new JPanel();
    private JScrollPane scrollPane;
    private JComboBox<String> comboBox;
    private JTable table;

    private long midiCounter = 0;
    private JLabel labelByte;
    private JLabel lblData1;
    private JLabel lblCommand;

    public MidiMessageMonitor() {
        setTitle("MIDIメッセージモニタ");
        setBounds(100, 100, 710, 480);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                table = new JTable();
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow >= 0) {
                            updateLabel(selectedRow);
                        }
                    }
                });
                model = new DefaultTableModel(new Object[][] {},
                        new String[] { "Time", "Byte", "Channel", "Command", "Data1", "Data2" });
                table.setModel(model);

                table.getColumn("Time").setPreferredWidth(30);
                table.getColumn("Byte").setPreferredWidth(30);
                table.getColumn("Channel").setPreferredWidth(5);
                scrollPane.setViewportView(table);
            }
        }
        {
            JPanel panel = new JPanel();
            contentPanel.add(panel, BorderLayout.NORTH);
            panel.setLayout(new GridLayout(0, 4, 0, 0));
            {
                lblCounter = new JLabel(" Counter:");
                lblCounter.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblCounter);
            }
            {
                labelByte = new JLabel("");
                labelByte.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(labelByte);
            }
            {
                lblCommand = new JLabel("");
                lblCommand.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblCommand);
            }
            {
                lblData1 = new JLabel("");
                lblData1.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblData1);
            }
            updateCounterLabel();
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
                            clearMidiMessage();
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

    public void clearMidiMessage() {
        model.setRowCount(0);
        clearCounter();
    }

    public void addMidiMessage(MidiMessage mes) {
        try {
            int ch = mes.getStatus() & 0x0f;
            int status = mes.getStatus() & 0xf0;
            switch (status) {
                case DefineCommand.START:
                case DefineCommand.RESET:
                case DefineCommand.STOP:
                    return;
                default:
                    break;
            }

            String combo = comboBox.getSelectedItem().toString();
            if (combo.equalsIgnoreCase("All") == false) {
                if ((Utility.tryParseInt(combo, -1) - 1) != ch) {
                    return;
                }
            }

            String[] rowData = getMessageInfoStr(mes);

            synchronized (model) {
                model.insertRow(0, rowData);
                if (model.getRowCount() > MAX_ROW) {
                    model.setRowCount(MAX_ROW);
                }
            }
            synchronized (table) {
                table.setRowSelectionInterval(0, 0);
            }
            incrementCounter();
            updateLabel(0);
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
        String sub = "";

        String[] rowData = new String[model.getColumnCount()];

        boolean notChannelMsg = false;

        if (command < 0xf0) {
            switch (command) {
                case MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE:
                    sub = "[" + MidiByte.convertByteToControlChangeString(data1) + "]";
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON:
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF:
                    sub = "[" + MidiByte.convertByteToNoteString(data1) + "]";
                    break;
                default:
                    break;
            }
            notChannelMsg = false;
        }
        else {
            notChannelMsg = true;
        }

        rowData[0] = time;
        rowData[1] = byteStr;
        if (notChannelMsg == true) {
            rowData[2] = "--";
            rowData[3] = MidiByte.convertByteToChannelCommandString(status);
            rowData[4] = "--";
            rowData[5] = "--";
        }
        else {
            // チャンネルメッセージ
            rowData[2] = String.valueOf(ch + 1);
            rowData[3] = MidiByte.convertByteToChannelCommandString(status);
            rowData[4] = sub + " " + String.valueOf(data1);
            rowData[5] = String.valueOf(data2);
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

    private void incrementCounter() {
        midiCounter++;
        updateCounterLabel();
    }

    private void clearCounter() {
        midiCounter = 0;
        updateCounterLabel();
    }

    private void updateCounterLabel() {
        synchronized (lblCounter) {
            lblCounter.setText(String.format(COUNTER_FORMAT, midiCounter));
        }
    }

    private void updateLabel(int row) {
        updateByteLabel(row);
        updateCommandLabel(row);
        updateData1Label(row);
    }

    private void updateByteLabel(int row) {
        synchronized (labelByte) {
            String value = model.getValueAt(row, 1).toString();
            labelByte.setText(value);
            labelByte.repaint();
        }
    }

    private void updateCommandLabel(int row) {
        synchronized (lblCommand) {
            String value = model.getValueAt(row, 3).toString();
            lblCommand.setText(value);
            lblCommand.repaint();
        }
    }

    private void updateData1Label(int row) {
        synchronized (lblData1) {
            String value = model.getValueAt(row, 4).toString();
            lblData1.setText(value);
            lblData1.repaint();
        }
    }
}
