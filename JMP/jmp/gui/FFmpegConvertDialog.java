package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Utility;
import jmp.ConfigDatabase;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;
import wrapper.IProcessingCallback;

public class FFmpegConvertDialog extends JMPDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldFFmpegExePath;
    private JTextField textFieldOutputDirectory;
    private JTextField textFieldInputFile;
    private JLabel lblStatus;
    private JCheckBox chckbxLeave;
    private JCheckBox chckbxToPlay;
    private JButton convertButton;
    private JLabel lblPath;
    private JLabel lblOutputDirectory;
    private JLabel lblInputFile;
    private JButton btnExpButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            FFmpegConvertDialog dialog = new FFmpegConvertDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public FFmpegConvertDialog() {
        super();
        setTitle("FFmpeg convert");
        setBounds(100, 100, 450, 300);
        setResizable(false);
        setTransferHandler(new DropFileHandler());

        contentPanel.setBackground(getJmpBackColor());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_FFMPEG, this);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        textFieldFFmpegExePath = new JTextField();
        textFieldFFmpegExePath.setBounds(12, 48, 373, 19);
        contentPanel.add(textFieldFFmpegExePath);
        textFieldFFmpegExePath.setColumns(10);

        lblPath = new JLabel("FFmpeg path(.exe)");
        lblPath.setForeground(Color.WHITE);
        lblPath.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblPath.setBounds(12, 25, 373, 19);
        contentPanel.add(lblPath);

        lblOutputDirectory = new JLabel("Output directory");
        lblOutputDirectory.setForeground(Color.WHITE);
        lblOutputDirectory.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblOutputDirectory.setBounds(12, 77, 373, 19);
        contentPanel.add(lblOutputDirectory);

        textFieldOutputDirectory = new JTextField();
        textFieldOutputDirectory.setColumns(10);
        textFieldOutputDirectory.setBounds(12, 100, 373, 19);
        contentPanel.add(textFieldOutputDirectory);

        lblInputFile = new JLabel("Input file");
        lblInputFile.setForeground(Color.WHITE);
        lblInputFile.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblInputFile.setBounds(12, 129, 373, 19);
        contentPanel.add(lblInputFile);

        textFieldInputFile = new JTextField();
        textFieldInputFile.setColumns(10);
        textFieldInputFile.setBounds(12, 152, 373, 19);
        contentPanel.add(textFieldInputFile);

        lblStatus = new JLabel("");
        lblStatus.setBackground(new Color(0, 0, 0));
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setBounds(12, 211, 420, 19);
        contentPanel.add(lblStatus);

        chckbxLeave = new JCheckBox("Leave output file");
        chckbxLeave.setForeground(new Color(255, 255, 255));
        chckbxLeave.setVisible(false);
        chckbxLeave.setFont(new Font("Dialog", Font.PLAIN, 12));
        chckbxLeave.setBackground(getJmpBackColor());
        chckbxLeave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setFFmpegLeaveOutputFile(chckbxLeave.isSelected());
            }
        });
        chckbxLeave.setBounds(151, 177, 176, 21);
        contentPanel.add(chckbxLeave);

        JButton btnOpenExe = new JButton("...");
        btnOpenExe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("EXE Files", "exe"));
                File dir = new File(Platform.getCurrentPath());
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

        JButton buttonOpenOut = new JButton("...");
        buttonOpenOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                String path = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_OUTPUT);
                if (path != null && path.isEmpty() == false) {
                    filechooser.setCurrentDirectory(new File(path));
                }

                int selected = filechooser.showSaveDialog(getParent());
                if (selected == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    if (file.exists() == true && file.canRead() == true) {
                        setOutputDirectory(file.getPath());
                    }
                }
            }
        });
        buttonOpenOut.setBounds(385, 99, 47, 21);
        contentPanel.add(buttonOpenOut);

        JButton buttonOpenInput = new JButton("...");
        buttonOpenInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            setInputPath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        buttonOpenInput.setBounds(385, 151, 47, 21);
        contentPanel.add(buttonOpenInput);

        chckbxToPlay = new JCheckBox("Play after convert");
        chckbxToPlay.setSelected(true);
        chckbxToPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chckbxToPlay.setFont(new Font("Dialog", Font.PLAIN, 12));
        chckbxToPlay.setForeground(new Color(255, 255, 255));
        chckbxToPlay.setBackground(getJmpBackColor());
        chckbxToPlay.setBounds(12, 177, 135, 21);
        contentPanel.add(chckbxToPlay);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(getJmpBackColor());
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                convertButton = new JButton("Convert");
                convertButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        SystemManager system = JMPCore.getSystemManager();
                        LanguageManager lm = JMPCore.getLanguageManager();
                        String inpath = textFieldInputFile.getText();
                        if (Utility.isExsistFile(inpath) == false) {
                            lblStatus.setForeground(Color.RED);
                            String name = String.format("\"%s\"", lm.getLanguageStr(LangID.Input_file));
                            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
                            return;
                        }

                        system.setFFmpegWrapperPath(textFieldFFmpegExePath.getText());
                        if (system.isValidFFmpegWrapper() == false) {
                            lblStatus.setForeground(Color.RED);
                            String name = String.format("\"%s\"", lm.getLanguageStr(LangID.FFmpeg_path));
                            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
                            return;
                        }
                        String inname = Utility.getFileNameNotExtension(inpath);
                        File outdir = new File(textFieldOutputDirectory.getText());

                        String outpath = Utility.pathCombin(outdir.getPath(), (inname + ".wav"));

                        File in = new File(inpath);
                        File out = new File(outpath);

                        system.setFFmpegWrapperCallback(new IProcessingCallback() {

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

                                String name = Utility.getFileNameAndExtension(out);
                                lblStatus.setText(lm.getLanguageStr(LangID.Conversion_completed) + "(" + name + ")");
                                repaint();

                                if (chckbxToPlay.isSelected() == true) {
                                    JMPFlags.LoadToPlayFlag = true;
                                    JMPCore.getSystemManager().loadFile(out.getPath());
                                }
                            }

                            @Override
                            public void begin() {
                                lblStatus.setForeground(Color.LIGHT_GRAY);
                                lblStatus.setText(lm.getLanguageStr(LangID.Now_converting));
                                repaint();

                                convertButton.setEnabled(false);
                            }
                        });

                        try {
                            system.executeConvert(in.getPath(), out.getPath());
                        }
                        catch (IOException e1) {
                            lblStatus.setForeground(Color.RED);
                            lblStatus.setText("Faild.");
                        }
                    }
                });

                btnExpButton = new JButton("Exploler");
                btnExpButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        File outdir = new File(textFieldOutputDirectory.getText());
                        if (outdir.exists() == true) {
                            try {
                                Utility.openExproler(outdir);
                            }
                            catch (IOException e2) {
                            }
                        }
                    }
                });
                buttonPane.add(btnExpButton);
                convertButton.setActionCommand("OK");
                buttonPane.add(convertButton);
                getRootPane().setDefaultButton(convertButton);
            }
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
        JMPCore.getDataManager().setFFmpegPath(path);

        textFieldFFmpegExePath.setText(JMPCore.getDataManager().getFFmpegPath());
    }

    public void setOutputDirectory(String path) {
        JMPCore.getDataManager().setFFmpegOutputPath(path);

        textFieldOutputDirectory.setText(JMPCore.getDataManager().getFFmpegOutputPath());
    }

    public void setInputPath(String path) {
        textFieldInputFile.setText(path);
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            textFieldFFmpegExePath.setText(JMPCore.getDataManager().getFFmpegPath());
            textFieldOutputDirectory.setText(JMPCore.getDataManager().getFFmpegOutputPath());
            chckbxLeave.setSelected(JMPCore.getDataManager().isFFmpegLeaveOutputFile());
        }
        super.setVisible(b);
    }

    /**
     * ロードアイテム受信
     *
     * @param item
     *            アイテム
     */
    public void catchLoadItem(Object item) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) item;

        // 一番先頭のファイルを取得
        if ((files != null) && (files.size() > 0)) {
            String path = files.get(0).getPath();
            File file = new File(path);
            if (file.exists() == true) {
                if (Utility.checkExtension(file, "exe") == true) {
                    setExePath(file.getPath());
                }
                else {
                    if (file.isDirectory() == true) {
                        setOutputDirectory(file.getPath());
                    }
                    else {
                        setInputPath(file.getPath());
                    }
                }
            }
        }
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();

        setTitle(lm.getLanguageStr(LangID.FFmpeg_converter));
        lblPath.setText(lm.getLanguageStr(LangID.FFmpeg_path));
        lblOutputDirectory.setText(lm.getLanguageStr(LangID.Output_directory));
        lblInputFile.setText(lm.getLanguageStr(LangID.Input_file));
        convertButton.setText(lm.getLanguageStr(LangID.Convert));
        btnExpButton.setText(lm.getLanguageStr(LangID.Open_with_Explorer));
        chckbxToPlay.setText(lm.getLanguageStr(LangID.Play_after_convert));
    }

    /**
     *
     * ドラッグ＆ドロップハンドラー
     *
     */
    public class DropFileHandler extends TransferHandler {
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
                catchLoadItem(t.getTransferData(DataFlavor.javaFileListFlavor));
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
        }
    }
}
