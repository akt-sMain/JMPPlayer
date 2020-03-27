package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import function.Platform;
import function.Utility;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class LicenseReaderDialog extends JMPDialog {

    private final JPanel contentPanel = new JPanel();
    private ButtonGroup btnGroupAcc;
    private JLabel labelAccept = new JLabel("上記の条件に：");
    private JRadioButton rdbtnReject = new JRadioButton("同意しない");
    private JRadioButton rdbtnAccept = new JRadioButton("同意する");
    private JTextArea textAreaLisence = new JTextArea();
    private JLabel labelClipMes = new JLabel("クリップボードにコピーしました");
    private JButton buttonCopy = new JButton("原文のコピー");

    /**
     * Create the dialog.
     */
    public LicenseReaderDialog() {
        super();
        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setTitle("License");
        setBounds(100, 100, 725, 550);
        getContentPane().setLayout(new BorderLayout());

        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_LICENSE, this);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        rdbtnReject.setForeground(Color.WHITE);

        rdbtnReject.setSelected(true);
        rdbtnReject.setBounds(570, 442, 113, 21);
        contentPanel.add(rdbtnReject);
        rdbtnAccept.setForeground(Color.WHITE);

        rdbtnAccept.setBounds(453, 442, 113, 21);
        contentPanel.add(rdbtnAccept);
        labelAccept.setForeground(Color.WHITE);

        labelAccept.setBounds(408, 423, 160, 13);
        contentPanel.add(labelAccept);

        buttonCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                labelClipMes.setVisible(true);
                Utility.setClipboard(textAreaLisence.getText());
            }
        });
        buttonCopy.setBounds(12, 424, 106, 21);
        contentPanel.add(buttonCopy);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(null);
        }

        btnGroupAcc = new ButtonGroup();
        btnGroupAcc.add(rdbtnAccept);
        btnGroupAcc.add(rdbtnReject);

        labelClipMes.setForeground(new Color(0, 191, 255));
        labelClipMes.setFont(new Font("MS UI Gothic", Font.BOLD, 12));
        labelClipMes.setBounds(12, 456, 251, 13);
        labelClipMes.setVisible(false);
        contentPanel.add(labelClipMes);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 681, 403);
        contentPanel.add(scrollPane);
        scrollPane.setViewportView(textAreaLisence);
        textAreaLisence.setEditable(false);
        {
            JButton okButton = new JButton("OK");
            okButton.setBounds(598, 478, 95, 21);
            contentPanel.add(okButton);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (rdbtnAccept.isSelected() == true) {
                        /* 使用可能 */
                        JMPFlags.ActivateFlag = true;
                    }
                    else {
                        JMPFlags.ActivateFlag = false;
                    }
                    close();
                }
            });
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);
        }

        contentPanel.setBackground(getJmpBackColor());
        rdbtnReject.setBackground(getJmpBackColor());
        rdbtnAccept.setBackground(getJmpBackColor());
    }

    public void start() {

        BufferedReader reader;
        String line = "";
        String contents = "";

        try {
            String path = Utility.pathCombin(Platform.getCurrentPath(false), "license.txt");
            File file = new File(path);
            FileInputStream fs = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
            reader = new BufferedReader(isr);

            // ファイルを読み込む
            while ((line = reader.readLine()) != null) {
                contents += (line + Platform.getNewLine());
            }
            reader.close();
        }
        catch (Exception e) {
            contents = "";
            contents += "ライセンスが読み取れません。" + Platform.getNewLine();
            contents += "「license.txt」の条件項目を参照してください。";
        }
        finally {
            reader = null;
        }

        if (JMPFlags.ActivateFlag == true) {
            rdbtnAccept.setSelected(true);
            labelAccept.setVisible(false);
            rdbtnReject.setVisible(false);
            rdbtnAccept.setVisible(false);
        }
        else {
            labelAccept.setVisible(true);
            rdbtnReject.setVisible(true);
            rdbtnAccept.setVisible(true);
        }

        textAreaLisence.setText("");
        textAreaLisence.setText(contents);
        labelClipMes.setVisible(false);
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        LanguageManager lm = JMPCore.getLanguageManager();

        setTitle(lm.getLanguageStr(LangID.License));
        labelAccept.setText(lm.getLanguageStr(LangID.Above_conditions));
        rdbtnReject.setText(lm.getLanguageStr(LangID.Reject));
        rdbtnAccept.setText(lm.getLanguageStr(LangID.Accept));
        labelClipMes.setText(lm.getLanguageStr(LangID.Copied_to_clipboard));
        buttonCopy.setText(lm.getLanguageStr(LangID.Original_copy));
    }
}
