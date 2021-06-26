package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SystemManager;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.JMPDialog;
import jmp.gui.ui.MultiKeyActionTextField;
import jmp.lang.DefineLanguage.LangID;
import process.IProcessingCallback;

public class YoutubeConvertDialog extends JMPDialog {

    public static final String DEFAULT_DST_EXT = "mp3";

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldExePath;
    private JTextField textFieldURL;
    private JLabel lblStatus;
    private JButton convertButton;
    private JLabel lblPath;
    private JLabel lblURL;
    private JButton btnExpButton;
    private JTextField dstExtTextField;
    private JButton btnOpenExe;
    private JPanel buttonPane;
    private JCheckBox chckbxInstalled;

    /**
     * Create the dialog.
     */
    public YoutubeConvertDialog() {
        super();
        setTitle("youtube-dl");
        setBounds(100, 100, 450, 235);
        setResizable(false);

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        textFieldExePath = new MultiKeyActionTextField();
        textFieldExePath.setBounds(12, 48, 373, 19);
        contentPanel.add(textFieldExePath);
        textFieldExePath.setColumns(10);
        textFieldExePath.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                setExePath(file.getPath());
            }
        }));

        lblPath = new JLabel("youtube-dl path(.exe)");
        lblPath.setForeground(Color.WHITE);
        lblPath.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblPath.setBounds(12, 25, 373, 19);
        contentPanel.add(lblPath);

        lblURL = new JLabel("URL");
        lblURL.setForeground(Color.WHITE);
        lblURL.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblURL.setBounds(12, 83, 373, 19);
        contentPanel.add(lblURL);

        textFieldURL = new MultiKeyActionTextField();
        textFieldURL.setColumns(10);
        textFieldURL.setBounds(12, 106, 350, 19);
        contentPanel.add(textFieldURL);
        textFieldURL.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (Utility.checkExtension(file, "url") == true) {
                    /* .urlファイルは開いてURL記述部分を抜粋する */
                    String str = "";
                    try {
                        List<String> content = Utility.getTextFileContents(file.getPath());
                        for (String c : content) {
                            if (c.startsWith("URL=") == true) {
                                str = c.substring(4);
                                break;
                            }
                        }
                    }
                    catch (IOException e) {
                    }
                    setInputPath(str);
                }
                else {
                    setInputPath(file.getPath());
                }
            }
        }));

        lblStatus = new JLabel("");
        lblStatus.setBackground(new Color(0, 0, 0));
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setBounds(12, 130, 373, 19);
        contentPanel.add(lblStatus);

        btnOpenExe = new JButton("...");
        btnOpenExe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("EXE Files", "exe"));

                File dir = null;
                String exePath = textFieldExePath.getText();
                if (Utility.isExsistFile(exePath) == true) {
                    File ef = new File(exePath);
                    dir = ef.getParentFile();
                }
                else {
                    dir = new File(Platform.getCurrentPath());
                }
                if (dir != null) {
                    if (dir.isDirectory() == false) {
                        dir = dir.getParentFile();
                    }
                }

                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            setExePath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        btnOpenExe.setBounds(385, 47, 47, 21);
        contentPanel.add(btnOpenExe);

        dstExtTextField = new JTextField();
        dstExtTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        dstExtTextField.setText("wav");
        dstExtTextField.setBounds(362, 83, 70, 19);
        contentPanel.add(dstExtTextField);
        dstExtTextField.setColumns(10);

        JButton buttonPaste = new JButton("Paste");
        buttonPaste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textFieldURL.setText(Utility.getClipboardString());
            }
        });
        buttonPaste.setBounds(362, 105, 70, 21);
        contentPanel.add(buttonPaste);

        chckbxInstalled = new JCheckBox("installed");
        chckbxInstalled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = chckbxInstalled.isSelected();
                JMPCore.getDataManager().setYoutubeDlInstalled(isSelected);
                updateGuiState();
            }
        });
        chckbxInstalled.setBounds(329, 24, 103, 21);
        contentPanel.add(chckbxInstalled);
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                convertButton = new JButton("Convert");
                convertButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        executeConvert();
                    }
                });

                btnExpButton = new JButton("Exploler");
                btnExpButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openOutputFolder();
                    }
                });
                buttonPane.add(btnExpButton);
                convertButton.setActionCommand("OK");
                buttonPane.add(convertButton);
                getRootPane().setDefaultButton(convertButton);
            }
        }

        dstExtTextField.setText(DEFAULT_DST_EXT);

        updateGuiState();
        updateBackColor();
    }

    private void updateGuiState() {
        textFieldExePath.setEnabled(!JMPCore.getDataManager().isYoutubeDlInstalled());
        btnOpenExe.setEnabled(!JMPCore.getDataManager().isYoutubeDlInstalled());
        if (JMPCore.getDataManager().isYoutubeDlInstalled() == true) {
            textFieldExePath.setText("youtube-dl");
        }
        else {
            textFieldExePath.setText(JMPCore.getDataManager().getYoutubeDlPath());
        }
    }

    private FileNameExtensionFilter createFileFilter(String exName, String... ex) {
        String exs = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                exs += ", ";
            }
            exs += String.format("*.%s", ex[i]);
        }

        String description = String.format("%s (%s)", exName, exs);
        return new FileNameExtensionFilter(description, ex);
    }

    public void setExePath(String path) {
        JMPCore.getDataManager().setYoutubeDlPath(path);

        textFieldExePath.setText(JMPCore.getDataManager().getYoutubeDlPath());
    }

    public void setInputPath(String path) {
        textFieldURL.setText(path);
    }

    public void openOutputFolder() {
//        SystemManager system = JMPCore.getSystemManager();
//        File outdir = new File(system.getYoutubeDlWrapperPath());
//        outdir = outdir.getParentFile();
//        if (outdir != null) {
//            if (outdir.exists() == true) {
//                try {
//                    Utility.openExproler(outdir);
//                }
//                catch (IOException e2) {
//                }
//            }
//        }
//        else {
//            try {
//                Utility.openExproler(Platform.getCurrentPath());
//            }
//            catch (IOException e2) {
//            }
//        }

        /* 常にカレントを開く */
        try {
            Utility.openExproler(Platform.getCurrentPath());
        }
        catch (IOException e2) {
        }
    }

    private void executeConvert() {
        DataManager dm = JMPCore.getDataManager();
        SystemManager system = JMPCore.getSystemManager();
        LanguageManager lm = JMPCore.getLanguageManager();

        system.setYoutubeDlWrapperPath(textFieldExePath.getText());
        if (system.isValidYoutubeDlWrapper() == false) {
            lblStatus.setForeground(Color.RED);
            String name = String.format("\"%s\"", ".exe");
            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
            return;
        }
        String url = textFieldURL.getText();
        if (url.isEmpty() == true) {
            lblStatus.setForeground(Color.RED);
            String name = String.format("\"%s\"", "URL");
            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
            return;
        }

        File outdir = new File(JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_FFMPEG_OUTPUT));
        String dstExt = dstExtTextField.getText();
        if (dstExt.isEmpty() == true) {
            dstExt = DEFAULT_DST_EXT;
        }

        IProcessingCallback callback = new IProcessingCallback() {

            @Override
            public void end(int result) {
                convertButton.setEnabled(true);

                if (result != 0) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText(lm.getLanguageStr(LangID.Conversion_failed));
                    repaint();
                    return;
                }

                lblStatus.setForeground(Color.GREEN);

                lblStatus.setText(lm.getLanguageStr(LangID.Conversion_completed));
                repaint();

                openOutputFolder();
            }

            @Override
            public void begin() {
                lblStatus.setForeground(Color.LIGHT_GRAY);
                lblStatus.setText(lm.getLanguageStr(LangID.Now_converting));
                repaint();

                convertButton.setEnabled(false);
            }
        };

        system.getYoutubeDlWrapper().setCallback(callback);

        try {
            system.getYoutubeDlWrapper().setOutput(outdir.getPath());
            system.getYoutubeDlWrapper().convert(url, dstExt);
            dm.setYoutubeDlPath(textFieldExePath.getText());
        }
        catch (IOException e1) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Faild.");
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            updateConfig("");
        }
        super.setVisible(b);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();

        convertButton.setText(lm.getLanguageStr(LangID.Convert));
        btnExpButton.setText(lm.getLanguageStr(LangID.Open_with_Explorer));
    }

    @Override
    public void updateConfig(String key) {
        textFieldExePath.setText(JMPCore.getDataManager().getYoutubeDlPath());
        chckbxInstalled.setSelected(JMPCore.getDataManager().isYoutubeDlInstalled());
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            chckbxInstalled.setEnabled(false);
        }
        else {
            chckbxInstalled.setEnabled(true);
        }

        super.updateConfig(key);

        updateGuiState();
    }

    @Override
    public void updateBackColor() {
        super.updateBackColor();
        contentPanel.setBackground(getJmpBackColor());
        buttonPane.setBackground(getJmpBackColor());
        chckbxInstalled.setBackground(getJmpBackColor());
        chckbxInstalled.setForeground(Utility.getForegroundColor(getJmpBackColor()));
    }
}