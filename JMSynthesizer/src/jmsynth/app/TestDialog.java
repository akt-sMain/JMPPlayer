package jmsynth.app;

import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import jmsynth.JMSoftSynthesizer;
import jmsynth.app.component.WaveViewerPanel;
import jmsynth.midi.MidiInterface;
import jmsynth.oscillator.IOscillator.WaveType;

public class TestDialog extends JFrame {

    private JPanel contentPane;

    private static JMSoftSynthesizer SoftSynthesizer;
    private static Sequencer SoftSequencer;
    private JTextField textFieldMidiFile;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Startup();

                    TestDialog frame = new TestDialog();
                    frame.setupGui();
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestDialog() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SoftSynthesizer.closeDevice();
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 631, 618);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnSaw = new JButton("Saw");
        btnSaw.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //SoftSynthesizer.setOscillator(0, new SawWaveOscillator());
                SoftSynthesizer.setOscillator(0, WaveType.SAW);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        btnSaw.setBounds(12, 10, 72, 21);
        contentPane.add(btnSaw);

        JButton btnSquare = new JButton("Square");
        btnSquare.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SoftSynthesizer.setOscillator(0, WaveType.SQUARE);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        btnSquare.setBounds(96, 10, 72, 21);
        contentPane.add(btnSquare);

        textFieldMidiFile = new JTextField();
        textFieldMidiFile.setBounds(12, 41, 487, 19);
        contentPane.add(textFieldMidiFile);
        textFieldMidiFile.setColumns(10);

        textFieldMidiFile.setText("DuckTales_moon_org4.mid");

        JButton btnPlay = new JButton("play");
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = new File(textFieldMidiFile.getText());
                if (SoftSequencer.isRunning() == false) {
                    if (f.exists() == true) {
                        try {
                            Sequence seq = MidiSystem.getSequence(f);
                            SoftSequencer.setSequence(seq);
                            SoftSequencer.start();
                        }
                        catch (InvalidMidiDataException e1) {
                            // TODO 自動生成された catch ブロック
                            e1.printStackTrace();
                        }
                        catch (IOException e1) {
                            // TODO 自動生成された catch ブロック
                            e1.printStackTrace();
                        }
                    }
                }
                else {
                    SoftSequencer.stop();
                }
            }
        });
        btnPlay.setBounds(501, 40, 91, 21);
        contentPane.add(btnPlay);

        WaveViewerPanel waveViewerPanel1 = new WaveViewerPanel(SoftSynthesizer, 0);
        waveViewerPanel1.setBounds(12, 70, 136, 112);
        contentPane.add(waveViewerPanel1);

        WaveViewerPanel waveViewerPanel2 = new WaveViewerPanel(SoftSynthesizer, 1);
        waveViewerPanel2.setBounds(160, 70, 136, 112);
        contentPane.add(waveViewerPanel2);

        WaveViewerPanel waveViewerPanel3 = new WaveViewerPanel(SoftSynthesizer, 2);
        waveViewerPanel3.setBounds(308, 70, 136, 112);
        contentPane.add(waveViewerPanel3);

        WaveViewerPanel waveViewerPanel4 = new WaveViewerPanel(SoftSynthesizer, 3);
        waveViewerPanel4.setBounds(456, 70, 136, 112);
        contentPane.add(waveViewerPanel4);

        WaveViewerPanel waveViewerPanel5 = new WaveViewerPanel(SoftSynthesizer, 4);
        waveViewerPanel5.setBounds(12, 192, 136, 112);
        contentPane.add(waveViewerPanel5);

        WaveViewerPanel waveViewerPanel6 = new WaveViewerPanel(SoftSynthesizer, 5);
        waveViewerPanel6.setBounds(160, 192, 136, 112);
        contentPane.add(waveViewerPanel6);

        WaveViewerPanel waveViewerPanel7 = new WaveViewerPanel(SoftSynthesizer, 6);
        waveViewerPanel7.setBounds(308, 192, 136, 112);
        contentPane.add(waveViewerPanel7);

        WaveViewerPanel waveViewerPanel8 = new WaveViewerPanel(SoftSynthesizer, 7);
        waveViewerPanel8.setBounds(456, 192, 136, 112);
        contentPane.add(waveViewerPanel8);

        WaveViewerPanel waveViewerPanel9 = new WaveViewerPanel(SoftSynthesizer, 8);
        waveViewerPanel9.setBounds(12, 314, 136, 112);
        contentPane.add(waveViewerPanel9);

        WaveViewerPanel waveViewerPanel10 = new WaveViewerPanel(SoftSynthesizer, 9);
        waveViewerPanel10.setBounds(160, 314, 136, 112);
        contentPane.add(waveViewerPanel10);

        WaveViewerPanel waveViewerPanel11 = new WaveViewerPanel(SoftSynthesizer, 10);
        waveViewerPanel11.setBounds(308, 314, 136, 112);
        contentPane.add(waveViewerPanel11);

        WaveViewerPanel waveViewerPanel12 = new WaveViewerPanel(SoftSynthesizer, 11);
        waveViewerPanel12.setBounds(456, 314, 136, 112);
        contentPane.add(waveViewerPanel12);

        WaveViewerPanel waveViewerPanel13 = new WaveViewerPanel(SoftSynthesizer, 12);
        waveViewerPanel13.setBounds(12, 436, 136, 112);
        contentPane.add(waveViewerPanel13);

        WaveViewerPanel waveViewerPanel14 = new WaveViewerPanel(SoftSynthesizer, 13);
        waveViewerPanel14.setBounds(160, 436, 136, 112);
        contentPane.add(waveViewerPanel14);

        WaveViewerPanel waveViewerPanel15 = new WaveViewerPanel(SoftSynthesizer, 14);
        waveViewerPanel15.setBounds(308, 436, 136, 112);
        contentPane.add(waveViewerPanel15);

        WaveViewerPanel waveViewerPanel16 = new WaveViewerPanel(SoftSynthesizer, 15);
        waveViewerPanel16.setBounds(456, 436, 136, 112);
        contentPane.add(waveViewerPanel16);

        JButton btnSin = new JButton("Sin");
        btnSin.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SoftSynthesizer.setOscillator(0, WaveType.SINE);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        btnSin.setBounds(180, 10, 72, 21);
        contentPane.add(btnSin);

        JButton btnTri = new JButton("Tri");
        btnTri.setBounds(264, 10, 72, 21);
        btnTri.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SoftSynthesizer.setOscillator(0, WaveType.TRIANGLE);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        contentPane.add(btnTri);

        JButton btnNois = new JButton("Nois");
        btnNois.setBounds(432, 10, 72, 21);
        btnNois.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SoftSynthesizer.setOscillator(0, WaveType.NOISE);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        contentPane.add(btnNois);

        JButton btnPulse = new JButton("Pulse");
        btnPulse.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SoftSynthesizer.setOscillator(0, WaveType.PULSE);
                sendNote(true, 0);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                sendNote(false, 0);
            }
        });
        btnPulse.setBounds(348, 10, 72, 21);
        contentPane.add(btnPulse);
    }

    private void sendNote(boolean on, int ch) {
        sendNote(on, ch, 60);//テスト用の60番
    }
    private void sendNote(boolean on, int ch, int noteNo) {
        if (on) {
            SoftSynthesizer.noteOn(ch, noteNo, 100);
        }
        else {
            SoftSynthesizer.noteOff(ch, noteNo);
        }
//        ShortMessage ms;
//        try {
//            ms = new ShortMessage(on ? ShortMessage.NOTE_ON:ShortMessage.NOTE_OFF, ch, noteNo, 100);
//            SoftMidiInterface.send(ms, 0);
//        }
//        catch (InvalidMidiDataException e) {
//            // TODO 自動生成された catch ブロック
//            e.printStackTrace();
//        }
    }

    public class DropFileCallbackHandler extends TransferHandler {
        private JTextComponent cmp;
        public DropFileCallbackHandler(JTextComponent cmp) {
            super();
            this.cmp = cmp;
        }

        /**
         * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
         */
        @Override
        public boolean canImport(TransferSupport support) {
            if (support.isDrop() == false) {
                // ドロップ操作でない場合は受け取らない
                return false;
            }

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
                // ファイルでない場合は受け取らない
                return false;
            }

            return true;
        }

        /**
         * ドロップされたアイテムを受け取る
         */
        @Override
        public boolean importData(TransferSupport support) {
            // ドロップアイテム受理の確認
            if (canImport(support) == false) {
                return false;
            }

            // ドロップ処理
            Transferable t = support.getTransferable();
            try {
                // ドロップアイテム取得
                Object item = t.getTransferData(DataFlavor.javaFileListFlavor);

                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) item;

                // 一番先頭のファイルを取得
                if ((files != null) && (files.size() > 0)) {
                    String path = files.get(0).getPath();
                    File file = new File(path);
                    cmp.setText(file.getPath());
                }
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
        }
    }

    public void setupGui() {
        textFieldMidiFile.setTransferHandler(new DropFileCallbackHandler(textFieldMidiFile));
        this.setTransferHandler(new DropFileCallbackHandler(textFieldMidiFile));
    }

    private static void Startup() {
        SoftSynthesizer = new JMSoftSynthesizer();
        SoftSynthesizer.openDevice();

        if (SoftSequencer == null) {
            try {
                SoftSequencer = MidiSystem.getSequencer(false);
                SoftSequencer.open();

                SoftSequencer.getTransmitter().setReceiver(new MidiInterface(SoftSynthesizer));
//                for (int i=0; i<16; i++) {
//                    SoftSequencer.getTransmitter().setReceiver(new MidiInterface(SoftSynthesizer.getChannel(i)));
//                }
            }
            catch (MidiUnavailableException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }
    }
}
