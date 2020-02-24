package jmp.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import jmp.core.JMPCore;
import jmp.gui.ui.JMPDialog;

public class SelectSoundFontDIalog extends JMPDialog {

    private boolean isOkActionClose = false;
    private final JPanel contentPanel = new JPanel();
    private JTextField sfPathField;

    /**
     * Create the dialog.
     */
    public SelectSoundFontDIalog() {
        super();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 450, 200);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Synthesizer synth = MidiSystem.getSynthesizer();
                        synth.open();
                        // synth.unloadAllInstruments(synth.getDefaultSoundbank());

                        String path = sfPathField.getText();
                        if (path.equals("") == true) {
                            return;
                        }
                        File sfFile = new File(path);
                        synth.unloadAllInstruments(synth.getDefaultSoundbank());
                        synth.loadAllInstruments(MidiSystem.getSoundbank(sfFile));

                        Sequencer sequencer = JMPCore.getSoundManager().getSequencer();
                        sequencer.getTransmitter().setReceiver(synth.getReceiver());
                    }
                    catch (MidiUnavailableException e1) {
                        e1.printStackTrace();
                    }
                    catch (InvalidMidiDataException e1) {
                        e1.printStackTrace();
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    finally {
                        isOkActionClose = true;
                        close();
                    }
                }
            });
            okButton.setBounds(373, 134, 49, 21);
            contentPanel.add(okButton);
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);
        }

        sfPathField = new JTextField();
        sfPathField.setBounds(12, 48, 379, 19);
        contentPanel.add(sfPathField);
        sfPathField.setColumns(10);

        JButton sfFileButton = new JButton("...");
        sfFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("SoundFont Files(*.sf2)", "sf2");
                filechooser.setFileFilter(filter);

                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            // ファイルロード
                            sfPathField.setText(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        sfFileButton.setBounds(391, 47, 31, 21);
        contentPanel.add(sfFileButton);

        JLabel lblSoundfont = new JLabel("SoundFont");
        lblSoundfont.setBounds(12, 31, 109, 13);
        contentPanel.add(lblSoundfont);
    }

    /**
     * 開く
     */
    public void start() {

        isOkActionClose = false;
        sfPathField.setText("");
        this.setVisible(true);
    }

    /**
     * 閉じる
     */
    public void close() {
        this.setVisible(false);
    }

    public boolean isOkActionClose() {
        return isOkActionClose;
    }
}
