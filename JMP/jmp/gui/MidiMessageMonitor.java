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
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import function.Utility;
import jlib.midi.IMidiEventListener;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SoundManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class MidiMessageMonitor extends JMPDialog implements IMidiEventListener {

    private static final int MAX_STACK = 20000;
    private static final String COUNTER_STR_FORMAT = " Counter : %d";
    private static final String STACK_STR_FORMAT = "Stack:%d";
    private static final String TIME_STR_FORMAT = "%d[%s] ";
    private static final String SUB_STR_FORMAT = "[%s] ";

    private static final int INDEX_OF_COLUMN_TIME = 0;
    private static final int INDEX_OF_COLUMN_BYTES = 1;
    private static final int INDEX_OF_COLUMN_CHANNEL = 2;
    private static final int INDEX_OF_COLUMN_COMMAND = 3;
    private static final int INDEX_OF_COLUMN_DATA1 = 4;
    private static final int INDEX_OF_COLUMN_DATA2 = 5;

    private static final String[] COLUMN_NAMES = new String[] { "Time", "Byte", "Channel", "Command", "Data1", "Data2" };

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
    private JLabel lblStack;
    private JButton btnClear;

    public MidiMessageMonitor() {
        super();
        setTitle("MIDIメッセージモニタ");
        setBounds(100, 100, 710, 480);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setJmpIcon();
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
                model = new DefaultTableModel(new Object[][] {}, COLUMN_NAMES);
                table.setModel(model);

                table.getColumn(COLUMN_NAMES[INDEX_OF_COLUMN_TIME]).setPreferredWidth(30);
                table.getColumn(COLUMN_NAMES[INDEX_OF_COLUMN_BYTES]).setPreferredWidth(30);
                table.getColumn(COLUMN_NAMES[INDEX_OF_COLUMN_CHANNEL]).setPreferredWidth(5);
                table.getColumn(COLUMN_NAMES[INDEX_OF_COLUMN_DATA2]).setPreferredWidth(5);
                scrollPane.setViewportView(table);
            }
        }
        {
            JPanel panel = new JPanel();
            panel.setBackground(getJmpBackColor());
            contentPanel.add(panel, BorderLayout.NORTH);
            panel.setLayout(new GridLayout(0, 4, 0, 0));
            {
                lblCounter = new JLabel(" Counter:");
                lblCounter.setForeground(Color.WHITE);
                lblCounter.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblCounter);
            }
            {
                labelByte = new JLabel("");
                labelByte.setForeground(Color.WHITE);
                labelByte.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(labelByte);
            }
            {
                lblCommand = new JLabel("");
                lblCommand.setForeground(Color.WHITE);
                lblCommand.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblCommand);
            }
            {
                lblData1 = new JLabel("");
                lblData1.setForeground(Color.WHITE);
                lblData1.setFont(new Font("Dialog", Font.BOLD, 9));
                panel.add(lblData1);
            }
            updateCounterLabel();
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(getJmpBackColor());
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new GridLayout(0, 6, 5, 0));
            {
                JPanel panel = new JPanel();
                panel.setBackground(getJmpBackColor());
                buttonPane.add(panel);
                panel.setLayout(new BorderLayout(0, 0));
                {
                    btnClear = new JButton("Clear");
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
                panel.setBackground(getJmpBackColor());
                buttonPane.add(panel);
                panel.setLayout(new BorderLayout(0, 0));
                {
                    lblStack = new JLabel("Stack:");
                    lblStack.setForeground(Color.WHITE);
                    lblStack.setHorizontalAlignment(SwingConstants.LEFT);
                    panel.add(lblStack);
                }
            }
            {
                JPanel panel = new JPanel();
                panel.setBackground(getJmpBackColor());
                buttonPane.add(panel);
            }
            {
                JPanel panel = new JPanel();
                panel.setBackground(getJmpBackColor());
                buttonPane.add(panel);
            }
            {
                JPanel panel = new JPanel();
                panel.setBackground(getJmpBackColor());
                buttonPane.add(panel);
            }
            {
                comboBox = new JComboBox<String>();
                buttonPane.add(comboBox);
                comboBox.setModel(new DefaultComboBoxModel<String>(
                        new String[] { "All", "Meta_SysEx", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", }));
            }
        }
    }

    public void clearMidiMessage() {
        model.setRowCount(0);
        clearCounter();
    }

    public void addMidiMessage(MidiMessage mes) {
        try {
            // チャンネルフィルター
            String combo = comboBox.getSelectedItem().toString();
            if (combo.equalsIgnoreCase("Meta_SysEx") == true) {
                int statusByte = mes.getStatus();
                if (MidiByte.isMetaMessage(statusByte) || MidiByte.isSystemMessage(statusByte)) {
                    /* Meta と SysEx を許可 */
                }
                else {
                    return;
                }
            }
            else if (combo.equalsIgnoreCase("All") == true) {
                /* 全て許可 */
            }
            else {
                /* チャンネル指定 */
                int ch = mes.getStatus() & 0x0f;
                if ((Utility.tryParseInt(combo, -1) - 1) != ch) {
                    return;
                }
            }

            String[] rowData = getMessageInfoStr(mes);
            synchronized (model) {
                model.insertRow(0, rowData);
                if (model.getRowCount() > MAX_STACK) {
                    model.setRowCount(MAX_STACK);
                }
            }
            synchronized (table) {
                table.setRowSelectionInterval(0, 0);
            }
            incrementCounter();
            updateLabel(0);
            updateStack();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private String[] getMessageInfoStr(MidiMessage mes) {

        SoundManager sm = JMPCore.getSoundManager();

        String time = String.format(TIME_STR_FORMAT, sm.getSequencer().getTickPosition(), sm.getPositionTimeString());
        String[] rowData = new String[model.getColumnCount()];
        String byteStr = getByteMessageStr(mes);
        String sub = "";
        byte[] data = mes.getMessage();
        int length = mes.getLength();
        int ch = length >= 1 ? (data[0] & 0x0f) : 0;
        int status = length >= 1 ? Byte.toUnsignedInt(data[0]) : 0;
        int command = length >= 1 ? (data[0] & 0xf0) : 0;
        int data1 = length >= 2 ? (data[1] & 0xff) : 0;
        int data2 = length >= 3 ? (data[2] & 0xff) : 0;

        rowData[INDEX_OF_COLUMN_TIME] = time;
        rowData[INDEX_OF_COLUMN_BYTES] = byteStr;
        if (MidiByte.isChannelMessage(status) == true) {
            // チャンネルメッセージ
            switch (command) {
                case MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE:
                    sub = String.format(SUB_STR_FORMAT, MidiByte.convertByteToControlChangeString(data1));
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON:
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF:
                    sub = String.format(SUB_STR_FORMAT, MidiByte.convertByteToNoteString(data1));
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND:
                    sub = String.format(SUB_STR_FORMAT, "" + (MidiByte.mergeLsbMsbValue(data1, data2) - 8192));
                    break;
                default:
                    break;
            }
            rowData[INDEX_OF_COLUMN_CHANNEL] = String.valueOf(ch + 1);
            rowData[INDEX_OF_COLUMN_COMMAND] = String.format("[%s] %d", MidiByte.convertByteToChannelCommandString(status), status);
            rowData[INDEX_OF_COLUMN_DATA1] = sub + String.valueOf(data1);
            rowData[INDEX_OF_COLUMN_DATA2] = String.valueOf(data2);
        }
        else if (MidiByte.isMetaMessage(status) == true) {
            // メタメッセージ
            rowData[INDEX_OF_COLUMN_CHANNEL] = "--";
            rowData[INDEX_OF_COLUMN_COMMAND] = (length >= 2) ? MidiByte.convertByteToMetaString(data[1] & 0xff) : "--";
            rowData[INDEX_OF_COLUMN_DATA1] = sub + "--";
            rowData[INDEX_OF_COLUMN_DATA2] = "--";
        }
        else if (MidiByte.isSystemMessage(status) == true) {
            // システムメッセージ
            rowData[INDEX_OF_COLUMN_CHANNEL] = "--";
            rowData[INDEX_OF_COLUMN_COMMAND] = MidiByte.convertByteToChannelCommandString(status);
            rowData[INDEX_OF_COLUMN_DATA1] = sub + "--";
            rowData[INDEX_OF_COLUMN_DATA2] = "--";
        }
        else {
            // 不明なメッセージ（例外）
            rowData[INDEX_OF_COLUMN_CHANNEL] = "--";
            rowData[INDEX_OF_COLUMN_COMMAND] = "--";
            rowData[INDEX_OF_COLUMN_DATA1] = "--";
            rowData[INDEX_OF_COLUMN_DATA2] = "--";
        }
        return rowData;
    }

    private String getByteMessageStr(MidiMessage mes) {
        String byteData = "";
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
            lblCounter.setText(String.format(COUNTER_STR_FORMAT, midiCounter));
        }
    }

    private void updateLabel(int row) {
        updateByteLabel(row);
        updateCommandLabel(row);
        updateData1Label(row);
    }

    private void updateByteLabel(int row) {
        synchronized (labelByte) {
            String value = model.getValueAt(row, INDEX_OF_COLUMN_BYTES).toString();
            labelByte.setText(value);
            labelByte.repaint();
        }
    }

    private void updateCommandLabel(int row) {
        synchronized (lblCommand) {
            String value = model.getValueAt(row, INDEX_OF_COLUMN_COMMAND).toString();
            lblCommand.setText(value);
            lblCommand.repaint();
        }
    }

    private void updateData1Label(int row) {
        synchronized (lblData1) {
            String value = model.getValueAt(row, INDEX_OF_COLUMN_DATA1).toString();
            lblData1.setText(value);
            lblData1.repaint();
        }
    }

    private void updateStack() {
        synchronized (lblStack) {
            int count = model.getRowCount();
            lblStack.setText(String.format(STACK_STR_FORMAT, count));
            if (count >= MAX_STACK) {
                lblStack.setForeground(Color.RED);
            }
            else {
                lblStack.setForeground(Color.WHITE);
            }
            lblStack.repaint();
        }
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();
        setTitle(lm.getLanguageStr(LangID.MIDI_message_monitor));
        btnClear.setText(lm.getLanguageStr(LangID.Clear));
    }
}
